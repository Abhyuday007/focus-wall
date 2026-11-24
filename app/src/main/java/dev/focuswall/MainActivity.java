package dev.focuswall;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    private TextView protectionBadge;
    private TextView status;
    private TextView budgetLabel;
    private TextView scheduledBudgetLabel;
    private TextView instagramUsage;
    private TextView youtubeUsage;
    private QuotaRingView quotaRing;
    private SeekBar slider;
    private SeekBar scheduledSlider;
    private Button start;
    private boolean startupGuideShown;
    private final Handler handler = new Handler();
    private final Runnable refresh = new Runnable() {
        @Override public void run() { renderState(); handler.postDelayed(this, 1_000); }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!OnboardingActivity.isComplete(this) && !DailyBudget.isArmed(this)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = column();
        page.setPadding(dp(20), dp(28), dp(20), dp(30));
        page.setBackgroundColor(getColor(R.color.paper));
        scroll.addView(page);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(dp(54), dp(54));
        markParams.setMarginEnd(dp(14));
        header.addView(brandMark(), markParams);
        LinearLayout greeting = column();
        greeting.addView(label("FOCUS WALL"));
        greeting.addView(gap(5));
        greeting.addView(title("Keep the people.\nRemove the trap.", 26));
        header.addView(greeting, new LinearLayout.LayoutParams(0, -2, 1));
        protectionBadge = pill("CHECKING");
        header.addView(protectionBadge);
        page.addView(header);
        page.addView(gap(20));

        LinearLayout hero = card(true);
        LinearLayout heroTop = new LinearLayout(this);
        heroTop.setGravity(Gravity.CENTER_VERTICAL);
        TextView heroLabel = label("TODAY'S WALL");
        heroLabel.setTextColor(getColor(R.color.ink));
        heroTop.addView(heroLabel, new LinearLayout.LayoutParams(0, -2, 1));
        heroTop.addView(heroPill("LIVE"));
        hero.addView(heroTop);
        hero.addView(gap(4));
        quotaRing = new QuotaRingView(this);
        LinearLayout.LayoutParams ringParams = new LinearLayout.LayoutParams(dp(250), dp(250));
        ringParams.gravity = Gravity.CENTER_HORIZONTAL;
        hero.addView(quotaRing, ringParams);
        status = copy("");
        status.setGravity(Gravity.CENTER);
        status.setTextColor(getColor(R.color.ink));
        hero.addView(status);
        page.addView(hero);
        page.addView(gap(20));

        sectionHeader(page, "WHAT STAYS OPEN", "The people stay. Only the endless feed meets the wall.");
        page.addView(gap(11));
        page.addView(shieldCard("IG", "Instagram Reels", "Home, Stories and DMs stay open", true));
        page.addView(gap(10));
        page.addView(shieldCard("YT", "YouTube Shorts", "Home, Search and videos stay open", false));
        page.addView(gap(20));

        LinearLayout today = card(false);
        today.addView(label("TODAY'S COMMITMENT"));
        today.addView(gap(8));
        budgetLabel = title("", 24);
        today.addView(budgetLabel);
        today.addView(gap(6));
        slider = budgetSlider();
        slider.setProgress(DailyBudget.minutes(this) / 10);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean user) {
                if (user) { DailyBudget.setMinutes(MainActivity.this, progress * 10); renderState(); }
            }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });
        today.addView(slider);
        today.addView(helper("Once armed, today's limit cannot be increased."));
        page.addView(today);
        page.addView(gap(12));

        LinearLayout schedule = card(false);
        schedule.addView(label("TOMORROW & FUTURE DAYS"));
        schedule.addView(gap(8));
        scheduledBudgetLabel = title("", 24);
        schedule.addView(scheduledBudgetLabel);
        schedule.addView(gap(6));
        scheduledSlider = budgetSlider();
        scheduledSlider.setProgress(DailyBudget.scheduledMinutes(this) / 10);
        scheduledSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean user) {
                if (user) { DailyBudget.setScheduledMinutes(MainActivity.this, progress * 10); renderState(); }
            }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });
        schedule.addView(scheduledSlider);
        schedule.addView(helper("Auto-arms at midnight and repeats until you change it."));
        page.addView(schedule);
        page.addView(gap(16));

        start = action("Build today's wall");
        start.setOnClickListener(v -> { DailyBudget.start(this); renderState(); });
        page.addView(start);
        page.addView(gap(10));

        Button settings = secondary("Review disclosure & protection access");
        settings.setOnClickListener(v -> startActivity(new Intent(this, AccessibilityDisclosureActivity.class)));
        page.addView(settings);
        page.addView(gap(22));

        LinearLayout privateCard = card(false);
        LinearLayout privateHeader = new LinearLayout(this);
        privateHeader.setGravity(Gravity.CENTER_VERTICAL);
        privateHeader.addView(title("Private by design", 20), new LinearLayout.LayoutParams(0, -2, 1));
        privateHeader.addView(pill("ON DEVICE"));
        privateCard.addView(privateHeader);
        privateCard.addView(gap(8));
        privateCard.addView(helper("No account, ads, analytics, cloud database, or social-media login. Your limits and detection stay on this phone."));
        privateCard.addView(gap(14));
        Button privacyPolicy = compactButton("Privacy policy");
        privacyPolicy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        privateCard.addView(privacyPolicy);
        page.addView(privateCard);
        page.addView(gap(14));

        LinearLayout utilityBar = new LinearLayout(this);
        utilityBar.setPadding(dp(8), dp(8), dp(8), dp(8));
        utilityBar.setGravity(Gravity.CENTER);
        utilityBar.setBackground(roundRect(getColor(R.color.nav_surface), dp(24), 0, 0));
        Button setup = navButton("Setup");
        setup.setOnClickListener(v -> startActivity(OnboardingActivity.previewIntent(this)));
        Button diagnostics = navButton("Compatibility");
        diagnostics.setOnClickListener(v -> startActivity(new Intent(this, DetectionStatusActivity.class)));
        utilityBar.addView(setup, new LinearLayout.LayoutParams(0, dp(44), 1));
        utilityBar.addView(diagnostics, new LinearLayout.LayoutParams(0, dp(44), 1));
        page.addView(utilityBar);

        page.setAlpha(0f);
        page.setTranslationY(dp(14));
        page.animate().alpha(1f).translationY(0).setDuration(520)
                .setInterpolator(new DecelerateInterpolator()).start();
        setContentView(scroll);
    }

    @Override protected void onResume() {
        super.onResume();
        handler.post(refresh);
        if (!startupGuideShown && (!DisclosureConsent.isGranted(this) || !isAccessibilityEnabled())) {
            startupGuideShown = true;
            startActivity(new Intent(this, ProtectionSetupActivity.class));
        }
    }
    @Override protected void onPause() { super.onPause(); handler.removeCallbacks(refresh); }

    private void renderState() {
        int minutes = DailyBudget.minutes(this);
        int scheduledMinutes = DailyBudget.scheduledMinutes(this);
        long remaining = DailyBudget.remainingMillis(this);
        boolean consented = DisclosureConsent.isGranted(this);
        boolean enabled = consented && isAccessibilityEnabled();
        boolean armed = DailyBudget.isArmed(this);

        budgetLabel.setText(minutes + " minutes today");
        scheduledBudgetLabel.setText(scheduledMinutes + " minutes daily");
        quotaRing.setQuota(remaining, minutes * 60_000L);
        String usageState = !enabled
                ? "Waiting for protection access"
                : armed ? "Protected · timer ready" : "Waiting for commitment";
        instagramUsage.setText(usageState);
        youtubeUsage.setText(usageState);

        slider.setEnabled(!armed);
        slider.setAlpha(armed ? .38f : 1f);
        slider.setProgress(minutes / 10);
        scheduledSlider.setProgress(scheduledMinutes / 10);
        start.setEnabled(!armed);
        start.setAlpha(armed ? .48f : 1f);
        start.setText(armed ? "Today's wall is built" : "Build today's wall");

        if (!consented) {
            protectionBadge.setText("SETUP NEEDED");
            status.setText("Review the disclosure to turn on protection.");
        } else if (!enabled) {
            protectionBadge.setText("ACCESS OFF");
            status.setText("Permission saved · enable Accessibility protection.");
        } else if (armed) {
            protectionBadge.setText("WALL UP");
            status.setText("Counting only while a supported Reel or Short is visible.");
        } else {
            protectionBadge.setText("READY");
            status.setText("Choose a budget and make today's commitment.");
        }
    }

    private LinearLayout shieldCard(String mark, String name, String detail, boolean instagram) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(15), dp(16), dp(15));
        row.setBackground(roundRect(getColor(R.color.surface), dp(22), dp(1), getColor(R.color.border)));
        TextView icon = new TextView(this);
        icon.setText(mark);
        icon.setTextSize(13);
        icon.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        icon.setTextColor(getColor(R.color.brand_button_text));
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(roundRect(getColor(R.color.brand_button), dp(14), 0, 0));
        row.addView(icon, new LinearLayout.LayoutParams(dp(46), dp(46)));
        LinearLayout words = column();
        words.setPadding(dp(13), 0, dp(8), 0);
        words.addView(title(name, 18));
        TextView detailView = helper(detail);
        detailView.setTextSize(13);
        words.addView(detailView);
        TextView usage = helper("");
        usage.setTextSize(12);
        usage.setTextColor(getColor(R.color.safe));
        words.addView(usage);
        if (instagram) instagramUsage = usage; else youtubeUsage = usage;
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        TextView active = new TextView(this);
        active.setBackground(roundRect(getColor(R.color.safe), dp(7), 0, 0));
        row.addView(active, new LinearLayout.LayoutParams(dp(14), dp(14)));
        return row;
    }

    private void sectionHeader(LinearLayout page, String heading, String detail) {
        page.addView(label(heading));
        TextView subtitle = helper(detail);
        subtitle.setTextSize(13);
        page.addView(subtitle);
    }

    private SeekBar budgetSlider() {
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(6);
        seekBar.setProgressTintList(ColorStateList.valueOf(getColor(R.color.brand_orange)));
        seekBar.setThumbTintList(ColorStateList.valueOf(getColor(R.color.brand_orange)));
        return seekBar;
    }

    private boolean isAccessibilityEnabled() {
        String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.contains(new ComponentName(this, FocusAccessibilityService.class).flattenToString());
    }

    private LinearLayout column() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        return view;
    }

    private LinearLayout card(boolean hero) {
        LinearLayout card = column();
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(roundRect(getColor(hero ? R.color.hero_surface : R.color.surface), dp(26), dp(1), getColor(R.color.border)));
        return card;
    }

    private Button action(String value) { return button(value, getColor(R.color.brand_button), getColor(R.color.brand_button_text), 56); }
    private Button secondary(String value) { return button(value, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink), 54); }
    private Button compactButton(String value) { return button(value, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink), 46); }
    private Button navButton(String value) { return button(value, getColor(R.color.nav_surface), getColor(R.color.secondary_ink), 44); }

    private Button button(String value, int backgroundColor, int textColor, int height) {
        Button button = new Button(this);
        button.setText(value);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(textColor);
        button.setGravity(Gravity.CENTER);
        button.setStateListAnimator(null);
        button.setBackground(roundRect(backgroundColor, dp(18), 0, 0));
        button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(height)));
        return button;
    }

    private TextView pill(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(10);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setLetterSpacing(.08f);
        view.setTextColor(getColor(R.color.brand_orange));
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(10), dp(6), dp(10), dp(6));
        view.setBackground(roundRect(getColor(R.color.accent_wash), dp(14), 0, 0));
        return view;
    }

    private TextView heroPill(String value) {
        TextView view = pill(value);
        view.setTextColor(getColor(R.color.brand_button_text));
        view.setBackground(roundRect(getColor(R.color.brand_button), dp(14), 0, 0));
        return view;
    }

    private TextView label(String value) {
        TextView view = copy(value);
        view.setTextSize(11);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setLetterSpacing(.12f);
        view.setTextColor(getColor(R.color.brand_orange));
        return view;
    }

    private TextView title(String value, int size) {
        TextView view = copy(value);
        view.setTextSize(size);
        view.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        view.setTextColor(getColor(R.color.ink));
        return view;
    }

    private TextView helper(String value) {
        TextView view = copy(value);
        view.setTextSize(14);
        view.setTextColor(getColor(R.color.muted));
        return view;
    }

    private TextView copy(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(16);
        view.setTextColor(getColor(R.color.ink));
        view.setLineSpacing(dp(4), 1f);
        return view;
    }

    private GradientDrawable roundRect(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);
        if (strokeWidth > 0) background.setStroke(strokeWidth, strokeColor);
        return background;
    }

    private ImageView brandMark() {
        ImageView mark = new ImageView(this);
        mark.setImageResource(R.drawable.focus_wall_icon_foreground);
        mark.setBackground(roundRect(getColor(R.color.brand_orange), dp(18), 0, 0));
        mark.setContentDescription("Focus Wall");
        return mark;
    }

    private View gap(int value) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value)));
        return view;
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
