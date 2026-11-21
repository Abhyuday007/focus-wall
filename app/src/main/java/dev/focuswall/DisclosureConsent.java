package dev.focuswall;

import android.content.Context;

final class DisclosureConsent {
    private static final String STORE = "accessibility_disclosure";
    private static final String ACCEPTED_VERSION = "accepted_version";
    private static final int CURRENT_VERSION = 1;

    static boolean isGranted(Context context) {
        return context.getSharedPreferences(STORE, Context.MODE_PRIVATE)
                .getInt(ACCEPTED_VERSION, 0) == CURRENT_VERSION;
    }

    static void grant(Context context) {
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)
                .edit().putInt(ACCEPTED_VERSION, CURRENT_VERSION).apply();
    }

    static void withdraw(Context context) {
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().clear().apply();
        DailyBudget.stopWatching(context);
    }

    private DisclosureConsent() {}
}
