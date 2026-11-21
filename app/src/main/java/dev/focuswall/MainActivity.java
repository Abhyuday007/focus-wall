package dev.focuswall;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    private TextView status;
    private TextView budgetLabel;
    private TextView remainingLabel;
    private SeekBar slider;
    private Button start;
    private final Handler handler = new Handler();
    private final Runnable refresh = new Runnable() { @Override public void run() { renderState(); handler.postDelayed(this, 1_000); } };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!OnboardingActivity.isComplete(this) && !DailyBudget.isArmed(this)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(24), dp(34), dp(24), dp(28)); page.setBackgroundColor(getColor(R.color.paper)); scroll.addView(page);
        page.addView(label("FOCUS WALL")); page.addView(gap(12)); page.addView(title("Quiet social.\nStill yours.")); page.addView(gap(12)); page.addView(copy("Instagram stays native. Focus Wall only manages the time you intentionally choose for Reels and Shorts.")); page.addView(gap(24));
        LinearLayout budget = card(); budget.addView(label("DAILY REELS BUDGET")); budget.addView(gap(9)); budgetLabel = title(""); budgetLabel.setTextSize(22); budget.addView(budgetLabel);
        slider = new SeekBar(this); slider.setMax(6); slider.setProgress(DailyBudget.minutes(this) / 10); slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { public void onProgressChanged(SeekBar bar, int progress, boolean user) { if (user) { DailyBudget.setMinutes(MainActivity.this, progress * 10); renderState(); } } public void onStartTrackingTouch(SeekBar bar) {} public void onStopTrackingTouch(SeekBar bar) {} }); budget.addView(slider); budget.addView(copy("Choose 0, 10, 20, 30, 40, 50, or 60 minutes before arming. The choice locks until tomorrow.")); page.addView(budget); page.addView(gap(12));
        LinearLayout guardrail = card(); guardrail.addView(label("TODAY")); guardrail.addView(gap(8)); remainingLabel = title(""); remainingLabel.setTextSize(22); guardrail.addView(remainingLabel); guardrail.addView(gap(8)); status = copy(""); guardrail.addView(status); page.addView(guardrail); page.addView(gap(20));
        start = action("Arm daily reel budget"); start.setOnClickListener(v -> { DailyBudget.start(this); renderState(); }); page.addView(start); page.addView(gap(10));
        Button settings = secondary("Accessibility settings"); settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))); page.addView(settings); page.addView(gap(18)); page.addView(copy("The timer only counts after you intentionally enter Instagram Reels or YouTube Shorts. Stories, DMs, the main feed, shared reels, and time away from Instagram do not use your budget."));
        page.addView(gap(18)); LinearLayout account = card(); account.addView(label("ACCOUNT & BACKUP")); account.addView(gap(8)); account.addView(copy("Using Focus Wall locally")); account.addView(gap(12)); Button manageAccount = secondary("Account & backup"); manageAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class))); account.addView(manageAccount); page.addView(account); page.addView(gap(10));
        Button previewSetup = secondary("Preview setup experience"); previewSetup.setOnClickListener(v -> startActivity(OnboardingActivity.previewIntent(this))); page.addView(previewSetup);
        page.addView(gap(10)); Button detectorStatus = secondary("Detector compatibility & diagnostics"); detectorStatus.setOnClickListener(v -> startActivity(new Intent(this, DetectionStatusActivity.class))); page.addView(detectorStatus);
        page.setAlpha(0f); page.setTranslationY(dp(16)); page.animate().alpha(1f).translationY(0).setDuration(480).start(); setContentView(scroll);
    }
    @Override protected void onResume() { super.onResume(); handler.post(refresh); }
    @Override protected void onPause() { super.onPause(); handler.removeCallbacks(refresh); }
    private void renderState() {
        int minutes = DailyBudget.minutes(this); long remaining = DailyBudget.remainingMillis(this); budgetLabel.setText(minutes + " minutes per day"); remainingLabel.setText(format(remaining) + " left today");
        boolean enabled = isAccessibilityEnabled(); boolean armed = DailyBudget.isArmed(this);
        slider.setEnabled(!armed); slider.setAlpha(armed ? .55f : 1f); slider.setProgress(minutes / 10);
        start.setEnabled(!armed); start.setAlpha(armed ? .55f : 1f); start.setText(armed ? "Daily budget locked until tomorrow" : "Arm daily reel budget");
        status.setText(!enabled ? "Turn on the local Accessibility permission to show the limit card." : armed ? "Armed and locked — time starts only when you enter Reels or Shorts." : "Choose a budget, then arm it when you are ready."); status.setTextColor(getColor(enabled ? R.color.mint : R.color.ink));
    }
    private String format(long millis) { long seconds = millis / 1000; return String.format(java.util.Locale.US, "%d:%02d", seconds / 60, seconds % 60); }
    private void openNativeApp(String packageName) { Intent intent = getPackageManager().getLaunchIntentForPackage(packageName); if (intent != null) startActivity(intent); }
    private boolean isAccessibilityEnabled() { String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES); return enabled != null && enabled.contains(new ComponentName(this, FocusAccessibilityService.class).flattenToString()); }
    private LinearLayout card() { LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18), dp(18), dp(18), dp(18)); GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{getColor(R.color.surface), getColor(R.color.surface_tint)}); background.setCornerRadius(dp(24)); card.setBackground(background); return card; }
    private Button action(String value) { return button(value, getColor(R.color.mint_deep), getColor(R.color.on_mint)); }
    private Button secondary(String value) { return button(value, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink)); }
    private Button button(String value, int backgroundColor, int textColor) { Button button = new Button(this); button.setText(value); button.setAllCaps(false); button.setTextSize(16); button.setTextColor(textColor); button.setGravity(Gravity.CENTER); GradientDrawable background = new GradientDrawable(); background.setColor(backgroundColor); background.setCornerRadius(dp(18)); button.setBackground(background); button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(54))); return button; }
    private TextView label(String value) { TextView view = copy(value); view.setTextSize(12); view.setLetterSpacing(.13f); view.setTextColor(getColor(R.color.mint)); return view; }
    private TextView title(String value) { TextView view = copy(value); view.setTextSize(34); view.setTextColor(getColor(R.color.ink)); return view; }
    private TextView copy(String value) { TextView view = new TextView(this); view.setText(value); view.setTextSize(16); view.setTextColor(getColor(R.color.ink)); view.setLineSpacing(dp(5), 1f); return view; }
    private View gap(int value) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
