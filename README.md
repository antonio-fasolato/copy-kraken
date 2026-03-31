# Copy Kraken

Collect text shared from any Android app, accumulate it, and keep it ready on your clipboard.

Copy Kraken lives in the Android share menu. Every time you share a piece of text from any app, Copy Kraken silently appends it to your current buffer without switching away from what you were doing. When you are ready, open the app to review the accumulated text, archive it for later, or restore a previous entry — all automatically copied to the clipboard.

---

## Features

- **Share target** — appears in the Android share sheet for plain text; no need to open the app first
- **Text accumulation** — shared texts are joined with newlines into a single buffer
- **Clipboard sync** — the full current text is copied to the clipboard whenever it changes
- **Archive & history** — archive the current buffer to a history list and start fresh; tap any history entry to restore it
- **Configurable history size** — set the maximum number of entries to keep (default: 100)
- **Full / truncated display** — history cards show a preview by default (first 50 + last 50 chars); toggle full text in Settings
- **Timestamps & character count** — each history entry shows when it was created and how many characters it contains
- **Localised** — English (default) and Italian

---

## Requirements

- Android 13 (API 33) or higher
- Android Studio Meerkat or later (for building from source)

---

## Build from source

### Prerequisites

- JDK 11 or higher
- Android SDK with Build Tools for API 36

### Steps

```bash
# Clone the repository
git clone https://github.com/<your-username>/copy-kraken.git
cd copy-kraken

# Debug build
./gradlew assembleDebug

# Release build (unsigned)
./gradlew assembleRelease
```

The APK is produced at:

| Variant | Path |
|---------|------|
| Debug   | `app/build/outputs/apk/debug/app-debug.apk` |
| Release | `app/build/outputs/apk/release/app-release-unsigned.apk` |

> **Note:** `gradle/wrapper/gradle-wrapper.jar` is not committed to the repository. Android Studio generates it automatically on the first sync. If you are building from the command line without Android Studio, download the Gradle wrapper jar manually or run `gradle wrapper --gradle-version 9.3.1` once before building.

---

## Install on a device

### Using ADB (recommended for development)

```bash
# Connect your device via USB with USB debugging enabled, then:
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Manual APK installation

1. Copy `app-debug.apk` to your Android device (USB, cloud storage, email, etc.)
2. On the device, open the APK file from a file manager
3. If prompted, allow installation from unknown sources for that file manager app
4. Follow the on-screen instructions to complete the installation

---

## Usage

### Collecting text

1. In any app, select text and tap **Share**
2. Choose **Copy Kraken** from the share sheet
3. A toast confirms the text was added — you stay in the original app

### Reviewing and managing text

Open Copy Kraken to see:

- **Current text** — the accumulated buffer, scrollable up to 10 lines
- **History** — previously archived entries, most recent first

### Archiving

Tap the **Archive** floating button (bottom-right) to save the current text to history and start a new empty buffer.

### Restoring from history

Tap any history card to:
- Move that entry back to the current buffer
- Archive the current text (if non-empty) at the top of history
- Copy the restored text to the clipboard automatically

### Settings

Tap the gear icon next to the app title to open Settings:

| Setting | Description | Default |
|---------|-------------|---------|
| Max history items | Maximum number of entries kept in history | 100 |
| Show full text in history | Show complete text in history cards instead of a preview | Off |

---

## Tech stack

- Kotlin 2.2.10
- Jetpack Compose with Material Design 3
- AndroidViewModel + SharedPreferences
- Min SDK 33 · Target/Compile SDK 36
