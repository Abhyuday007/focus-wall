package dev.focuswall;

final class DetectionResult {
    enum Surface {
        INSTAGRAM_REELS,
        INSTAGRAM_EXPLORE_REELS,
        INSTAGRAM_SHARED_ADVANCE,
        YOUTUBE_SHORTS,
        INSTAGRAM_SAFE,
        YOUTUBE_SAFE,
        OUTSIDE_TARGET,
        UNKNOWN
    }

    enum Confidence { HIGH, MEDIUM, LOW }

    final Surface surface;
    final Confidence confidence;
    final String reason;

    DetectionResult(Surface surface, Confidence confidence, String reason) {
        this.surface = surface;
        this.confidence = confidence;
        this.reason = reason;
    }

    boolean isFeed() {
        return surface == Surface.INSTAGRAM_REELS
                || surface == Surface.INSTAGRAM_EXPLORE_REELS
                || surface == Surface.YOUTUBE_SHORTS;
    }

    boolean isSafe() {
        return surface == Surface.INSTAGRAM_SAFE
                || surface == Surface.YOUTUBE_SAFE
                || surface == Surface.OUTSIDE_TARGET;
    }
}
