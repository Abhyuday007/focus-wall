package dev.focuswall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Prominent disclosure and affirmative consent required before Accessibility setup. */
public class AccessibilityDisclosureActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        boolean alreadyGranted = DisclosureConsent.isGranted(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(24), dp(36), dp(24), dp(28)); page.setBackgroundColor(getColor(R.color.paper)); scroll.addView(page);

        page.addView(label("ACCESSIBILITY DISCLOSURE")); page.addView(gap(12));
        page.addView(title("Protection needs your permission.")); page.addView(gap(14));
        page.addView(copy("Focus Wall uses Android Accessibility only to recognize supported Instagram Reels and YouTube Shorts screens, measure time spent there, and show your limit card.")); page.addView(gap(18));

        LinearLayout access = card(); access.addView(label("WHAT THE SERVICE ACCESSES")); access.addView(gap(8));
        access.addView(copy("While enabled, Focus Wall receives app and window changes, clicks, and scroll events from Instagram and YouTube. It examines package names, view identifiers, selected states, component class names, and scroll distance to distinguish Reels or Shorts from allowed screens.")); page.addView(access); page.addView(gap(12));

        LinearLayout excluded = card(); excluded.addView(label("WHAT IT DOES NOT ACCESS OR COLLECT")); excluded.addView(gap(8));
        excluded.addView(copy("Focus Wall does not read, record, collect, or share screen text, DMs, Stories, passwords, contacts, media, search terms, account names, or social-media credentials. It never presses Back, performs gestures, changes device settings, or uploads activity.")); page.addView(excluded); page.addView(gap(12));

        LinearLayout storage = card(); storage.addView(label("LOCAL STORAGE")); storage.addView(gap(8));
        storage.addView(copy("Your daily limit and a maximum of 40 sanitized detector events stay in this app’s private storage. Diagnostic events contain only time, supported app package/version, detector result, and action. You can clear them at any time.")); page.addView(storage); page.addView(gap(18));

        Button privacy = secondary("Read privacy policy"); privacy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class))); page.addView(privacy); page.addView(gap(10));
        Button agree = action(alreadyGranted ? "Consent granted — open Settings" : "I agree — open Accessibility settings"); agree.setOnClickListener(v -> { DisclosureConsent.grant(this); startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); finish(); }); page.addView(agree); page.addView(gap(10));
        Button decline = secondary(alreadyGranted ? "Withdraw consent" : "Not now"); decline.setOnClickListener(v -> { DisclosureConsent.withdraw(this); if (alreadyGranted) startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); finish(); }); page.addView(decline);

        setContentView(scroll);
    }

    private LinearLayout card() { LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18), dp(18), dp(18), dp(18)); GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{getColor(R.color.surface), getColor(R.color.surface_tint)}); background.setCornerRadius(dp(24)); card.setBackground(background); return card; }
    private Button action(String value) { return button(value, getColor(R.color.brand_button), getColor(R.color.brand_button_text)); }
    private Button secondary(String value) { return button(value, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink)); }
    private Button button(String value, int backgroundColor, int textColor) { Button button = new Button(this); button.setText(value); button.setAllCaps(false); button.setTextSize(16); button.setTextColor(textColor); button.setGravity(Gravity.CENTER); GradientDrawable background = new GradientDrawable(); background.setColor(backgroundColor); background.setCornerRadius(dp(18)); button.setBackground(background); button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(54))); return button; }
    private TextView label(String value) { TextView view = copy(value); view.setTextSize(12); view.setTypeface(Typeface.DEFAULT, Typeface.BOLD); view.setLetterSpacing(.13f); view.setTextColor(getColor(R.color.brand_orange)); return view; }
    private TextView title(String value) { TextView view = copy(value); view.setTextSize(34); view.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD)); return view; }
    private TextView copy(String value) { TextView view = new TextView(this); view.setText(value); view.setTextSize(16); view.setTextColor(getColor(R.color.ink)); view.setLineSpacing(dp(5), 1f); return view; }
    private View gap(int value) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
