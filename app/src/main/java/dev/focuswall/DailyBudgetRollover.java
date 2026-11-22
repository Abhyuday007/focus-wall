package dev.focuswall;

final class DailyBudgetRollover {
    static final class State {
        final int activeMinutes;
        final boolean armed;
        final long watchingStarted;

        State(int activeMinutes, boolean armed, long watchingStarted) {
            this.activeMinutes = activeMinutes;
            this.armed = armed;
            this.watchingStarted = watchingStarted;
        }
    }

    static State forNewDay(boolean hasPreviousDay, int scheduledMinutes,
            boolean wasWatching, long now) {
        boolean armed = hasPreviousDay;
        return new State(scheduledMinutes, armed, armed && wasWatching ? now : 0);
    }

    static boolean armExistingUser(boolean onboardingComplete, boolean alreadyArmed) {
        return alreadyArmed || onboardingComplete;
    }

    private DailyBudgetRollover() {}
}
