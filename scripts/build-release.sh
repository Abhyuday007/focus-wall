#!/bin/zsh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
KEYSTORE_PATH="$PROJECT_DIR/focus-wall-release.jks"
KEY_PASSWORD="$(security find-generic-password -a focus-wall -s focus-wall-release-signing -w)"

FOCUS_WALL_KEYSTORE="$KEYSTORE_PATH" \
FOCUS_WALL_KEYSTORE_PASSWORD="$KEY_PASSWORD" \
"$PROJECT_DIR/gradlew" -p "$PROJECT_DIR" :app:assembleRelease
