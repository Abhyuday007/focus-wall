package dev.focuswall;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;

/** Brand wall showing how much of today's short-form budget remains. */
public final class QuotaRingView extends View {
    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progress = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint value = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint caption = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF brick = new RectF();
    private float displayedFraction;
    private long remainingMillis;

    public QuotaRingView(Context context) {
        super(context);
        track.setStyle(Paint.Style.FILL);
        track.setColor(context.getColor(R.color.ring_track));
        progress.setStyle(Paint.Style.FILL);
        progress.setColor(context.getColor(R.color.ink));
        value.setTextAlign(Paint.Align.CENTER);
        value.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
        value.setColor(context.getColor(R.color.ink));
        caption.setTextAlign(Paint.Align.CENTER);
        caption.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        caption.setColor(context.getColor(R.color.ink));
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void setQuota(long remaining, long total) {
        remainingMillis = Math.max(0, remaining);
        float target = total <= 0 ? 0f : Math.min(1f, remainingMillis / (float) total);
        if (Math.abs(target - displayedFraction) < .002f) {
            updateDescription();
            invalidate();
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(displayedFraction, target);
        animator.setDuration(420);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(frame -> {
            displayedFraction = (float) frame.getAnimatedValue();
            invalidate();
        });
        animator.start();
        updateDescription();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rows = 4;
        int columns = 5;
        int totalBricks = rows * columns;
        int filledBricks = Math.round(totalBricks * displayedFraction);
        float gap = dp(6);
        float side = dp(18);
        float top = dp(22);
        float brickHeight = dp(18);
        float brickWidth = (getWidth() - (side * 2) - (gap * (columns - 1))) / columns;
        float radius = dp(5);
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                float left = side + column * (brickWidth + gap);
                float rowTop = top + row * (brickHeight + gap);
                brick.set(left, rowTop, left + brickWidth, rowTop + brickHeight);
                canvas.drawRoundRect(brick, radius, radius, index < filledBricks ? progress : track);
                index++;
            }
        }

        long seconds = remainingMillis / 1000;
        String time = String.format(Locale.US, "%d:%02d", seconds / 60, seconds % 60);
        value.setTextSize(dp(42));
        caption.setTextSize(dp(11));
        float valueY = top + rows * (brickHeight + gap) + dp(52);
        canvas.drawText(time, getWidth() / 2f, valueY, value);
        canvas.drawText("TIME LEFT TODAY", getWidth() / 2f, valueY + dp(25), caption);
    }

    private void updateDescription() {
        long seconds = remainingMillis / 1000;
        setContentDescription(String.format(Locale.US, "%d minutes and %d seconds remaining today", seconds / 60, seconds % 60));
    }

    private float dp(int value) { return value * getResources().getDisplayMetrics().density; }
}
