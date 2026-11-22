# Data safety draft

## Expected declaration for v1.5.0

- Data collected: No.
- Data shared: No.
- Data encrypted in transit: Not applicable; the app requests no Internet permission and transmits no user data.
- Account creation: No.
- Account deletion URL: Not applicable.
- Ads: No.

## Local processing that must still be disclosed

Focus Wall processes Accessibility events and structural UI metadata only on-device. It stores the daily limit, onboarding state, consent record, and up to 40 sanitized detector events in private preferences. Android backup and device-transfer extraction are disabled. Local diagnostics can be cleared in-app; all local data can be removed by clearing app data or uninstalling.

## Verification before submission

- Confirm the final merged manifest contains no `INTERNET`, `ACCESS_NETWORK_STATE`, advertising, location, contacts, storage, camera, microphone, or account permissions.
- Confirm dependency inventory contains no analytics, ads, Firebase, crash-reporting, or identity SDK.
- Confirm the privacy-policy URL is live and matches the in-app policy.
- Answer the AccessibilityService declaration separately; a “no data collected” Data safety answer does not replace prominent disclosure and consent.
