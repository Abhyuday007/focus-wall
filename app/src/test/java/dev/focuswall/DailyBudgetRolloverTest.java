package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DailyBudgetRolloverTest {
    @Test public void firstDayWaitsForIntentionalSetup() {
        DailyBudgetRollover.State state = DailyBudgetRollover.forNewDay(false, 10, false, 500);
        assertFalse(state.armed);
        assertEquals(10, state.activeMinutes);
        assertEquals(0, state.watchingStarted);
    }

    @Test public void nextDayAutoArmsScheduledLimit() {
        DailyBudgetRollover.State state = DailyBudgetRollover.forNewDay(true, 30, false, 500);
        assertTrue(state.armed);
        assertEquals(30, state.activeMinutes);
        assertEquals(0, state.watchingStarted);
    }

    @Test public void midnightWhileWatchingContinuesCounting() {
        DailyBudgetRollover.State state = DailyBudgetRollover.forNewDay(true, 20, true, 500);
        assertTrue(state.armed);
        assertEquals(500, state.watchingStarted);
    }

    @Test public void completedExistingUserIsArmedDuringMigration() {
        assertTrue(DailyBudgetRollover.armExistingUser(true, false));
        assertTrue(DailyBudgetRollover.armExistingUser(false, true));
        assertFalse(DailyBudgetRollover.armExistingUser(false, false));
    }
}
