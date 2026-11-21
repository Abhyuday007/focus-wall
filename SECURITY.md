# Security policy

Focus Wall is local-first. Its timer and settings live in private Android
preferences, with Android backup and device-transfer extraction disabled. The
app has no account system, Internet permission, advertising, analytics,
external-storage access, cloud sync, crash-reporting SDK, or database of
Instagram / YouTube content.

The Accessibility service remains inert until the user affirmatively accepts
the current prominent disclosure. It examines only structural identifiers and
scroll state required to distinguish supported Reels and Shorts surfaces. It
does not read accessibility text, content descriptions, messages, media,
passwords, contacts, searches, or account information, and never performs
gestures or global navigation actions.

## Release checklist

- Build a signed release APK and AAB with a locally held upload/signing key. Never
  commit a `.jks`, `.keystore`, `local.properties`, or signing password.
- Verify `./gradlew :app:testDebugUnitTest :app:lintRelease :app:assembleRelease :app:bundleRelease` succeeds.
- Install only the resulting signed APK from a release you control.
- Verify the disclosure acceptance, refusal, withdrawal, and privacy-policy
  flows before enabling the service.
- Verify the merged release manifest contains no unexpected permissions.

## Reporting

Do not include passwords, DMs, private media, or device identifiers in a bug
report. Describe the observed behavior and Android/app versions instead.
