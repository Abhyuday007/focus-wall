# Security policy

Focus Wall is intentionally local-only. It has no network permission, account,
analytics SDK, cloud sync, external-storage access, or database of Instagram
content. The only persistent data is its private Android preferences file for
the local daily time budget.

## Release checklist

- Build a signed release APK with a locally held upload/signing key. Never
  commit a `.jks`, `.keystore`, `local.properties`, or signing password.
- Verify `./gradlew :app:lintRelease :app:assembleRelease` succeeds.
- Install only the resulting signed APK from a release you control.
- Review Android's Accessibility permission prompt before enabling the app.

## Reporting

Do not include passwords, DMs, private media, or device identifiers in a bug
report. Describe the observed behavior and Android/app versions instead.
