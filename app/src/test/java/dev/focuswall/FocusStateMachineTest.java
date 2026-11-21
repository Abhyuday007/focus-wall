package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FocusStateMachineTest {
    private static DetectionResult result(DetectionResult.Surface surface) {
        return new DetectionResult(surface, DetectionResult.Confidence.HIGH, "fixture");
    }

    @Test public void reelsStartsOnceAndKnownSafeStopsOnce() {
        FocusStateMachine machine = new FocusStateMachine();
        assertEquals(FocusStateMachine.Action.START_TIMER,
                machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_REELS), false));
        assertEquals(FocusStateMachine.Action.NONE,
                machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_REELS), false));
        assertEquals(FocusStateMachine.Action.STOP_TIMER,
                machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_SAFE), false));
        assertEquals(FocusStateMachine.Action.NONE,
                machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_SAFE), false));
    }

    @Test public void expiredFeedShowsOneCardUntilSafeSurface() {
        FocusStateMachine machine = new FocusStateMachine();
        assertEquals(FocusStateMachine.Action.SHOW_YOUTUBE_LIMIT,
                machine.onSurface(result(DetectionResult.Surface.YOUTUBE_SHORTS), true));
        assertEquals(FocusStateMachine.Action.NONE,
                machine.onSurface(result(DetectionResult.Surface.YOUTUBE_SHORTS), true));
        assertEquals(FocusStateMachine.Action.NONE,
                machine.onSurface(result(DetectionResult.Surface.YOUTUBE_SAFE), true));
        assertEquals(FocusStateMachine.Action.SHOW_YOUTUBE_LIMIT,
                machine.onSurface(result(DetectionResult.Surface.YOUTUBE_SHORTS), true));
    }

    @Test public void lowConfidenceNeverChangesState() {
        FocusStateMachine machine = new FocusStateMachine();
        DetectionResult uncertain = new DetectionResult(DetectionResult.Surface.INSTAGRAM_REELS,
                DetectionResult.Confidence.LOW, "fixture");
        assertEquals(FocusStateMachine.Action.NONE, machine.onSurface(uncertain, false));
        assertEquals(FocusStateMachine.State.IDLE, machine.state());
    }

    @Test public void expiredExploreReelUsesInstagramFeedLimit() {
        FocusStateMachine machine = new FocusStateMachine();
        assertEquals(FocusStateMachine.Action.SHOW_INSTAGRAM_LIMIT,
                machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_EXPLORE_REELS), true));
    }

    @Test public void sharedAdvanceUsesBudgetAndCannotOverrideNativeReels() {
        FocusStateMachine machine = new FocusStateMachine();
        assertEquals(FocusStateMachine.Action.START_TIMER, machine.onSharedReelAdvance(false));
        assertEquals(FocusStateMachine.Action.SHOW_SHARED_LIMIT, machine.onBudgetExpired());
        assertEquals(FocusStateMachine.Action.NONE, machine.onSharedReelAdvance(true));
        machine.goIdle();
        machine.onSurface(result(DetectionResult.Surface.INSTAGRAM_REELS), false);
        assertEquals(FocusStateMachine.Action.NONE, machine.onSharedReelAdvance(true));
    }
}
