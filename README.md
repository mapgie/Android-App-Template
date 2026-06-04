# Android App Template

A production-ready Android app template based on **GoFlo**, a shipping Compose app. Drop in your package name, pick a theme, and build features from day one.

## What You Get

- **Jetpack Compose + Material 3** with MVVM and single-Activity navigation
- **19-palette theme system** (light, dark, system-following, WCAG accessibility variants, custom HSL picker) — all colour-contrast-validated
- **Room database scaffolding** with migration pattern (never `fallbackToDestructiveMigration`)
- **DataStore preferences** for type-safe settings persistence
- **Google Fonts** integration via downloadable font provider (Comfortaa Bold brand font — swap as needed)
- **Privacy-first manifest** (cloud backup and device transfer disabled by default)
- **FileProvider** for content-URI sharing (e.g. export files)
- **CI/CD workflows**: build + lint + tests, changelog enforcement, CodeQL security scan, licence-screen sync
- **Accessibility tooling**: `a11y_check.py` enforces semantics roles on every `.clickable {}` modifier
- **Versioning policy** baked into CLAUDE.md and the changelog check workflow
- **LESSONS.md** with hard-won lessons from the GoFlo project

## Quick Start

### 1. Rename the package

Do a **global search-and-replace** across the entire project:

| Find | Replace with |
|---|---|
| `com.example.myapp` | `your.package.name` |
| `MyApp` | `YourAppName` |
| `MyApplication` | `YourAppClass` |
| `myapp_database` | `yourapp_database` |
| `myapp_prefs` | `yourapp_prefs` |

Then rename the source directory:
```
app/src/main/java/com/example/myapp/
  → app/src/main/java/your/package/name/
```

### 2. Set your version

In `app/build.gradle.kts`:
```kotlin
versionCode = 1
versionName = "0.1.0-beta.1"
```

### 3. Pick your starter theme

In `MainActivity.kt`, the default theme is `AppTheme.CORAL`. Change this to any of the 19 named themes in `ui/theme/Color.kt`, or let the user pick from the full catalogue.

### 4. Generate a debug keystore (for CI)

The template includes a committed `debug.keystore` for CI signing consistency. If you want a fresh one:
```bash
keytool -genkey -v \
  -keystore debug.keystore \
  -alias androiddebugkey \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass android -keypass android \
  -dname "CN=Android Debug,O=Android,C=US"
```

### 5. Build

```bash
./gradlew assembleDebug
./gradlew test
./gradlew lintDebug
python3 a11y_check.py
```

---

## Project Structure

```
app/src/main/java/com/example/myapp/
├── MyApplication.kt          # App singleton — DB, preferences, coroutine scope
├── MainActivity.kt           # Single activity — theme, nav, screen routing
├── ui/
│   ├── MainViewModel.kt      # App-level state (LOADING → READY)
│   ├── theme/
│   │   ├── Color.kt          # 19 palette theme catalogue + WCAG variants
│   │   ├── Type.kt           # Typography (brand font + system body)
│   │   └── Theme.kt          # AppTheme composable
│   ├── navigation/Screen.kt  # Sealed route definitions
│   └── screens/
│       ├── home/HomeScreen.kt
│       └── settings/SettingsScreen.kt
└── data/
    └── preferences/AppPreferences.kt  # DataStore settings
```

---

## Theme System

The template ships 19 pre-built colour palettes, each with light + dark + system-following variants:

**Classic:** Coral, Teal, Sage  
**Fun:** Summer Candy, Beach Vibes, Peach Melba, All-Night Disco Party, Metal Chic, Whimsy Whispers, Colour Me Happy  
**Bold:** Dragon Fire, Midnight Neon  
**Accessibility:** High Contrast (light + dark), Blue & Orange (deuteranopia/protanopia safe)  
**Custom:** HSL hue picker generates a full M3 scheme dynamically

To use a palette in code:
```kotlin
AppTheme(appTheme = AppTheme.CORAL) { … }
```

To add a new palette:
1. Add an entry to the `AppTheme` enum in `Color.kt`
2. Define the `lightColorScheme` / `darkColorScheme` values
3. Add a branch to `colorSchemeFor()` and `standardColorSchemeFor()`
4. Run `python3 a11y_check.py` and verify WCAG AA contrast

**Do NOT rename existing enum entries** — the name is persisted to DataStore.

---

## Versioning

Every PR must include a version bump and a CHANGELOG entry. See `CLAUDE.md` for the full policy. The `changelog-check.yml` CI workflow enforces this.

| Bump | When |
|---|---|
| MAJOR | Breaking change, destructive DB migration, incompatible export format |
| MINOR | New feature, new screen, new setting |
| PATCH | Bug fix, copy change, refactor with no user-visible impact |

---

## Accessibility

The `a11y_check.py` script (and the corresponding CI step) will fail if any `.clickable {}` or `.combinedClickable {}` modifier lacks a `.semantics { role = Role.* }` declaration in the same modifier chain.

Run locally before pushing:
```bash
python3 a11y_check.py
```

See `CLAUDE.md` for the full accessibility rules.

---

## CI/CD

| Workflow | Trigger | What it does |
|---|---|---|
| `build.yml` | PR or manual | a11y check → build → version check → tests → lint → optional release |
| `changelog-check.yml` | PR | Fails if code changed but CHANGELOG.md wasn't updated |
| `codeql.yml` | Push/PR/weekly | GitHub CodeQL security scan |
| `license-sync.yml` | PR (deps changed) | Fails if `libs.versions.toml` changed but `LicensesScreen.kt` wasn't updated |

---

## Privacy Defaults

The manifest ships with cloud backup and device transfer **disabled**. If your app does not handle sensitive data and you want system backup enabled, remove `android:allowBackup="false"` and clear `backup_rules.xml` and `data_extraction_rules.xml`.

---

## Derived from GoFlo

This template was extracted from [GoFlo](https://github.com/mapgie/goflo), a production period-tracking app. The theme system, CI tooling, accessibility patterns, and lessons in `LESSONS.md` were all battle-tested there. Package-specific content (period tracking, health data, biometrics, alarm reminders) has been stripped; the infrastructure has been kept.
