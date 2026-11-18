<h1 align="center">Focus Wall</h1>

<p align="center">
  <strong>Stay connected. Scroll with intention.</strong><br />
  A local-first Android accessibility utility for putting a daily boundary around native Reels and Shorts—without replacing Instagram, asking for your login, or sending your data anywhere.
</p>

<p align="center">
  <a href="#how-it-feels">How it feels</a> ·
  <a href="#privacy--security">Privacy &amp; security</a> ·
  <a href="#build">Build</a> ·
  <a href="SECURITY.md">Security policy</a>
</p>

> **Demo policy:** This repository will use a real, privacy-safe device recording for its product demo. It will show only Focus Wall screens—never an account, DM, Story, or other personal content.

## The idea

Instagram is useful for people, conversations, Stories, and creating things. Infinite vertical video is a different experience. Focus Wall adds a personal daily budget to the native Reels tab while leaving the rest of Instagram native.

| You can keep doing | What Focus Wall changes |
| --- | --- |
| Stories, DMs, notifications, posts, story creation, search, and your profile | Native Reels and YouTube Shorts can use only the daily time budget you choose: 0–60 minutes |
| Opening a single Reel someone shared in a DM or Story | Once the budget is over, trying to advance sends you to a calm limit screen and then back to native Instagram Home |
| Your existing Instagram app and login | Nothing is proxied through a sketchy login page or a third-party feed |

## How it feels

<details open>
<summary><strong>1. Set a daily budget</strong></summary>

Choose 0–60 minutes. The timer is local to your device and only runs during detected Reel/Shorts use.
</details>

<details>
<summary><strong>2. Stay connected</strong></summary>

Home, Stories, DMs, notifications, posts, and story creation remain in the native app.
</details>

<details>
<summary><strong>3. Reach the edge gently</strong></summary>

When time is up, Focus Wall shows a clear limit card. It never performs repeated Back actions or broadly closes Instagram.
</details>

## Privacy & security

- **Local only.** No network permission, accounts, analytics SDKs, cloud sync, or WebView.
- **No Instagram data writes.** Focus Wall does not have external-storage access and does not alter Instagram data.
- **Private state.** The app persists only its own small timer preference file; backups and device-transfer extraction are disabled.
- **Tight Android surface.** The accessibility service and limit activity are non-exported. The launcher is the only public component.
- **Release-hardened.** Release builds are minified and resource-shrunk. Signing material is ignored by Git.

Read the full [security policy](SECURITY.md) before publishing a build.

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

For a public release:

```bash
./gradlew :app:lintRelease :app:assembleRelease
```

The generated release APK is unsigned by design. Sign it with a key that stays on your own machine—never commit a `.jks`, `.keystore`, `local.properties`, or signing password.

## Scope

Focus Wall is an independent, local personal project. It is not affiliated with, endorsed by, or sponsored by Meta, Instagram, or YouTube. App interfaces and accessibility behavior can change with third-party app updates; test on your own device before relying on a new build.
