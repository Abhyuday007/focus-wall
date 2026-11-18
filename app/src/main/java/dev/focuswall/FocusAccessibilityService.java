package dev.focuswall;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/** Applies the budget only in native Instagram Reels and YouTube Shorts. */
public class FocusAccessibilityService extends AccessibilityService {
    private static final String INSTAGRAM = "com.instagram.android";
    private static final String YOUTUBE = "com.google.android.youtube";
    private static final String INSTAGRAM_REELS_TAB = INSTAGRAM + ":id/clips_tab";
    private static final String YOUTUBE_SHORTS_ROOT = YOUTUBE + ":id/reel_watch_fragment_root";

    private final Handler handler = new Handler();
    private boolean reelsFeedActive;
    private boolean youtubeShortsActive;
    private boolean sharedReelActive;
    private boolean inspectionQueued;
    private long lastLimitCard;

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) { stopFeedTimer(); return; }
        String packageName = event.getPackageName().toString();
        if (!INSTAGRAM.equals(packageName) && !YOUTUBE.equals(packageName)) {
            stopFeedTimer();
            return;
        }
        if (isSharedReelAdvance(event)) {
            if (DailyBudget.expired(this)) {
                DailyBudget.stopWatching(this);
                sharedReelActive = false;
                showSharedReelCard();
            } else {
                startSharedReelTimer();
            }
            return;
        }
        queueTabInspection();
    }

    private void queueTabInspection() {
        if (inspectionQueued) return;
        inspectionQueued = true;
        handler.postDelayed(tabInspection, 250);
    }

    /**
     * A shared Reel is rendered as a vertical ViewPager. A real upward advance
     * reports a large nonzero Y delta; media/layout updates report zero. This
     * rule is ignored after selecting the native Reels tab, where the daily
     * budget handles the feed instead.
     */
    private boolean isSharedReelAdvance(AccessibilityEvent event) {
        if (reelsFeedActive || event.getEventType() != AccessibilityEvent.TYPE_VIEW_SCROLLED) return false;
        if (event.getClassName() == null
                || !"androidx.viewpager.widget.ViewPager".contentEquals(event.getClassName())) return false;
        return Math.abs(event.getScrollDeltaY()) >= 300;
    }

    private final Runnable tabInspection = new Runnable() {
        @Override public void run() {
            inspectionQueued = false;
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root == null) return;

            boolean reelsSelected = isSelected(root, INSTAGRAM_REELS_TAB);
            boolean youtubeShortsVisible = isPresent(root, YOUTUBE_SHORTS_ROOT);
            if (reelsSelected || youtubeShortsVisible) {
                reelsFeedActive = true;
                youtubeShortsActive = youtubeShortsVisible;
                if (DailyBudget.expired(FocusAccessibilityService.this)) {
                    DailyBudget.stopWatching(FocusAccessibilityService.this);
                    showLimitCard(youtubeShortsVisible);
                } else {
                    startFeedTimer();
                }
                return;
            }

            if (belongsTo(root, YOUTUBE)) {
                stopFeedTimer();
                return;
            }

            // No matching tab is intentionally a no-op. This preserves a Reel
            // opened from a DM or Story, which does not select the Reels tab.
            if (isSelected(root, INSTAGRAM + ":id/feed_tab")
                    || isSelected(root, INSTAGRAM + ":id/direct_tab")
                    || isSelected(root, INSTAGRAM + ":id/search_tab")
                    || isSelected(root, INSTAGRAM + ":id/profile_tab")) {
                stopFeedTimer();
            }
        }
    };

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

    private void startFeedTimer() {
        sharedReelActive = false;
        DailyBudget.beginWatching(this);
        handler.removeCallbacks(tick);
        handler.post(tick);
    }

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!reelsFeedActive && !sharedReelActive) { DailyBudget.stopWatching(FocusAccessibilityService.this); return; }
            if (DailyBudget.expired(FocusAccessibilityService.this)) {
                DailyBudget.stopWatching(FocusAccessibilityService.this);
                boolean wasSharedReel = sharedReelActive;
                boolean wasYouTubeShorts = youtubeShortsActive;
                reelsFeedActive = false;
                sharedReelActive = false;
                youtubeShortsActive = false;
                if (wasSharedReel) showSharedReelCard(); else showLimitCard(wasYouTubeShorts);
                return;
            }
            handler.postDelayed(this, 1_000);
        }
    };

    private void startSharedReelTimer() {
        sharedReelActive = true;
        DailyBudget.beginWatching(this);
        handler.removeCallbacks(tick);
        handler.post(tick);
    }

    private void stopFeedTimer() {
        reelsFeedActive = false;
        youtubeShortsActive = false;
        sharedReelActive = false;
        handler.removeCallbacks(tick);
        DailyBudget.stopWatching(this);
    }

    private void showLimitCard(boolean youtubeShorts) {
        showCard(false, youtubeShorts);
    }

    private void showSharedReelCard() {
        showCard(true, false);
    }

    private void showCard(boolean sharedReel, boolean youtubeShorts) {
        long now = System.currentTimeMillis();
        if (now - lastLimitCard < 2_000) return;
        lastLimitCard = now;
        Intent limit = new Intent(this, ReelsLimitActivity.class);
        limit.putExtra("shared_reel", sharedReel);
        limit.putExtra("youtube_shorts", youtubeShorts);
        limit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(limit);
    }

    @Override public void onInterrupt() { stopFeedTimer(); }
}
