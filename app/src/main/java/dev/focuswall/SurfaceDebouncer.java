package dev.focuswall;

/** Requires two matching high-confidence feed observations; safe surfaces stop immediately. */
final class SurfaceDebouncer {
    private DetectionResult.Surface pending;
    private int observations;

    DetectionResult accept(DetectionResult result) {
        if (result.confidence != DetectionResult.Confidence.HIGH) {
            clear();
            return null;
        }
        if (result.isSafe()) {
            clear();
            return result;
        }
        if (!result.isFeed()) {
            clear();
            return null;
        }
        if (pending == result.surface) observations++; else {
            pending = result.surface;
            observations = 1;
        }
        if (observations < 2) return null;
        clear();
        return result;
    }

    boolean hasPendingFeed() { return pending != null; }

    void clear() {
        pending = null;
        observations = 0;
    }
}
