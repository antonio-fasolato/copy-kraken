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

- **`MainActivity`** — sole entry point; hosts the Compose UI tree inside a `Scaffold` with a FAB; calls `viewModel.reload()` in `onResume`; collects `clipboardEvent` via `LaunchedEffect` to write to `ClipboardManager`
- **`MainViewModel`** — `AndroidViewModel`; initialises state from `AppStorage`; exposes `uiState: StateFlow<MainUiState>` and `clipboardEvent: SharedFlow<String>`; methods: `reload()`, `archiveCurrent()`, `restoreFromHistory(index)`
- **`MainScreen`** — stateless composable; receives `MainUiState`, `onArchive`, `onRestoreFromHistory`; renders app header (name + description), current text card, history list with tappable cards
- **`AppStorage`** — `SharedPreferences` wrapper; persists `currentText` and `history` (serialised via `org.json.JSONArray`); method `appendText(text): String`
- **`ShareReceiverActivity`** — bare `Activity` with `Theme.NoDisplay`; receives `ACTION_SEND / text/plain`; appends text via `AppStorage`, copies to clipboard, shows Toast, calls `finish()` immediately
- **`ui/theme/`** — Material3 theme (Color, Type, Theme); dynamic color enabled for Android 12+
- Package root: `fasolato.click.copykraken`

New screens should be added as composable functions, placed in packages under the main package (e.g., `fasolato.click.copykraken.feature.featurename`).

## Share target

The app registers as an Android share target for `text/plain` via `ShareReceiverActivity`:

- `AndroidManifest.xml` declares `ShareReceiverActivity` with `android:theme="@android:style/Theme.NoDisplay"` and an `ACTION_SEND / text/plain` intent-filter
- After sharing, the user stays in the originating app; Copy Kraken shows only a Toast confirmation
- `MainActivity` is not involved in the share flow; it picks up changes via `viewModel.reload()` in `onResume`

## UI — FAB

The archive action is a `FloatingActionButton` (bottom-right) using `Icons.Default.Archive` from `material-icons-extended`. It is visible only when `currentText` is non-empty.

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
