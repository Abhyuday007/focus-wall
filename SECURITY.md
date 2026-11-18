# Security policy

Focus Wall is local-first. Its timer and settings live in private Android
preferences, with Android backup and device-transfer extraction disabled. The
app has no advertising, analytics, external-storage access, cloud sync, or
database of Instagram / YouTube content. Optional Firebase Google sign-in uses
network access only when a user explicitly starts sign-in; it does not upload
the daily budget or social-media data.

## Release checklist

- Build a signed release APK with a locally held upload/signing key. Never
  commit a `.jks`, `.keystore`, `local.properties`, or signing password.
- Verify `./gradlew :app:lintRelease :app:assembleRelease` succeeds.
- Install only the resulting signed APK from a release you control.
- Review Android's Accessibility permission prompt before enabling the app.
- Keep `google-services.json` out of version control. It is project
  configuration, not a substitute for Firebase Auth provider setup or rules.

## Reporting

Do not include passwords, DMs, private media, or device identifiers in a bug
report. Describe the observed behavior and Android/app versions instead.
