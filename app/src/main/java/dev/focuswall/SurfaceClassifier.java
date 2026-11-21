package dev.focuswall;

final class SurfaceClassifier {
    DetectionResult classify(String packageName, long versionCode, SurfaceSnapshot snapshot) {
        DetectionRules.Profile profile = DetectionRules.forPackage(packageName);
        if (profile == null) {
            return new DetectionResult(DetectionResult.Surface.OUTSIDE_TARGET,
                    DetectionResult.Confidence.HIGH, "outside_target");
        }
        if (!profile.supports(versionCode)) {
            return new DetectionResult(DetectionResult.Surface.UNKNOWN,
                    DetectionResult.Confidence.LOW, "unsupported_app_version");
        }
        if (!snapshot.rootBelongsToPackage) {
            return new DetectionResult(DetectionResult.Surface.UNKNOWN,
                    DetectionResult.Confidence.LOW, "root_package_mismatch");
        }
        if (DetectionRules.INSTAGRAM.equals(packageName)) {
            if (snapshot.feedVisible) {
                return new DetectionResult(DetectionResult.Surface.INSTAGRAM_REELS,
                        DetectionResult.Confidence.HIGH, "selected_reels_tab");
            }
            if (snapshot.instagramSearchSelected && snapshot.pagerVisible) {
                return new DetectionResult(DetectionResult.Surface.INSTAGRAM_EXPLORE_REELS,
                        DetectionResult.Confidence.HIGH, "explore_tab_fullscreen_pager");
            }
            if (snapshot.knownSafeVisible) {
                return new DetectionResult(DetectionResult.Surface.INSTAGRAM_SAFE,
                        DetectionResult.Confidence.HIGH, "selected_safe_tab");
            }
            return new DetectionResult(DetectionResult.Surface.UNKNOWN,
                    DetectionResult.Confidence.LOW, "instagram_surface_unknown");
        }
        if (snapshot.feedVisible) {
            return new DetectionResult(DetectionResult.Surface.YOUTUBE_SHORTS,
                    DetectionResult.Confidence.HIGH, "shorts_root_present");
        }
        return new DetectionResult(DetectionResult.Surface.YOUTUBE_SAFE,
                DetectionResult.Confidence.HIGH, "youtube_non_shorts_surface");
    }
}
