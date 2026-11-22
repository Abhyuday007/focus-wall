package dev.focuswall;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;

/** Calm, glanceable representation of today's remaining short-form budget. */
public final class QuotaRingView extends View {
    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progress = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint value = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint caption = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arc = new RectF();
    private float displayedFraction;
    private long remainingMillis;

    public QuotaRingView(Context context) {
        super(context);
        track.setStyle(Paint.Style.STROKE);
        track.setStrokeCap(Paint.Cap.ROUND);
        track.setColor(context.getColor(R.color.ring_track));
        progress.setStyle(Paint.Style.STROKE);
        progress.setStrokeCap(Paint.Cap.ROUND);
        progress.setColor(context.getColor(R.color.mint));
        value.setTextAlign(Paint.Align.CENTER);
        value.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
        value.setColor(context.getColor(R.color.ink));
        caption.setTextAlign(Paint.Align.CENTER);
        caption.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        caption.setColor(context.getColor(R.color.muted));
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
        float stroke = dp(13);
        track.setStrokeWidth(stroke);
        progress.setStrokeWidth(stroke);
        float inset = stroke / 2f + dp(8);
        arc.set(inset, inset, getWidth() - inset, getHeight() - inset);
        canvas.drawArc(arc, -90, 360, false, track);
        if (displayedFraction > 0) canvas.drawArc(arc, -90, 360 * displayedFraction, false, progress);

        long seconds = remainingMillis / 1000;
        String time = String.format(Locale.US, "%d:%02d", seconds / 60, seconds % 60);
        value.setTextSize(dp(38));
        caption.setTextSize(dp(12));
        float centerY = getHeight() / 2f;
        canvas.drawText(time, getWidth() / 2f, centerY + dp(5), value);
        canvas.drawText("MINUTES LEFT", getWidth() / 2f, centerY + dp(31), caption);
    }

    private void updateDescription() {
        long seconds = remainingMillis / 1000;
        setContentDescription(String.format(Locale.US, "%d minutes and %d seconds remaining today", seconds / 60, seconds % 60));
    }

    private float dp(int value) { return value * getResources().getDisplayMetrics().density; }
}
