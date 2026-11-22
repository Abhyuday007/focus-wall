package dev.focuswall;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;

final class DailyBudget {
    private static final String STORE = "daily_budget";
    private static final String DAY = "day";
    private static final String MINUTES = "minutes";
    private static final String SCHEDULED_MINUTES = "scheduled_minutes";
    private static final String USED = "used";
    private static final String ARMED = "armed";
    private static final String WATCHING_STARTED = "watching_started";
    private static final String AUTO_ROLLOVER_VERSION = "auto_rollover_version";
    private static final int CURRENT_AUTO_ROLLOVER_VERSION = 1;
    private static final int MIN_BUDGET_MINUTES = 0;
    private static final int MAX_BUDGET_MINUTES = 60;

    static int minutes(Context context) {
        resetIfNeeded(context);
        int value = prefs(context).getInt(MINUTES, 10);
        return Math.max(MIN_BUDGET_MINUTES, Math.min(MAX_BUDGET_MINUTES, value));
    }
    static boolean setMinutes(Context context, int minutes) {
        resetIfNeeded(context);
        if (prefs(context).getBoolean(ARMED, false)) return false;
        int bounded = bound(minutes);
        prefs(context).edit().putInt(MINUTES, bounded).putInt(SCHEDULED_MINUTES, bounded).apply();
        return true;
    }
    static int scheduledMinutes(Context context) {
        resetIfNeeded(context);
        return bound(prefs(context).getInt(SCHEDULED_MINUTES, prefs(context).getInt(MINUTES, 10)));
    }
    static void setScheduledMinutes(Context context, int minutes) {
        resetIfNeeded(context);
        prefs(context).edit().putInt(SCHEDULED_MINUTES, bound(minutes)).apply();
    }
    static void start(Context context) {
        resetIfNeeded(context);
        SharedPreferences prefs = prefs(context);
        prefs.edit().putBoolean(ARMED, true).putInt(SCHEDULED_MINUTES, bound(prefs.getInt(MINUTES, 10))).apply();
    }
    static boolean isArmed(Context context) { resetIfNeeded(context); return prefs(context).getBoolean(ARMED, false); }
    static void beginWatching(Context context) {
        resetIfNeeded(context);
        SharedPreferences prefs = prefs(context);
        if (prefs.getBoolean(ARMED, false) && prefs.getLong(WATCHING_STARTED, 0) == 0) prefs.edit().putLong(WATCHING_STARTED, System.currentTimeMillis()).apply();
    }
    static void stopWatching(Context context) {
        resetIfNeeded(context);
        SharedPreferences prefs = prefs(context);
        long started = prefs.getLong(WATCHING_STARTED, 0);
        if (started > 0) {
            long elapsed = Math.max(0, System.currentTimeMillis() - started);
            long used = Math.max(0, prefs.getLong(USED, 0));
            long updatedUsed = used > Long.MAX_VALUE - elapsed ? Long.MAX_VALUE : used + elapsed;
            prefs.edit().putLong(USED, updatedUsed).putLong(WATCHING_STARTED, 0).apply();
        }
    }
    static long remainingMillis(Context context) {
        resetIfNeeded(context);
        SharedPreferences prefs = prefs(context);
        long used = prefs.getLong(USED, 0);
        long watchingStarted = prefs.getLong(WATCHING_STARTED, 0);
        if (watchingStarted > 0) used += Math.max(0, System.currentTimeMillis() - watchingStarted);
        return Math.max(0, minutes(context) * 60_000L - used);
    }
    static boolean expired(Context context) { return remainingMillis(context) == 0; }
    private static void resetIfNeeded(Context context) {
        SharedPreferences prefs = prefs(context);
        String today = LocalDate.now().toString();
        String storedDay = prefs.getString(DAY, "");
        if (!today.equals(storedDay)) {
            long now = System.currentTimeMillis();
            int scheduled = bound(prefs.getInt(SCHEDULED_MINUTES, prefs.getInt(MINUTES, 10)));
            DailyBudgetRollover.State rollover = DailyBudgetRollover.forNewDay(
                    !storedDay.isEmpty(), scheduled, prefs.getLong(WATCHING_STARTED, 0) > 0, now);
            prefs.edit().putString(DAY, today).putInt(MINUTES, rollover.activeMinutes)
                    .putInt(SCHEDULED_MINUTES, rollover.activeMinutes).putLong(USED, 0)
                    .putLong(WATCHING_STARTED, rollover.watchingStarted)
                    .putBoolean(ARMED, rollover.armed).apply();
        }
        if (prefs.getInt(AUTO_ROLLOVER_VERSION, 0) < CURRENT_AUTO_ROLLOVER_VERSION) {
            boolean onboardingComplete = context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
                    .getBoolean("complete", false);
            int scheduled = bound(prefs.getInt(SCHEDULED_MINUTES, prefs.getInt(MINUTES, 10)));
            prefs.edit().putInt(SCHEDULED_MINUTES, scheduled)
                    .putBoolean(ARMED, DailyBudgetRollover.armExistingUser(
                            onboardingComplete, prefs.getBoolean(ARMED, false)))
                    .putInt(AUTO_ROLLOVER_VERSION, CURRENT_AUTO_ROLLOVER_VERSION).apply();
        }
    }
    private static int bound(int value) { return Math.max(MIN_BUDGET_MINUTES, Math.min(MAX_BUDGET_MINUTES, value)); }
    private static SharedPreferences prefs(Context context) { return context.getSharedPreferences(STORE, Context.MODE_PRIVATE); }
}
