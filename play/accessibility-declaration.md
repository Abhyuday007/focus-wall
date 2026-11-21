# AccessibilityService declaration draft

## Core purpose

Focus Wall is an attention-management utility. Its user-facing core feature applies a user-selected daily time boundary specifically to supported Instagram Reels and YouTube Shorts surfaces while leaving messaging, Stories, search, posts, profiles, subscriptions, and regular videos available.

## Why the API is necessary

Android does not provide a narrower API that identifies which surface is visible inside another app. UsageStats can identify only the foreground package and would block all of Instagram or YouTube. Focus Wall uses AccessibilityService to distinguish supported short-video surfaces from allowed surfaces.

## Data accessed and use

The service receives window/app changes, clicks, and scroll events. On supported Instagram and YouTube versions it examines package names, view resource identifiers, selected states, component class names, and scroll distance. This information is processed on-device only to classify the current surface, measure the daily short-video budget, and show the user’s limit card.

The service does not read, store, collect, or share accessibility text, content descriptions, DMs, Stories, passwords, media, contacts, search terms, account names, or credentials. It does not perform gestures, press Back, change settings, or autonomously execute actions in another app.

## Prominent disclosure and consent

Normal setup routes the user to an in-app `Accessibility disclosure` screen before Android Accessibility Settings. The complete disclosure is visible in-app and includes what is accessed, the purpose, what is excluded, local retention, a privacy-policy link, an affirmative `I agree — open Accessibility settings` action, and a `Not now` action.

The service remains inert unless the current disclosure version was affirmatively accepted. Users can reopen the disclosure, withdraw consent, disable the service in Android Settings, and clear local diagnostics.

Do not mark Focus Wall as an accessibility tool for people with disabilities. The service metadata explicitly declares `isAccessibilityTool=false`.

## Required declaration video

Record one continuous real-device video showing:

1. Cold-open Focus Wall.
2. Navigate through normal setup to `Review disclosure & enable access`.
3. Slowly show the full disclosure, including all cards and the privacy link.
4. Tap `Not now`; demonstrate that protection remains inactive.
5. Reopen the disclosure.
6. Tap `I agree — open Accessibility settings`.
7. Enable `Focus Wall app blocker` in Android Settings.
8. Return to Focus Wall and arm a zero-minute budget.
9. Open a supported Instagram Reel or YouTube Short and show the limit card.
10. Return to the disclosure and demonstrate `Withdraw consent`.

Use captions or voice-over explaining that the service classifies surfaces using structural identifiers and never reads messages or screen text. Host the final video as an unlisted YouTube video or accessible Google Drive MP4 and paste that URL into Play Console.
