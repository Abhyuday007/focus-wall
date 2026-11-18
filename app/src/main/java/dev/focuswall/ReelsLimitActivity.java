package dev.focuswall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReelsLimitActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        boolean sharedReel = getIntent().getBooleanExtra("shared_reel", false);
        boolean youtubeShorts = getIntent().getBooleanExtra("youtube_shorts", false);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setGravity(Gravity.CENTER); page.setPadding(dp(28), dp(28), dp(28), dp(28)); page.setBackgroundColor(getColor(R.color.paper));
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(24), dp(26), dp(24), dp(24));
        GradientDrawable panel = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{getColor(R.color.surface), getColor(R.color.surface_tint)}); panel.setCornerRadius(dp(28)); card.setBackground(panel);
        TextView eyebrow = text("FOCUS WALL", 12, getColor(R.color.mint)); eyebrow.setLetterSpacing(.13f);
        String headingText = sharedReel
                ? "That shared Reel was your one view."
                : youtubeShorts
                ? "Your daily Shorts budget is over."
                : "Your " + DailyBudget.minutes(this) + " minutes of daily reels are over.";
        String messageText = sharedReel
                ? "You can still use Stories, DMs, notifications, posts, and the rest of Instagram. The next Reel is intentionally held back."
                : youtubeShorts
                ? "YouTube Home, Search, subscriptions, and regular videos are still open."
                : "Stories, DMs, notifications, posts, and story creation are still open. Thank you for protecting your attention.";
        TextView heading = text(headingText, 28, getColor(R.color.ink));
        TextView message = text(messageText, 16, getColor(R.color.ink));
        Button close = new Button(this); close.setText(sharedReel ? "Back to Instagram home" : youtubeShorts ? "Go to YouTube Home" : "Got it"); close.setAllCaps(false); close.setTextSize(16); close.setTextColor(getColor(R.color.on_mint)); GradientDrawable button = new GradientDrawable(); button.setColor(getColor(R.color.mint_deep)); button.setCornerRadius(dp(18)); close.setBackground(button); close.setOnClickListener(v -> { if (sharedReel) openInstagramHome(); else if (youtubeShorts) openYouTubeHome(); else finish(); });
        card.addView(eyebrow); card.addView(gap(12)); card.addView(heading); card.addView(gap(14)); card.addView(message); card.addView(gap(22)); card.addView(close, new LinearLayout.LayoutParams(-1, dp(54)));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        setContentView(page);
    }
    private TextView text(String value, int size, int color) { TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(color); view.setLineSpacing(dp(5), 1f); return view; }
    private void openInstagramHome() {
        Intent home = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
        home.setPackage("com.instagram.android");
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (home.resolveActivity(getPackageManager()) != null) {
            startActivity(home);
            finish();
        }
    }
    private void openYouTubeHome() {
        Intent home = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));
        home.setPackage("com.google.android.youtube");
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (home.resolveActivity(getPackageManager()) != null) {
            startActivity(home);
            finish();
        }
    }
    private View gap(int value) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
