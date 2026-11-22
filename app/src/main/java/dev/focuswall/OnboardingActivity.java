package dev.focuswall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class OnboardingActivity extends Activity {
    private static final String STORE = "onboarding";
    private static final String COMPLETE = "complete";
    private static final String PREVIEW = "preview";
    private final Handler handler = new Handler();
    private int step;
    private int minutes;
    private String intention = "more time for what matters";
    private boolean preview;
    private LinearLayout page;

    static boolean isComplete(Context context) { return context.getSharedPreferences(STORE, MODE_PRIVATE).getBoolean(COMPLETE, false); }
    static Intent previewIntent(Context context) { return new Intent(context, OnboardingActivity.class).putExtra(PREVIEW, true); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        preview = getIntent().getBooleanExtra(PREVIEW, false);
        minutes = DailyBudget.minutes(this);
        page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(28), dp(42), dp(28), dp(28)); page.setBackgroundColor(getColor(R.color.paper));
        setContentView(page); showStep();
    }

    private void showStep() {
        page.removeAllViews();
        page.addView(label((preview ? "SETUP PREVIEW" : "FOCUS WALL") + "  ·  " + (step + 1) + " / 5")); page.addView(gap(20));
        if (step == 0) who(); else if (step == 1) intention(); else if (step == 2) insight(); else if (step == 3) limit(); else commit();
    }

    private void who() {
        page.addView(title("Who is this wall for?")); page.addView(gap(12)); page.addView(copy("Choose the promise you want this phone to help keep.")); page.addView(gap(28));
        option("For me", "A small boundary, on your own terms.", () -> next());
        option("Someone I care about", "A gentler feed without losing the connection.", () -> next());
    }

    private void intention() {
        page.addView(title("What would you like more of?")); page.addView(gap(12)); page.addView(copy("There is no wrong answer. This just makes the boundary feel like yours.")); page.addView(gap(28));
        option("Focus", "More room for work, study, or making things.", () -> { intention = "more focus"; next(); });
        option("Presence", "More attention for people and moments around you.", () -> { intention = "more presence"; next(); });
        option("Rest", "A quieter end to the day.", () -> { intention = "better rest"; next(); });
    }

    private void insight() {
        page.addView(title("Keep " + intention + ".")); page.addView(gap(14)); page.addView(copy("Focus Wall keeps Instagram and YouTube native. Stories, DMs, posts, subscriptions, and search stay available. Only the endless Reels and Shorts feeds respect the boundary you choose.")); page.addView(gap(28));
        Button next = action("Choose my daily limit"); next.setOnClickListener(v -> next()); page.addView(next); back();
    }

    private void limit() {
        TextView value = title(minutes + " minutes"); page.addView(value); page.addView(gap(8)); page.addView(copy("per day for Reels and Shorts")); page.addView(gap(26));
        SeekBar seek = new SeekBar(this); seek.setMax(6); seek.setProgress(minutes / 10); seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) { minutes = progress * 10; value.setText(minutes + " minutes"); } public void onStartTrackingTouch(SeekBar bar) {} public void onStopTrackingTouch(SeekBar bar) {} }); page.addView(seek); page.addView(gap(18)); page.addView(copy("0 minutes is valid. After you commit, this limit auto-arms every day. You can schedule a different future limit without adding time today.")); page.addView(gap(28)); Button next = action("Continue"); next.setOnClickListener(v -> next()); page.addView(next); back();
    }

    private void commit() {
        page.addView(title(preview ? "That’s the flow." : "Lock it in.")); page.addView(gap(12));
        page.addView(copy(preview ? "Nothing will change in preview mode. On a new setup, a one-second hold makes the daily boundary intentional." : "Hold for one second to make today’s " + minutes + " minute boundary real. It repeats automatically each day until you schedule another limit.")); page.addView(gap(32));
        Button hold = action(preview ? "Return without changing anything" : "Press and hold to commit");
        if (preview) hold.setOnClickListener(v -> finish());
        else hold.setOnTouchListener((v, event) -> { if (event.getAction() == MotionEvent.ACTION_DOWN) { hold.setText("Keep holding…"); handler.postDelayed(commit, 1000); return true; } if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) { handler.removeCallbacks(commit); hold.setText("Press and hold to commit"); return true; } return true; });
        page.addView(hold); back();
    }

    private void option(String headline, String detail, Runnable selected) {
        Button choice = secondary(headline + "\n" + detail); choice.setGravity(Gravity.START | Gravity.CENTER_VERTICAL); choice.setPadding(dp(20), 0, dp(20), 0); choice.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); choice.setOnClickListener(v -> selected.run()); page.addView(choice); page.addView(gap(12));
    }
    private void next() { step++; showStep(); }
    private void back() { if (step == 0) return; Button back = secondary("Back"); back.setOnClickListener(v -> { step--; showStep(); }); page.addView(gap(10)); page.addView(back); }
    private final Runnable commit = new Runnable() { @Override public void run() { DailyBudget.setMinutes(OnboardingActivity.this, minutes); DailyBudget.start(OnboardingActivity.this); getSharedPreferences(STORE, MODE_PRIVATE).edit().putBoolean(COMPLETE, true).apply(); startActivity(new Intent(OnboardingActivity.this, MainActivity.class)); finish(); }};
    private Button action(String text) { return button(text, getColor(R.color.mint_deep), getColor(R.color.on_mint)); }
    private Button secondary(String text) { return button(text, getColor(R.color.secondary_surface), getColor(R.color.secondary_ink)); }
    private Button button(String text, int color, int textColor) { Button button = new Button(this); button.setText(text); button.setAllCaps(false); button.setTextSize(16); button.setTextColor(textColor); button.setGravity(Gravity.CENTER); button.setLineSpacing(0, 1.15f); GradientDrawable background = new GradientDrawable(); background.setColor(color); background.setCornerRadius(dp(20)); button.setBackground(background); button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(68))); return button; }
    private TextView label(String text) { TextView view = copy(text); view.setTextSize(12); view.setLetterSpacing(.13f); view.setTextColor(getColor(R.color.mint)); return view; }
    private TextView title(String text) { TextView view = copy(text); view.setTextSize(36); return view; }
    private TextView copy(String text) { TextView view = new TextView(this); view.setText(text); view.setTextSize(17); view.setTextColor(getColor(R.color.ink)); view.setLineSpacing(dp(5), 1f); return view; }
    private View gap(int size) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(size))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
