package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SurfaceDebouncerTest {
    @Test public void feedNeedsTwoMatchingHighConfidenceObservations() {
        SurfaceDebouncer debouncer = new SurfaceDebouncer();
        DetectionResult feed = new DetectionResult(DetectionResult.Surface.INSTAGRAM_REELS,
                DetectionResult.Confidence.HIGH, "fixture");
        assertNull(debouncer.accept(feed));
        assertTrue(debouncer.hasPendingFeed());
        assertEquals(feed, debouncer.accept(feed));
        assertFalse(debouncer.hasPendingFeed());
    }

    @Test public void safeSurfaceIsAcceptedImmediately() {
        SurfaceDebouncer debouncer = new SurfaceDebouncer();
        DetectionResult safe = new DetectionResult(DetectionResult.Surface.INSTAGRAM_SAFE,
                DetectionResult.Confidence.HIGH, "fixture");
        assertEquals(safe, debouncer.accept(safe));
    }

    @Test public void uncertainObservationClearsPendingFeed() {
        SurfaceDebouncer debouncer = new SurfaceDebouncer();
        DetectionResult feed = new DetectionResult(DetectionResult.Surface.YOUTUBE_SHORTS,
                DetectionResult.Confidence.HIGH, "fixture");
        DetectionResult unknown = new DetectionResult(DetectionResult.Surface.UNKNOWN,
                DetectionResult.Confidence.LOW, "fixture");
        assertNull(debouncer.accept(feed));
        assertNull(debouncer.accept(unknown));
        assertNull(debouncer.accept(feed));
        assertTrue(debouncer.hasPendingFeed());
    }
}
