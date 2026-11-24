<div align="center">

# Focus Wall

### Stay connected. Scroll with intention.

**A local-first Android accessibility utility that puts a daily boundary around native Instagram Reels and YouTube Shorts—without replacing the apps you already use.**

[![Platform](https://img.shields.io/badge/platform-Android%2010%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](#build)
[![Language](https://img.shields.io/badge/language-Java-007396?style=flat-square&logo=openjdk&logoColor=white)](#build)
[![Privacy](https://img.shields.io/badge/privacy-local--first-1B7F67?style=flat-square&logo=shield&logoColor=white)](#privacy--security)
[![Demo](https://img.shields.io/badge/demo-19%20seconds-03D26F?style=flat-square&logo=play&logoColor=white)](#see-it-work)
[![Beta](https://img.shields.io/badge/beta-apply-7057FF?style=flat-square&logo=googleforms&logoColor=white)](https://docs.google.com/forms/d/e/1FAIpQLSflebBND8uDGJ_sQSNR-npJeYA5YrmJO1M_l9lMeQSbmZxuAQ/viewform?usp=dialog)

[Apply for beta](https://docs.google.com/forms/d/e/1FAIpQLSflebBND8uDGJ_sQSNR-npJeYA5YrmJO1M_l9lMeQSbmZxuAQ/viewform?usp=dialog) · [See it work](#see-it-work) · [What it protects](#what-focus-wall-changes) · [Build it](#build) · [Security](SECURITY.md)

</div>

> **Status:** v1.5.0 limited Android beta · targets Android 16 (API 36) and is tested on Android 16 / Samsung SM-S711B. [Apply to test Focus Wall](https://docs.google.com/forms/d/e/1FAIpQLSflebBND8uDGJ_sQSNR-npJeYA5YrmJO1M_l9lMeQSbmZxuAQ/viewform?usp=dialog). Third-party apps change often; test every new Focus Wall build on your own device.

## See it work

[![Watch the Focus Wall guide](docs/media/focus-wall-guide-poster.jpg)](docs/media/focus-wall-guide.mp4)

*A 19-second real-device guide recorded from v1.5.0. It contains only Focus Wall screens—no messages, account picker, social feed, or personal media.*

<table>
  <tr>
    <td align="center"><img src="docs/media/focus-wall-v150-setup.jpg" width="260" alt="Guided shield setup"><br><sub><b>Guided setup</b><br>Protection status is clear from launch.</sub></td>
    <td align="center"><img src="docs/media/focus-wall-v150-dashboard.jpg" width="260" alt="Focus Wall control center"><br><sub><b>Control center</b><br>Live budget and active app shields.</sub></td>
    <td align="center"><img src="docs/media/focus-wall-v150-controls.jpg" width="260" alt="Daily and recurring budget controls"><br><sub><b>Intentional controls</b><br>Today stays locked; future days repeat.</sub></td>
  </tr>
</table>

## The promise

Social apps are useful for people, messages, stories, posts, and search. Infinite vertical video is a different experience. Focus Wall preserves the first group and puts a daily, intentional boundary around the second.

| Keep using your apps | Focus Wall changes only this |
| --- | --- |
| Instagram Home, DMs, Stories, notifications, posts, profiles, and search | The native Reels tab uses a daily budget from **0–60 minutes** |
| YouTube search, regular videos, subscriptions, and Home | YouTube Shorts use that same daily budget |
| Your existing Instagram / YouTube login | No proxy login, alternate feed, or account password is requested |

## What Focus Wall changes

```text
Instagram / YouTube
        │
        ├─ Home · DMs · Stories · Posts · Search ─────────────── stays native
        │
        └─ Reels / Shorts ── daily timer ── budget reached ── calm limit card
```

- **Timer integrity:** time is counted only while Focus Wall detects native Reels or Shorts—not while you are in Home, DMs, Stories, or away from the app.
- **No forced Back loop:** hitting the limit presents a clear card instead of repeatedly pressing Back or closing the entire app.
- **Shared content remains useful:** a Reel opened from a DM or Story can be viewed; advancing into the endless feed is held back when the daily budget is gone.
- **Intentional setup:** onboarding asks who the boundary is for, what they want more of, and uses a press-and-hold commitment before arming a daily limit.
- **0-minute mode:** choose zero when you want no Reels or Shorts feed that day while keeping the rest of your social apps usable.
- **Automatic rollover:** the scheduled limit auto-arms at midnight and repeats daily. Changing a future limit never adds time to the current day.

## Privacy & security

Focus Wall is built around one constraint: it should never become more invasive than the habit it helps manage.

- **Local-first state.** The daily timer and settings are stored in the app’s private Android preferences. Android backup and device-transfer extraction are disabled.
- **No social-media credentials.** Focus Wall does not read, store, or submit Instagram or YouTube passwords, messages, media, or account data.
- **No accounts, network, advertising, or analytics.** The app requests no Internet permission and contains no account, ad, analytics, Firebase, or crash-reporting SDK.
- **Explicit consent.** The Accessibility service remains inert until the current in-app disclosure is affirmatively accepted. Consent can be withdrawn at any time.
- **Small Android surface.** The accessibility service and limit activity are non-exported; the launcher is the only public component.

Read the [privacy policy](https://abhyuday007.github.io/focus-wall/privacy.html) and full [security policy](SECURITY.md) before distributing a build.

## Build

```bash
git clone https://github.com/Abhyuday007/focus-wall.git
cd focus-wall
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Open Focus Wall and follow the startup shield guide. Read the complete disclosure, then choose whether to continue to Android Accessibility settings. The service remains inactive without affirmative consent, even if Android’s toggle was enabled by an older build.

### Signed release

```bash
./scripts/build-release.sh
```

The release script retrieves a locally held signing password from macOS Keychain and produces a signed APK and Play Store AAB. Never commit a `.jks`, `.keystore`, `local.properties`, `google-services.json`, or signing password.

## Scope

Focus Wall is an independent Android project. It is not affiliated with, endorsed by, or sponsored by Meta, Instagram, Google, or YouTube. It is a personal attention tool—not a substitute for medical, mental-health, or parental-control advice.
