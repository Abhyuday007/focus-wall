package dev.focuswall;

final class FocusStateMachine {
    enum State { IDLE, INSTAGRAM_REELS, YOUTUBE_SHORTS, SHARED_REEL, INSTAGRAM_LIMIT, YOUTUBE_LIMIT, SHARED_LIMIT }
    enum Action { NONE, START_TIMER, STOP_TIMER, SHOW_INSTAGRAM_LIMIT, SHOW_YOUTUBE_LIMIT, SHOW_SHARED_LIMIT }

    private State state = State.IDLE;

    Action onSurface(DetectionResult result, boolean expired) {
        if (result == null || result.confidence != DetectionResult.Confidence.HIGH) return Action.NONE;
        if (result.isSafe()) return goIdle();
        if (result.surface == DetectionResult.Surface.INSTAGRAM_REELS
                || result.surface == DetectionResult.Surface.INSTAGRAM_EXPLORE_REELS) {
            if (state == State.INSTAGRAM_LIMIT) return Action.NONE;
            if (expired) { state = State.INSTAGRAM_LIMIT; return Action.SHOW_INSTAGRAM_LIMIT; }
            if (state != State.INSTAGRAM_REELS) { state = State.INSTAGRAM_REELS; return Action.START_TIMER; }
        }
        if (result.surface == DetectionResult.Surface.YOUTUBE_SHORTS) {
            if (state == State.YOUTUBE_LIMIT) return Action.NONE;
            if (expired) { state = State.YOUTUBE_LIMIT; return Action.SHOW_YOUTUBE_LIMIT; }
            if (state != State.YOUTUBE_SHORTS) { state = State.YOUTUBE_SHORTS; return Action.START_TIMER; }
        }
        return Action.NONE;
    }

    Action onSharedReelAdvance(boolean expired) {
        if (state == State.INSTAGRAM_REELS || state == State.INSTAGRAM_LIMIT) return Action.NONE;
        if (state == State.SHARED_LIMIT) return Action.NONE;
        if (expired) { state = State.SHARED_LIMIT; return Action.SHOW_SHARED_LIMIT; }
        if (state != State.SHARED_REEL) { state = State.SHARED_REEL; return Action.START_TIMER; }
        return Action.NONE;
    }

    Action onBudgetExpired() {
        if (state == State.INSTAGRAM_REELS) { state = State.INSTAGRAM_LIMIT; return Action.SHOW_INSTAGRAM_LIMIT; }
        if (state == State.YOUTUBE_SHORTS) { state = State.YOUTUBE_LIMIT; return Action.SHOW_YOUTUBE_LIMIT; }
        if (state == State.SHARED_REEL) { state = State.SHARED_LIMIT; return Action.SHOW_SHARED_LIMIT; }
        return Action.NONE;
    }

    Action goIdle() {
        boolean wasTiming = isTiming();
        state = State.IDLE;
        return wasTiming ? Action.STOP_TIMER : Action.NONE;
    }

    State state() { return state; }
    boolean isTiming() { return state == State.INSTAGRAM_REELS || state == State.YOUTUBE_SHORTS || state == State.SHARED_REEL; }
    boolean blocksSharedAdvanceDetection() { return state == State.INSTAGRAM_REELS || state == State.INSTAGRAM_LIMIT; }
}
