package dev.focuswall;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DetectionStatusActivity extends Activity {
    private TextView diagnostics;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(24), dp(36), dp(24), dp(28)); page.setBackgroundColor(getColor(R.color.paper)); scroll.addView(page);
        page.addView(label("DETECTOR STATUS")); page.addView(gap(12)); page.addView(title("Safe when uncertain.")); page.addView(gap(10)); page.addView(copy("Focus Wall acts only after two matching, high-confidence feed observations. Unknown screens are recorded locally and left untouched.")); page.addView(gap(22));
        page.addView(profileCard(DetectionRules.INSTAGRAM_PROFILE)); page.addView(gap(12)); page.addView(profileCard(DetectionRules.YOUTUBE_PROFILE)); page.addView(gap(22));
        page.addView(label("LOCAL DIAGNOSTICS")); page.addView(gap(8)); diagnostics = copy(LocalDiagnostics.read(this)); diagnostics.setTextSize(12); diagnostics.setTypeface(Typeface.MONOSPACE); diagnostics.setTextIsSelectable(true); page.addView(diagnostics); page.addView(gap(14));
        Button clear = secondary("Clear local diagnostics"); clear.setOnClickListener(v -> { LocalDiagnostics.clear(this); diagnostics.setText(LocalDiagnostics.read(this)); }); page.addView(clear); page.addView(gap(10));
        Button done = action("Done"); done.setOnClickListener(v -> finish()); page.addView(done); setContentView(scroll);
    }

    private LinearLayout profileCard(DetectionRules.Profile profile) {
        InstalledAppVersion installed = InstalledAppVersion.read(this, profile.packageName);
        boolean supported = profile.supports(installed.code);
        LinearLayout card = card(); card.addView(label(profile == DetectionRules.INSTAGRAM_PROFILE ? "INSTAGRAM" : "YOUTUBE")); card.addView(gap(8));
        TextView status = copy(supported ? "Supported" : "Not verified — detector will safely do nothing"); status.setTextSize(20); status.setTextColor(getColor(supported ? R.color.safe : R.color.ink)); card.addView(status); card.addView(gap(8));
        card.addView(copy("Installed: " + installed.name + " (" + installed.code + ")\nTested: " + profile.testedVersionName + "\nRule set: " + DetectionRules.RULESET_VERSION)); return card;
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
