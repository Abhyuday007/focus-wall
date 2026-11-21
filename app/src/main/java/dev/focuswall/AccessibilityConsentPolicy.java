package dev.focuswall;

final class AccessibilityConsentPolicy {
    static boolean shouldProcess(boolean consentGranted, boolean budgetArmed) {
        return consentGranted && budgetArmed;
    }

    private AccessibilityConsentPolicy() {}
}
