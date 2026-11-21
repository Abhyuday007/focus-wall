package dev.focuswall;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.ArrayDeque;

/** Applies the budget only after stable, high-confidence native Reels / Shorts detection. */
public class FocusAccessibilityService extends AccessibilityService {
    private static final long INSPECTION_DELAY_MS = 250;
    private static final long CONFIRMATION_DELAY_MS = 180;
    private static final String INSTAGRAM_SEARCH_TAB = DetectionRules.INSTAGRAM + ":id/search_tab";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SurfaceClassifier classifier = new SurfaceClassifier();
    private final SurfaceDebouncer debouncer = new SurfaceDebouncer();
    private final FocusStateMachine stateMachine = new FocusStateMachine();
    private boolean inspectionQueued;
    private InstagramOrigin lastInstagramOrigin = InstagramOrigin.UNKNOWN;

    private enum InstagramOrigin { HOME, DIRECT, SEARCH, PROFILE, UNKNOWN }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!AccessibilityConsentPolicy.shouldProcess(DisclosureConsent.isGranted(this), DailyBudget.isArmed(this))) {
            stopForOutsideTarget();
            return;
        }
        if (event == null || event.getPackageName() == null) {
            stopForOutsideTarget();
            return;
        }
        String packageName = event.getPackageName().toString();
        if (!DetectionRules.INSTAGRAM.equals(packageName) && !DetectionRules.YOUTUBE.equals(packageName)) {
            stopForOutsideTarget();
            return;
        }
        if (DetectionRules.INSTAGRAM.equals(packageName) && isLargePagerAdvance(event)
                && lastInstagramOrigin == InstagramOrigin.SEARCH) {
            DetectionResult result = new DetectionResult(DetectionResult.Surface.INSTAGRAM_EXPLORE_REELS,
                    DetectionResult.Confidence.HIGH, "explore_origin_pager_advance");
            FocusStateMachine.Action action = stateMachine.onSurface(result, DailyBudget.expired(this));
            execute(action);
            LocalDiagnostics.record(this, packageName, installedVersion(packageName), result, action, stateMachine.state());
            return;
        }
        if (DetectionRules.INSTAGRAM.equals(packageName) && isSharedReelAdvance(event)) {
            FocusStateMachine.Action action = stateMachine.onSharedReelAdvance(DailyBudget.expired(this));
            DetectionResult result = new DetectionResult(DetectionResult.Surface.INSTAGRAM_SHARED_ADVANCE,
                    DetectionResult.Confidence.HIGH, "large_viewpager_scroll");
            execute(action);
            LocalDiagnostics.record(this, packageName, installedVersion(packageName), result, action, stateMachine.state());
            return;
        }
        queueInspection(INSPECTION_DELAY_MS);
    }

    private void queueInspection(long delayMs) {
        if (inspectionQueued) return;
        inspectionQueued = true;
        handler.postDelayed(tabInspection, delayMs);
    }

    private final Runnable tabInspection = new Runnable() {
        @Override public void run() {
            inspectionQueued = false;
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root == null || root.getPackageName() == null) return;
            String packageName = root.getPackageName().toString();
            DetectionRules.Profile profile = DetectionRules.forPackage(packageName);
            if (profile == null) { stopForOutsideTarget(); return; }

            boolean feedVisible = DetectionRules.INSTAGRAM.equals(packageName)
                    ? isSelected(root, profile.feedResourceId)
                    : isPresent(root, profile.feedResourceId);
            boolean safeVisible = false;
            for (String safeResourceId : profile.safeResourceIds) {
                if (isSelected(root, safeResourceId)) { safeVisible = true; break; }
            }
            boolean searchSelected = DetectionRules.INSTAGRAM.equals(packageName)
                    && isSelected(root, INSTAGRAM_SEARCH_TAB);
            if (DetectionRules.INSTAGRAM.equals(packageName)) updateInstagramOrigin(root);
            SurfaceSnapshot snapshot = new SurfaceSnapshot(feedVisible, safeVisible,
                    belongsTo(root, packageName), searchSelected, containsPager(root));
            long versionCode = installedVersion(packageName);
            DetectionResult classified = classifier.classify(packageName, versionCode, snapshot);
            DetectionResult stable = debouncer.accept(classified);
            FocusStateMachine.Action action = stable == null
                    ? FocusStateMachine.Action.NONE
                    : stateMachine.onSurface(stable, DailyBudget.expired(FocusAccessibilityService.this));
            execute(action);
            LocalDiagnostics.record(FocusAccessibilityService.this, packageName, versionCode,
                    classified, action, stateMachine.state());
            if (stable == null && debouncer.hasPendingFeed()) queueInspection(CONFIRMATION_DELAY_MS);
        }
    };

    /** A shared Reel advance is a large vertical ViewPager movement outside the native Reels tab. */
    private boolean isSharedReelAdvance(AccessibilityEvent event) {
        return !stateMachine.blocksSharedAdvanceDetection() && isLargePagerAdvance(event);
    }

    private boolean isLargePagerAdvance(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_VIEW_SCROLLED) return false;
        if (event.getClassName() == null
                || !"androidx.viewpager.widget.ViewPager".contentEquals(event.getClassName())) return false;
        return Math.abs(event.getScrollDeltaY()) >= 300;
    }

    private void execute(FocusStateMachine.Action action) {
        if (action == FocusStateMachine.Action.START_TIMER) {
            DailyBudget.beginWatching(this);
            handler.removeCallbacks(tick);
            handler.post(tick);
        } else if (action == FocusStateMachine.Action.STOP_TIMER) {
            handler.removeCallbacks(tick);
            DailyBudget.stopWatching(this);
        } else if (action == FocusStateMachine.Action.SHOW_INSTAGRAM_LIMIT) {
            stopTimerOnly(); showCard(false, false);
        } else if (action == FocusStateMachine.Action.SHOW_YOUTUBE_LIMIT) {
            stopTimerOnly(); showCard(false, true);
        } else if (action == FocusStateMachine.Action.SHOW_SHARED_LIMIT) {
            stopTimerOnly(); showCard(true, false);
        }
    }

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!stateMachine.isTiming()) { DailyBudget.stopWatching(FocusAccessibilityService.this); return; }
            if (DailyBudget.expired(FocusAccessibilityService.this)) {
                FocusStateMachine.Action action = stateMachine.onBudgetExpired();
                execute(action);
                return;
            }
            handler.postDelayed(this, 1_000);
        }
    };

    private void stopTimerOnly() {
        handler.removeCallbacks(tick);
        DailyBudget.stopWatching(this);
    }

    private void stopForOutsideTarget() {
        debouncer.clear();
        FocusStateMachine.Action action = stateMachine.goIdle();
        execute(action);
    }

    private void showCard(boolean sharedReel, boolean youtubeShorts) {
        Intent limit = new Intent(this, ReelsLimitActivity.class);
        limit.putExtra("shared_reel", sharedReel);
        limit.putExtra("youtube_shorts", youtubeShorts);
        limit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(limit);
    }

    private long installedVersion(String packageName) {
        return InstalledAppVersion.read(this, packageName).code;
    }

    private boolean isSelected(AccessibilityNodeInfo root, String resourceId) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(resourceId);
        if (nodes == null) return false;
        for (AccessibilityNodeInfo node : nodes) {
            if (node != null && node.isSelected()) return true;
        }
        return false;
    }

    private boolean isPresent(AccessibilityNodeInfo root, String resourceId) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(resourceId);
        return nodes != null && !nodes.isEmpty();
    }

    private boolean belongsTo(AccessibilityNodeInfo node, String packageName) {
        return node.getPackageName() != null && packageName.contentEquals(node.getPackageName());
    }

    private void updateInstagramOrigin(AccessibilityNodeInfo root) {
        if (isSelected(root, INSTAGRAM_SEARCH_TAB)) lastInstagramOrigin = InstagramOrigin.SEARCH;
        else if (isSelected(root, DetectionRules.INSTAGRAM + ":id/direct_tab")) lastInstagramOrigin = InstagramOrigin.DIRECT;
        else if (isSelected(root, DetectionRules.INSTAGRAM + ":id/feed_tab")) lastInstagramOrigin = InstagramOrigin.HOME;
        else if (isSelected(root, DetectionRules.INSTAGRAM + ":id/profile_tab")) lastInstagramOrigin = InstagramOrigin.PROFILE;
    }

    private boolean containsPager(AccessibilityNodeInfo root) {
        ArrayDeque<AccessibilityNodeInfo> nodes = new ArrayDeque<>();
        nodes.add(root);
        int inspected = 0;
        while (!nodes.isEmpty() && inspected++ < 2_000) {
            AccessibilityNodeInfo node = nodes.removeFirst();
            CharSequence className = node.getClassName();
            if (className != null && ("androidx.viewpager.widget.ViewPager".contentEquals(className)
                    || "androidx.viewpager2.widget.ViewPager2".contentEquals(className))) return true;
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) nodes.addLast(child);
            }
        }
        return false;
    }

    @Override public void onInterrupt() { stopForOutsideTarget(); }
    @Override public void onDestroy() { stopForOutsideTarget(); handler.removeCallbacksAndMessages(null); super.onDestroy(); }
}
