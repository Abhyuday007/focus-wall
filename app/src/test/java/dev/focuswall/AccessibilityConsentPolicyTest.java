package dev.focuswall;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessibilityConsentPolicyTest {
    @Test public void processingRequiresConsentAndArmedBudget() {
        assertTrue(AccessibilityConsentPolicy.shouldProcess(true, true));
        assertFalse(AccessibilityConsentPolicy.shouldProcess(false, true));
        assertFalse(AccessibilityConsentPolicy.shouldProcess(true, false));
        assertFalse(AccessibilityConsentPolicy.shouldProcess(false, false));
    }
}
