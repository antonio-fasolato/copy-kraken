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

The app uses a single-Activity architecture with Jetpack Compose and a `ViewModel` per screen:

- **`MainActivity`** — sole entry point; handles share intents (`ACTION_SEND`) in both `onCreate` and `onNewIntent`; hosts the Compose UI tree inside a `Scaffold`
- **`MainViewModel`** — holds `MainUiState` (current text + history list) as `StateFlow`; exposes `clipboardEvent: SharedFlow<String>` for one-shot clipboard writes; methods: `onSharedText(text)`, `archiveCurrent()`
- **`MainScreen`** — stateless composable; receives `MainUiState` and `onArchive` lambda; renders current text card, archive button, history list
- **`ui/theme/`** — Material3 theme setup (Color, Type, Theme); dynamic color enabled for Android 12+
- Package root: `fasolato.click.copykraken`

New screens should be added as composable functions, placed in packages under the main package (e.g., `fasolato.click.copykraken.feature.featurename`).

## Share target

The app registers as an Android share target for `text/plain`:

- `AndroidManifest.xml` declares `android:launchMode="singleTop"` and an `ACTION_SEND / text/plain` intent-filter on `MainActivity`
- Shared text is appended to the current text (newline separator); the full resulting text is copied to the system clipboard via `ClipboardManager`
- The clipboard write is triggered by collecting `MainViewModel.clipboardEvent` in a `LaunchedEffect(Unit)` inside `setContent` — the ViewModel never holds a `Context`
- State is in-memory only (survives configuration changes, not process death)

## Internationalisation (i18n)

The app is localised with **English as the default** and **Italian** as an alternative locale.

- All user-visible strings must be defined in `app/src/main/res/values/strings.xml` (English)
- Every string must also have an Italian translation in `app/src/main/res/values-it/strings.xml`
- Never hardcode strings in Kotlin/Compose code — always use `stringResource(R.string.key)` or `stringResource(R.string.key, arg)` for parameterised strings
- This rule applies to every change: new screens, new UI components, error messages, labels, placeholders, etc.
