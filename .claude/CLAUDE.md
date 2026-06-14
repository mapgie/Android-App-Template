# MyApp — Claude Code Instructions

When fixing a bug or solving a non-obvious problem, check `LESSONS.md` for prior art. If the fix produces a transferable lesson, add it to `LESSONS.md` in the same commit.

## Versioning

Every PR **must** include a version bump and a changelog entry. No exceptions.

### Scheme: `MAJOR.MINOR.PATCH[-prerelease]`

Version numbers communicate **compatibility risk**, not effort or importance.

| Bump | When to use |
|---|---|
| MAJOR | Breaking change: removes or changes behaviour users depend on, destructive DB migration (data loss risk), incompatible export/backup format change |
| MINOR | Backward-compatible addition: new feature, new screen, new setting, deprecation of existing behaviour |
| PATCH | Backward-compatible fix: bug fix, copy change, performance improvement, internal refactor with no user-visible impact |

Rules:
- Releasing a MINOR resets PATCH to 0 (`1.4.2 → 1.5.0`)
- Releasing a MAJOR resets MINOR and PATCH to 0 (`1.4.2 → 2.0.0`)
- Released versions are immutable — never re-tag or amend a released version
- When in doubt between MINOR and MAJOR, ask: can a user who doesn't update continue using their exported data without loss? If yes → MINOR.

Pre-release suffix: `-beta.N` (increment N for each beta on the same base version).

Current status: **beta** — all versions carry `-beta.N` until explicitly promoted.

### How to bump

1. Update `versionCode` (always increment by 1) and `versionName` in `app/build.gradle.kts`
2. Add a new entry at the top of `CHANGELOG.md` following the existing `## [version] - date` format
3. Include both changes in the same commit as the feature/fix

### Changelog immutability rules — NO EXCEPTIONS

- **Never edit an existing entry.** Once a changelog entry is committed, its version string and change list are frozen. Treat them like a released tag.
- **Never reuse a version string.** If a merge conflict tempts you to keep a version number that already exists in the file, bump to the next available number instead.
- **Never delete an entry.** Even if a feature was reverted, keep the original entry and add a new entry at the top describing the revert.
- **Merge conflicts in CHANGELOG.md must preserve both sides.** When resolving a conflict, keep all entries from both branches and order them by version number (newest at top). If two branches used the same version number, keep both entries and renumber the lower-priority one.

### Examples

```
Bug fix only           → 1.4.2-beta.1 → 1.4.3-beta.1   (versionCode +1)
New feature            → 1.4.2-beta.1 → 1.5.0-beta.1   (versionCode +1)
Breaking DB migration  → 1.4.2-beta.1 → 2.0.0-beta.1   (versionCode +1)
Second beta iteration  → 1.4.2-beta.1 → 1.4.2-beta.2   (versionCode +1)
Promote to release     → 1.4.2-beta.1 → 1.4.2           (versionCode +1)
```

## Architecture Notes

- **Settings screen:** Follow the structure, reusable composables, and "What's New" changelog dialog pattern documented in `docs/SETTINGS_PATTERN.md`. Use it when building or extending the Settings feature so it stays consistent across apps built from this template.
- **UI layer:** Jetpack Compose + Material 3, MVVM with ViewModels; navigation via Compose Navigation (single Activity)
- **Data layer:** Room (SQLite) for relational data; DataStore for user preferences — always add a migration for schema changes, never use `fallbackToDestructiveMigration`
- **Theme:** 19 pre-built palettes (light, dark, system-following) plus WCAG accessibility variants and a custom HSL picker; all in `ui/theme/Color.kt`. Brand font configured in `ui/theme/Type.kt` — replace Comfortaa with your own choice.
- **Privacy defaults:** Cloud backup and device transfer are disabled in the manifest by default — re-enable intentionally if your app's data warrants it.
- **Permissions:** Only declare permissions your app actually uses. Each permission is a user-visible consent prompt and a Play Store declaration. Document the reason for each one in the manifest.

## Key Rules

- `MaterialTheme.colorScheme.error` is reserved for genuine errors and destructive confirmations — do not repurpose for general UI states
- All colour-coded states must also communicate via shape or label (not colour alone) — the Blue & Orange theme exists for red-green colour blindness (~9% of users)
- Minimum tap target: 44x44dp
- Never use `fallbackToDestructiveMigration` in the Room database config
- **Never use en dashes (–) or em dashes (—) in user-facing text.** They read as robotic. Use a period, colon, or reword the sentence instead. Hyphens in genuine compound words ("in-app", "4-digit", "built-in") are fine.
- Never hardcode colours in `TextStyle` / typography — let `MaterialTheme` propagate `LocalContentColor`

## Accessibility Rules (enforced by `a11y_check.py` in CI)

Every `.clickable {}` or `.combinedClickable {}` modifier **must** carry a matching `.semantics { role = Role.<Type> }` in the same modifier chain. Use the role that best describes the element:

| Role | Use for |
|---|---|
| `Role.Button` | Navigation, generic action, expand/collapse, dialog dismiss |
| `Role.RadioButton` | Mutually exclusive single-select (theme pickers, format selectors) |
| `Role.Checkbox` | Toggle with two named states where the element acts as a row wrapping a Checkbox |
| `Role.Switch` | Toggle with two named states; pair with `stateDescription` to announce current state |

Additional rules:
- Place `.semantics { role = }` **before** `.clickable {}` in the chain when the clickable lambda is longer than a few lines, so the CI window check can find it.
- When the parent Row/Box handles the click, set the inner `Checkbox` / `RadioButton` to `onClick = null` to prevent double-focus.
- `clearAndSetSemantics { }` must also include `role = Role.<Type>` — it replaces all child semantics, so the role must be re-declared there.
- Status text that appears or changes in response to user action needs `Modifier.semantics { liveRegion = LiveRegionMode.Assertive }` (errors) or `LiveRegionMode.Polite` (non-urgent feedback).
- Icon-only interactive controls (FABs, icon-only buttons outside of `IconButton`) need `Modifier.semantics { contentDescription = "<action label>" }` on the container itself.
- Run `python3 a11y_check.py` locally before pushing to confirm no new violations.
