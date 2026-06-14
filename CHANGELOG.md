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
