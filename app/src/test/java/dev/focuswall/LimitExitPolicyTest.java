package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LimitExitPolicyTest {
    @Test public void instagramLimitNeverUsesDismissOnlyExit() {
        assertEquals(LimitExitPolicy.Destination.INSTAGRAM_HOME,
                LimitExitPolicy.destination(false));
        assertEquals("Back to Instagram Home", LimitExitPolicy.buttonLabel(false));
    }

    @Test public void youtubeLimitAlwaysRoutesHome() {
        assertEquals(LimitExitPolicy.Destination.YOUTUBE_HOME,
                LimitExitPolicy.destination(true));
        assertEquals("Go to YouTube Home", LimitExitPolicy.buttonLabel(true));
    }
}
