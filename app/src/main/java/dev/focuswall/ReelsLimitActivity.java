package dev.focuswall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReelsLimitActivity extends Activity {
    private LimitExitPolicy.Destination safeDestination;
    private OnBackInvokedCallback backCallback;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        boolean sharedReel = getIntent().getBooleanExtra("shared_reel", false);
        boolean youtubeShorts = getIntent().getBooleanExtra("youtube_shorts", false);
        safeDestination = LimitExitPolicy.destination(youtubeShorts);
        setFinishOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backCallback = this::returnToSafeSurface;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
        }
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setGravity(Gravity.CENTER); page.setPadding(dp(28), dp(28), dp(28), dp(28)); page.setBackgroundColor(getColor(R.color.paper));
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(24), dp(26), dp(24), dp(24));
        GradientDrawable panel = new GradientDrawable(); panel.setColor(getColor(R.color.hero_surface)); panel.setCornerRadius(dp(28)); card.setBackground(panel);
        ImageView mark = new ImageView(this); mark.setImageResource(R.drawable.focus_wall_icon_foreground); mark.setContentDescription("Focus Wall");
        TextView eyebrow = text("FOCUS WALL  ·  WALL UP", 12, getColor(R.color.ink)); eyebrow.setLetterSpacing(.11f);
        String headingText = sharedReel
                ? "That was the Reel you came for."
                : youtubeShorts
                ? "Shorts are behind the wall."
                : "Reels are behind the wall.";
        String messageText = sharedReel
                ? "The next Reel is intentionally held back. Stories, DMs, notifications, posts, and the rest of Instagram stay open."
                : youtubeShorts
                ? "Today’s Shorts time is complete. YouTube Home, Search, subscriptions, and regular videos stay open."
                : "Your " + DailyBudget.minutes(this) + " minutes are complete. Stories, DMs, notifications, posts, and story creation stay open.";
        TextView heading = text(headingText, 28, getColor(R.color.ink));
        TextView message = text(messageText, 16, getColor(R.color.ink));
        Button close = new Button(this); close.setText(LimitExitPolicy.buttonLabel(youtubeShorts)); close.setAllCaps(false); close.setTextSize(16); close.setTextColor(getColor(R.color.brand_button_text)); GradientDrawable button = new GradientDrawable(); button.setColor(getColor(R.color.brand_button)); button.setCornerRadius(dp(18)); close.setBackground(button); close.setOnClickListener(v -> returnToSafeSurface());
        card.addView(mark, new LinearLayout.LayoutParams(dp(64), dp(64))); card.addView(gap(10)); card.addView(eyebrow); card.addView(gap(12)); card.addView(heading); card.addView(gap(14)); card.addView(message); card.addView(gap(22));
        if (!youtubeShorts) {
            Button search = new Button(this); search.setText("Search people"); search.setAllCaps(false); search.setTextSize(16); search.setTextColor(getColor(R.color.ink)); GradientDrawable searchButton = new GradientDrawable(); searchButton.setColor(getColor(R.color.surface)); searchButton.setStroke(dp(1), getColor(R.color.ink)); searchButton.setCornerRadius(dp(18)); search.setBackground(searchButton); search.setOnClickListener(v -> openInstagramSearch());
            card.addView(search, new LinearLayout.LayoutParams(-1, dp(54)));
            card.addView(gap(10));
        }
        card.addView(close, new LinearLayout.LayoutParams(-1, dp(54)));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));
        setContentView(page);
    }

    @Override @SuppressWarnings("deprecation") public void onBackPressed() {
        returnToSafeSurface();
    }

    @Override protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && backCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backCallback);
        }
        super.onDestroy();
    }

    private void returnToSafeSurface() {
        if (safeDestination == LimitExitPolicy.Destination.YOUTUBE_HOME) openYouTubeHome();
        else openInstagramHome();
    }
    private TextView text(String value, int size, int color) { TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(color); view.setLineSpacing(dp(5), 1f); return view; }
    private void openInstagramHome() {
        Intent home = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
        home.setPackage("com.instagram.android");
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (home.resolveActivity(getPackageManager()) != null) {
            startActivity(home);
            finish();
        } else finishAndRemoveTask();
    }
    private void openInstagramSearch() {
        Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://search"));
        search.setPackage("com.instagram.android");
        search.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (search.resolveActivity(getPackageManager()) != null) {
            startActivity(search);
            finish();
        } else openInstagramHome();
    }
    private void openYouTubeHome() {
        Intent home = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));
        home.setPackage("com.google.android.youtube");
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (home.resolveActivity(getPackageManager()) != null) {
            startActivity(home);
            finish();
        } else finishAndRemoveTask();
    }
    private View gap(int value) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(value))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
