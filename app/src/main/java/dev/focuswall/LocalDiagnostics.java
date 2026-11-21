package dev.focuswall;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;

/** Sanitized ring buffer: never stores node text, account names, URLs, or media. */
final class LocalDiagnostics {
    private static final String STORE = "detection_diagnostics";
    private static final String EVENTS = "events";
    private static final int MAX_EVENTS = 40;

    static void record(Context context, String packageName, long versionCode,
            DetectionResult result, FocusStateMachine.Action action, FocusStateMachine.State state) {
        String safePackage = DetectionRules.forPackage(packageName) == null ? "other" : packageName;
        String line = Instant.now() + " | rules=" + DetectionRules.RULESET_VERSION
                + " | package=" + safePackage
                + " | version=" + versionCode
                + " | surface=" + result.surface
                + " | confidence=" + result.confidence
                + " | reason=" + result.reason
                + " | action=" + action
                + " | state=" + state;
        SharedPreferences prefs = context.getSharedPreferences(STORE, Context.MODE_PRIVATE);
        String existing = prefs.getString(EVENTS, "");
        String[] lines = existing.isEmpty() ? new String[0] : existing.split("\n");
        StringBuilder updated = new StringBuilder();
        int start = Math.max(0, lines.length - (MAX_EVENTS - 1));
        for (int i = start; i < lines.length; i++) {
            if (!lines[i].isEmpty()) updated.append(lines[i]).append('\n');
        }
        updated.append(line);
        prefs.edit().putString(EVENTS, updated.toString()).apply();
    }

    static String read(Context context) {
        return context.getSharedPreferences(STORE, Context.MODE_PRIVATE)
                .getString(EVENTS, "No detector events recorded yet.");
    }

    static void clear(Context context) {
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().clear().apply();
    }

    private LocalDiagnostics() {}
}
