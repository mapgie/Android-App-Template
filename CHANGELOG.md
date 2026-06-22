# Changelog

## Versioning policy

Format: `MAJOR.MINOR.PATCH-beta.N` (pre-release) or `MAJOR.MINOR.PATCH` (release).

| Bump | When |
|---|---|
| MAJOR | Breaking change, destructive DB migration (data loss risk), incompatible backup format |
| MINOR | Backward-compatible addition: new feature, new screen, new setting, deprecation |
| PATCH | Backward-compatible fix: bug fix, copy change, perf improvement, internal refactor |

Rules:
- MINOR bump resets PATCH to 0 (`1.4.2 → 1.5.0`); MAJOR resets MINOR and PATCH (`1.4.2 → 2.0.0`)
- Increment `versionCode` by 1 and update `versionName` in `app/build.gradle.kts` with every PR — no exceptions
- Add a changelog entry in the same commit as the feature/fix
- Released versions are immutable — never re-tag, never amend, never delete an entry
- Merge conflicts must preserve both sides; if both branches used the same version string, renumber the lower-priority one upward

---

## [0.6.2-beta.1] - 2026-06-22

### Added
- Custom palette card in the theme picker: tap it to switch to the custom theme, then use the hue sliders for Primary, Secondary, and Tertiary that appear below.

---

## [0.6.1-beta.1] - 2026-06-21

### Changed
- Custom theme name and save controls are now inline in the Appearance screen rather than a separate dialog. Load a saved theme to get Update and Save as new options.

## [0.6.0-beta.1] - 2026-06-20

### Added
- Multiple named custom colour themes: save, load, rename, and delete custom HSL colour profiles from the Appearance settings screen.

---

## [0.5.2-beta.1] - 2026-06-19

### Changed
- Version number on the home screen moved from the top bar to a footer at the bottom of the screen.

---

## [0.5.1-beta.1] - 2026-06-19

### Changed
- Licences screen redesigned to group libraries by licence type, show copyright holders, and use the primaryContainer top bar colour, matching the GoFlo app style.

---

## [0.5.0-beta.1] - 2026-06-19

### Added
- Tag release workflow: go to Actions > Tag release > Run workflow to create a version tag from the current main branch. The version is read from `app/build.gradle.kts` automatically; the workflow fails safely if the tag already exists, which prevents accidentally re-releasing a version.

---

## [0.4.0-beta.1] - 2026-06-18

### Added
- Release workflow: pushing a `v*` tag builds the release APK and creates a GitHub Release with the matching CHANGELOG section as the body. Tags containing `-` (e.g. `-beta.1`) are published as pre-releases. Signing uses repository secrets (`SIGNING_KEY`, `KEY_STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`); falls back to the checked-in debug keystore when secrets are absent.

---

## [0.3.0-beta.1] - 2026-06-16

### Added
- Settings screen with Appearance and About sub-screens
- CompactThemePicker with palette grid, Light/Dark/System toggle, WCAG accessibility mode, and custom HSL builder
- What's New changelog dialog reads from bundled CHANGELOG.md
- Open-source licenses screen with tappable license links
- Subtle clickable version number on the Home screen opens the changelog
- Notification infrastructure: three-channel system (Alarm, Notification, Silent) ready to configure
- AlarmScheduler, AlarmReceiver, AlarmActionReceiver with Snooze and Dismiss actions
- BootWorker reschedules alarms after device reboot

---

## [0.2.1-beta.1] - 2026-06-14

### Added
- `docs/SETTINGS_PATTERN.md`: documents the Settings screen structure (flat list + enum-routed sub-screens), reusable composables (`SettingsSectionHeader`, `SettingsNavItem`, `SettingsSubScreenScaffold`, `SwitchRow`), the About sub-screen layout, and the "What's New" changelog dialog with its Markdown parser, modelled on GoFlo's implementation.

---

## [0.2.0-beta.1] - 2026-06-13

### Added
- `permission/PermissionHelper.kt`: Settings deep links for exact alarms (API 31+) and notifications, for apps that schedule reminders. See LESSONS.md for the "denied permission needs a Settings link" pattern.

---

## [0.1.0-beta.1] - 2026-06-03

### Added
- Initial template scaffold: Compose + Material 3 theme system, Room database setup, DataStore preferences, CI/CD workflows, accessibility tooling, and LESSONS.md
