package dev.focuswall;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Short startup handoff into the required disclosure and Android Accessibility settings. */
public final class ProtectionSetupActivity extends android.app.Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        boolean consented = DisclosureConsent.isGranted(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = column();
        page.setPadding(dp(24), dp(38), dp(24), dp(28));
        page.setBackgroundColor(getColor(R.color.paper));
        scroll.addView(page);

        page.addView(brandHeader());
        page.addView(gap(18));
        TextView eyebrow = text("BUILD YOUR WALL  ·  ABOUT 1 MINUTE", 11, getColor(R.color.brand_orange));
        eyebrow.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        eyebrow.setLetterSpacing(.11f);
        page.addView(eyebrow);
        page.addView(gap(18));
        TextView heading = text(consented ? "One step left." : "Keep the people.\nRemove the trap.", 34, getColor(R.color.ink));
        heading.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        page.addView(heading);
        page.addView(gap(10));
        page.addView(copy(consented
                ? "Your privacy choice is saved. Enable Focus Wall in Android Accessibility settings so the boundary can work."
                : "Android keeps this permission in Settings. We’ll guide you there after a clear privacy review."));
        page.addView(gap(26));

        page.addView(step("1", "Choose your daily boundary", "Done — your current budget is already saved.", true));
        page.addView(gap(10));
        page.addView(step("2", "Review the privacy disclosure", consented ? "Done — consent is saved on this phone." : "See exactly what Focus Wall can and cannot access.", consented));
        page.addView(gap(10));
        page.addView(step("3", "Enable Focus Wall", "Open Installed apps, select Focus Wall app blocker, then turn it on.", isAccessibilityEnabled()));
        page.addView(gap(26));

        Button continueButton = action(consented ? "Open Accessibility settings" : "Review disclosure & continue");
        continueButton.setOnClickListener(v -> {
            if (consented) startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            else startActivity(new Intent(this, AccessibilityDisclosureActivity.class));
            finish();
        });
        page.addView(continueButton);
        page.addView(gap(10));
        Button later = secondary("Not now — view dashboard");
        later.setOnClickListener(v -> finish());
        page.addView(later);
        page.addView(gap(16));
        TextView reassurance = text("Focus Wall stays inactive until you explicitly consent and enable access.", 13, getColor(R.color.muted));
        reassurance.setGravity(Gravity.CENTER);
        page.addView(reassurance);

        page.setAlpha(0f);
        page.setTranslationY(dp(12));
        page.animate().alpha(1f).translationY(0).setDuration(420).start();
        setContentView(scroll);
    }

    private LinearLayout step(String number, String heading, String detail, boolean complete) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(15), dp(15), dp(15), dp(15));
        row.setBackground(roundRect(getColor(R.color.surface), dp(21), dp(1), getColor(R.color.border)));

        TextView marker = text(complete ? "✓" : number, 15, getColor(complete ? R.color.brand_button_text : R.color.brand_orange));
        marker.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        marker.setGravity(Gravity.CENTER);
        marker.setBackground(roundRect(getColor(complete ? R.color.safe : R.color.accent_wash), dp(16), 0, 0));
        row.addView(marker, new LinearLayout.LayoutParams(dp(38), dp(38)));

        LinearLayout words = column();
        words.setPadding(dp(13), 0, 0, 0);
        TextView title = text(heading, 17, getColor(R.color.ink));
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        words.addView(title);
        words.addView(text(detail, 13, getColor(R.color.muted)));
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        return row;
    }

    private boolean isAccessibilityEnabled() {
        String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.contains(new ComponentName(this, FocusAccessibilityService.class).flattenToString());
    }

    private Button action(String value) { return button(value, getColor(R.color.brand_button), getColor(R.color.brand_button_text)); }
    private Button secondary(String value) { return button(value, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink)); }

    private Button button(String value, int background, int foreground) {
        Button button = new Button(this);
        button.setText(value);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(foreground);
        button.setStateListAnimator(null);
        button.setBackground(roundRect(background, dp(18), 0, 0));
        button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(56)));
        return button;
    }

    private LinearLayout column() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        return view;
    }

    private LinearLayout brandHeader() {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        ImageView mark = new ImageView(this);
        mark.setImageResource(R.drawable.focus_wall_icon_foreground);
        mark.setBackground(roundRect(getColor(R.color.brand_orange), dp(16), 0, 0));
        mark.setContentDescription("Focus Wall");
        row.addView(mark, new LinearLayout.LayoutParams(dp(50), dp(50)));
        TextView name = text("FOCUS WALL", 14, getColor(R.color.ink));
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        name.setLetterSpacing(.08f);
        name.setPadding(dp(13), 0, 0, 0);
        row.addView(name);
        return row;
    }

    private TextView copy(String value) { return text(value, 16, getColor(R.color.muted)); }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
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

    private View gap(int value) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value)));
        return view;
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
