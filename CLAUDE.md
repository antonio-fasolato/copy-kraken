# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Tests
./gradlew test                    # Unit tests (JVM)
./gradlew connectedAndroidTest    # Instrumentation tests (requires device/emulator)

# Single test class
./gradlew test --tests "fasolato.click.copykraken.ExampleUnitTest"

# Lint
./gradlew lint
```

## Tech Stack

- **Language:** Kotlin 2.2.10
- **UI:** Jetpack Compose with Material Design 3
- **Min SDK:** 33 (Android 13) — no need for older API compatibility shims
- **Target/Compile SDK:** 36
- **Java compatibility:** VERSION_11
- **Build system:** Gradle 9.3.1 with Kotlin DSL and version catalog (`gradle/libs.versions.toml`)

## Architecture

The app uses a single-Activity architecture with Jetpack Compose:

- **`MainActivity`** — sole entry point; hosts `Scaffold` with FAB and conditional `TopAppBar`; `var showSettings` drives Main ↔ Settings navigation; calls `viewModel.reload()` in `onResume`; collects `clipboardEvent` via `LaunchedEffect` to write to `ClipboardManager`
- **`MainViewModel`** — `AndroidViewModel`; initialises state from `AppStorage`; exposes `uiState: StateFlow<MainUiState>`, `maxHistorySize: StateFlow<Int>`, `showFullHistoryText: StateFlow<Boolean>`, `autoArchiveMinutes: StateFlow<Int>`, `clipboardEvent: SharedFlow<String>`; methods: `reload()`, `archiveCurrent()`, `restoreFromHistory(index)`, `setMaxHistorySize(value)`, `setShowFullHistoryText(value)`, `setAutoArchiveMinutes(value)`
- **`MainScreen`** — stateless composable; receives `MainUiState`, `onArchive`, `onRestoreFromHistory`, `onSettingsClick`, `showFullHistoryText`; renders header row (app name + settings icon), description, current text card (max 10 lines, scrollable), tappable history cards (truncated or full based on `showFullHistoryText`)
- **`SettingsScreen`** — stateless composable; receives `maxHistorySize`, `onMaxHistorySizeChange`, `showFullHistoryText`, `onShowFullHistoryTextChange`, `autoArchiveMinutes`, `onAutoArchiveMinutesChange`; renders two `OutlinedTextField`s (history limit, auto-archive minutes) and a `Switch` for full-text toggle
- **`AppStorage`** — `SharedPreferences` wrapper; persists `currentText`, `history` (via `org.json.JSONArray`), `maxHistorySize` (Int, default 100), `showFullHistoryText` (Boolean, default false), `autoArchiveMinutes` (Int, default 10), `currentTextTimestamp` (Long); method `appendText(text): String` — auto-archives current text before appending if it is older than `autoArchiveMinutes`
- **`ShareReceiverActivity`** — bare `Activity` with `Theme.NoDisplay`; receives `ACTION_SEND / text/plain`; appends text via `AppStorage`, copies to clipboard, shows Toast, calls `finish()` immediately
- **`ui/theme/`** — Material3 theme (Color, Type, Theme); dynamic color enabled for Android 12+
- Package root: `fasolato.click.copykraken`

New screens should be added as composable functions, placed in packages under the main package (e.g., `fasolato.click.copykraken.feature.featurename`).

## Navigation

No `navigation-compose` dependency. Navigation between Main and Settings is driven by `var showSettings by remember { mutableStateOf(false) }` in `MainActivity`. The `TopAppBar` (with back arrow + "Settings" title) is shown only when `showSettings == true`.

## Share target

The app registers as an Android share target for `text/plain` via `ShareReceiverActivity`:

- `AndroidManifest.xml` declares `ShareReceiverActivity` with `android:theme="@android:style/Theme.NoDisplay"` and an `ACTION_SEND / text/plain` intent-filter
- After sharing, the user stays in the originating app; Copy Kraken shows only a Toast confirmation
- `MainActivity` is not involved in the share flow; it picks up changes via `viewModel.reload()` in `onResume`

## UI — FAB

The archive action is a `FloatingActionButton` (bottom-right) using `Icons.Default.Archive` from `material-icons-extended`. Visible only when `currentText` is non-empty and `showSettings == false`.

## UI — Text display rules

- **Current text card**: max height = `lineHeight × 10 + 24dp` (computed from `MaterialTheme.typography.bodyMedium.lineHeight` via `LocalDensity`); scrollable within the card
- **History items**: controlled by `showFullHistoryText` setting (default `false`)
  - `false` (default): if text ≤ 100 chars → show full; if > 100 chars → first 50 + `...` + last 50 (via `truncateHistoryItem()` in `MainScreen.kt`)
  - `true`: always show full text

## Settings

Accessible via the gear icon to the right of the app name in `MainScreen`.

- **Max history items** (`maxHistorySize`, default 100): maximum number of entries kept in the history list; stored in `AppStorage`; `archiveCurrent()` applies `.take(maxHistorySize)` after prepending; `setMaxHistorySize()` trims existing history immediately if it exceeds the new value
- **Show full text in history** (`showFullHistoryText`, default `false`): when `false`, history cards show first 50 + `...` + last 50 chars for texts longer than 100 chars; when `true`, always shows full text
- **Auto-archive after N minutes** (`autoArchiveMinutes`, default 10): when a text is shared via `ShareReceiverActivity`, if the current text exists and its session started more than N minutes ago, it is automatically archived and the new text begins a fresh session; logic is entirely in `AppStorage.appendText()`; `currentTextTimestamp` (Long) tracks when the session started (set once on the first append to an empty buffer, reset to `0L` on archive)

## History interaction

Tapping a history card calls `viewModel.restoreFromHistory(index)`:
- the tapped entry is removed from history and becomes the new current text
- if there was a current text, it is prepended to the remaining history
- the restored text is copied to the system clipboard via `clipboardEvent`

## Internationalisation (i18n)

The app is localised with **English as the default** and **Italian** as an alternative locale.

- All user-visible strings must be defined in `app/src/main/res/values/strings.xml` (English)
- Every string must also have an Italian translation in `app/src/main/res/values-it/strings.xml`
- Never hardcode strings in Kotlin/Compose code — always use `stringResource(R.string.key)` or `stringResource(R.string.key, arg)` for parameterised strings
- This rule applies to every change: new screens, new UI components, error messages, labels, placeholders, etc.
