package dev.focuswall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PrivacyPolicyActivity extends Activity {
    static final String PUBLIC_POLICY_URL = "https://abhyuday007.github.io/focus-wall/privacy.html";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(24), dp(36), dp(24), dp(28)); page.setBackgroundColor(getColor(R.color.paper)); scroll.addView(page);
        page.addView(label("PRIVACY POLICY")); page.addView(gap(12)); page.addView(title("Local means local.")); page.addView(gap(10)); page.addView(copy("Effective 21 November 2026")); page.addView(gap(22));
        section(page, "DATA COLLECTION", "Focus Wall does not collect, transmit, sell, or share personal data. It has no account system, advertising, analytics, cloud database, crash-reporting SDK, or social-media login.");
        section(page, "ACCESSIBILITY", "With your explicit consent, the Accessibility service processes app/window changes, clicks, scroll events, package names, view identifiers, selected states, class names, and scroll distance on the device to identify supported Instagram Reels and YouTube Shorts surfaces. It does not read or store screen text, DMs, Stories, passwords, media, contacts, search terms, or account content.");
        section(page, "ON-DEVICE STORAGE", "The daily limit, onboarding state, consent record, and up to 40 sanitized detector events are stored in private app preferences. Detector events contain only time, supported app package/version, detector result, and action. They remain until cleared in the diagnostics screen, app data is cleared, or the app is uninstalled. Android cloud backup and device-transfer extraction are disabled.");
        section(page, "NETWORK AND THIRD PARTIES", "Focus Wall requests no Internet permission. It can open Instagram, YouTube, Android Settings, and this public privacy-policy page through the operating system. Those destinations operate under their own privacy policies. Focus Wall does not send them diagnostic or usage data.");
        section(page, "YOUR CONTROLS", "You can withdraw Accessibility consent from the disclosure screen, disable the service in Android Settings, clear local diagnostics, clear app data, or uninstall the app. Focus Wall has no server-side account or data to delete.");
        section(page, "CHILDREN", "Focus Wall is a general attention-management utility and is not directed to children under 13. It is not a parental-control or medical application.");
        section(page, "CONTACT", "Developer: Abhyuday Purohit\nPrivacy contact: abhyuday.purohit007@gmail.com");
        Button publicPolicy = secondary("Open public policy"); publicPolicy.setOnClickListener(v -> open(Uri.parse(PUBLIC_POLICY_URL))); page.addView(publicPolicy); page.addView(gap(10));
        Button email = secondary("Email privacy contact"); email.setOnClickListener(v -> open(Uri.parse("mailto:abhyuday.purohit007@gmail.com?subject=Focus%20Wall%20privacy"))); page.addView(email); page.addView(gap(10));
        Button done = action("Done"); done.setOnClickListener(v -> finish()); page.addView(done);
        setContentView(scroll);
    }

    private void section(LinearLayout page, String heading, String body) { LinearLayout card = card(); card.addView(label(heading)); card.addView(gap(8)); card.addView(copy(body)); page.addView(card); page.addView(gap(12)); }
    private void open(Uri uri) { Intent intent = new Intent(Intent.ACTION_VIEW, uri); if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent); }
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
