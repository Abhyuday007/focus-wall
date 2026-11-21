#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
APK_PATH="${1:-$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk}"

if rg -n 'com\.google\.firebase|androidx\.credentials|googleid|FirebaseAuth|GoogleSignIn' \
    "$PROJECT_DIR/app/build.gradle" "$PROJECT_DIR/build.gradle" "$PROJECT_DIR/app/src/main/java"; then
  echo "Forbidden account, Firebase, or identity dependency found." >&2
  exit 1
fi

if rg -n 'performGlobalAction|GLOBAL_ACTION_BACK|dispatchGesture|node\.getText|event\.getText|getContentDescription' \
    "$PROJECT_DIR/app/src/main/java"; then
  echo "Forbidden Accessibility content read or automated action found." >&2
  exit 1
fi

if [[ ! -f "$APK_PATH" ]]; then
  echo "APK not found: $APK_PATH" >&2
  exit 1
fi

SDK_ROOT="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "$SDK_ROOT" && -f "$PROJECT_DIR/local.properties" ]]; then
  SDK_ROOT="$(sed -n 's/^sdk.dir=//p' "$PROJECT_DIR/local.properties" | head -n 1)"
fi
AAPT="$SDK_ROOT/build-tools/36.0.0/aapt"
if [[ ! -x "$AAPT" ]]; then
  echo "aapt not found: $AAPT" >&2
  exit 1
fi

if "$AAPT" dump permissions "$APK_PATH" | rg '^uses-permission:'; then
  echo "Release boundary violated: APK requests an Android permission." >&2
  exit 1
fi

echo "Privacy boundary verified: no Android permissions, account SDKs, content reads, or automated Accessibility actions."
