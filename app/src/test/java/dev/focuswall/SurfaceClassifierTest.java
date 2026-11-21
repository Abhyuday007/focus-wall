package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SurfaceClassifierTest {
    private final SurfaceClassifier classifier = new SurfaceClassifier();

    @Test public void instagramReelsRequiresSupportedVersionAndSelectedTab() {
        DetectionResult result = classifier.classify(DetectionRules.INSTAGRAM, 386305570L,
                new SurfaceSnapshot(true, false, true, false, true));
        assertEquals(DetectionResult.Surface.INSTAGRAM_REELS, result.surface);
        assertEquals(DetectionResult.Confidence.HIGH, result.confidence);
    }

    @Test public void unsupportedInstagramVersionFailsSafe() {
        DetectionResult result = classifier.classify(DetectionRules.INSTAGRAM, 386200000L,
                new SurfaceSnapshot(true, false, true, false, true));
        assertEquals(DetectionResult.Surface.UNKNOWN, result.surface);
        assertEquals(DetectionResult.Confidence.LOW, result.confidence);
        assertEquals("unsupported_app_version", result.reason);
    }

    @Test public void instagramUnknownSurfaceDoesNotBecomeSafeOrFeed() {
        DetectionResult result = classifier.classify(DetectionRules.INSTAGRAM, 386305570L,
                new SurfaceSnapshot(false, false, true, false, false));
        assertEquals(DetectionResult.Surface.UNKNOWN, result.surface);
        assertEquals(DetectionResult.Confidence.LOW, result.confidence);
    }

    @Test public void youtubeWithoutShortsRootIsKnownSafe() {
        DetectionResult result = classifier.classify(DetectionRules.YOUTUBE, 1561311006L,
                new SurfaceSnapshot(false, false, true, false, false));
        assertEquals(DetectionResult.Surface.YOUTUBE_SAFE, result.surface);
        assertEquals(DetectionResult.Confidence.HIGH, result.confidence);
    }

    @Test public void mismatchedRootFailsSafe() {
        DetectionResult result = classifier.classify(DetectionRules.YOUTUBE, 1561311006L,
                new SurfaceSnapshot(true, false, false, false, true));
        assertEquals(DetectionResult.Surface.UNKNOWN, result.surface);
        assertEquals("root_package_mismatch", result.reason);
    }

    @Test public void fullscreenPagerFromExploreIsAFeedNotASharedReel() {
        DetectionResult result = classifier.classify(DetectionRules.INSTAGRAM, 386113969L,
                new SurfaceSnapshot(false, true, true, true, true));
        assertEquals(DetectionResult.Surface.INSTAGRAM_EXPLORE_REELS, result.surface);
        assertEquals(DetectionResult.Confidence.HIGH, result.confidence);
    }

    @Test public void exploreGridRemainsSafe() {
        DetectionResult result = classifier.classify(DetectionRules.INSTAGRAM, 386113969L,
                new SurfaceSnapshot(false, true, true, true, false));
        assertEquals(DetectionResult.Surface.INSTAGRAM_SAFE, result.surface);
    }
}
