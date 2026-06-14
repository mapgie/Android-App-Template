# Settings screen pattern

This is the reference structure for the Settings feature, modelled on GoFlo's
implementation. New apps built from this template should follow this pattern
rather than inventing a one-off layout, so Settings looks and behaves the same
across apps.

## Structure: flat list + enum-routed sub-screens

`SettingsScreen` is a single composable that shows either:

- a **flat top-level list** grouped into sections, or
- one of several **full-screen sub-screens** (Appearance, Data, About, etc.)

Routing between them is a private enum, not Compose Navigation — these are
all "Settings", so they don't need their own nav graph entries or back-stack
entries:

```kotlin
private enum class SettingsSubScreen {
    NONE, APPEARANCE, DATA, ABOUT
    // add one entry per sub-screen your app needs
}

@Composable
fun SettingsScreen(onBack: () -> Unit, /* ... */) {
    var subScreen by rememberSaveable { mutableStateOf(SettingsSubScreen.NONE) }

    BackHandler(enabled = subScreen != SettingsSubScreen.NONE) {
        subScreen = SettingsSubScreen.NONE
    }

    when (subScreen) {
        SettingsSubScreen.NONE -> SettingsMainList(
            onBack = onBack,
            onNavigate = { subScreen = it },
        )
        SettingsSubScreen.APPEARANCE -> AppearanceSubScreen(onBack = { subScreen = SettingsSubScreen.NONE })
        SettingsSubScreen.DATA -> DataSubScreen(onBack = { subScreen = SettingsSubScreen.NONE })
        SettingsSubScreen.ABOUT -> AboutSubScreen(onBack = { subScreen = SettingsSubScreen.NONE })
    }
}
```

If a sub-screen itself has a deeper drill-down (e.g. an "Export" screen under
"Data"), route it back to its parent sub-screen, not to `NONE`.

## Reusable composables

Put these in `ui/components/SettingsComponents.kt` (or similar) so every
sub-screen and the main list share them.

### `SettingsSectionHeader`

Uppercase, semibold, primary-coloured label above a group of items:

```kotlin
@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp, end = 16.dp)
    )
}
```

### `SettingsNavItem`

A row that navigates to a sub-screen. Uses `ListItem` with a leading icon, a
title + subtitle, and a trailing chevron:

```kotlin
@Composable
fun SettingsNavItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color? = null,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint ?: MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .semantics { role = Role.Button }
            .clickable(onClick = onClick)
    )
}
```

### `SettingsSubScreenScaffold`

Every sub-screen is wrapped in the same `Scaffold` with a `TopAppBar` and back
arrow, using `primaryContainer` for the app bar:

```kotlin
@Composable
fun SettingsSubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.semantics { role = Role.Button }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        content = content,
    )
}
```

### `SwitchRow`

For toggles with a label and a short description, inside a sub-screen:

```kotlin
@Composable
fun SwitchRow(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .semantics { role = Role.Switch }
            .toggleable(value = checked, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.semantics { stateDescription = if (checked) "On" else "Off" }
        )
    }
}
```

## Top-level layout

Main list order, by section (omit sections your app doesn't need, but keep
this relative ordering):

1. **Personalisation** — Appearance (theme picker, app icon picker)
2. **Privacy & Data** — Security/Privacy, Data & Backup (export/import, delete)
3. **Feature-specific groups** — anything specific to the app (widgets,
   reminders, etc.)
4. **Help & Feedback** — bug report / feature request link, support card
5. **About** — last section, contains the version number and the entry point
   to "What's New"

Use `HorizontalDivider()` between sections and `SettingsSectionHeader` at the
top of each group. Each navigable group is a `SettingsNavItem`; don't inline
controls for a whole sub-area directly into the main list.

## About sub-screen

The About sub-screen is a short narrative plus links, not a settings form:

- A handful of short paragraphs (`bodyMedium`, `onSurface`) — what the app is,
  the privacy stance, any disclosures. Use the body font, never the brand
  font, even if the app has a custom display font elsewhere.
- A `HorizontalDivider()` before the button group.
- `FilledTonalButton` for the primary action: **"What's New"**, which opens
  the changelog dialog (see below).
- `OutlinedButton`s for secondary links: Privacy Policy, Licences, Disclaimer
  (whichever apply to the app).
- The current version number (`versionName`) displayed as plain text at the
  bottom.

## "What's New" changelog dialog

### Data source

Ship a copy of `CHANGELOG.md` as an asset (`app/src/main/assets/CHANGELOG.md`)
so the dialog can read it at runtime without bundling changelog text into
code. Keep this asset in sync with the repo-root `CHANGELOG.md` (a Gradle copy
task or a CI check should enforce this — see "Keeping the asset in sync"
below).

### Parser

Parse only the small subset of Markdown the changelog actually uses: version
headers (`## [x.y.z] - date`), section headers (`### Added` / `### Changed` /
`### Fixed`), and bullet lists (`- ...`), with `**bold**` inline. Cap the
number of entries shown — 5 is the standard:

```kotlin
data class ChangelogEntry(val header: String, val body: String)

/**
 * Parses CHANGELOG.md content and returns up to [maxEntries] of the most
 * recent version entries (lines starting with "## ["), in file order.
 */
fun parseChangelog(content: String, maxEntries: Int = 5): List<ChangelogEntry> {
    val entries = mutableListOf<ChangelogEntry>()
    var currentHeader: String? = null
    val currentBody = StringBuilder()

    for (line in content.lines()) {
        when {
            line.startsWith("## [") -> {
                currentHeader?.let { entries.add(ChangelogEntry(it, currentBody.toString().trimEnd())) }
                if (entries.size >= maxEntries) break
                currentHeader = line.removePrefix("## ")
                currentBody.clear()
            }
            currentHeader != null && line.trimEnd() != "---" -> currentBody.appendLine(line)
        }
    }
    if (currentHeader != null && entries.size < maxEntries) {
        entries.add(ChangelogEntry(currentHeader, currentBody.toString().trimEnd()))
    }
    return entries.take(maxEntries)
}
```

### Dialog

`ChangelogDialog` is an `AlertDialog` with a scrollable body, opened from the
About sub-screen's "What's New" button:

- Title: **"What's New"**
- Body: each entry's header (`titleSmall`, primary colour) followed by its
  body, rendered with a small Markdown subset renderer (`### ` -> `labelMedium`
  secondary-coloured sub-heading, `- `/`* ` -> bulleted `bodySmall` row,
  `**bold**` -> bold span, blank line -> spacing). `HorizontalDivider()`
  between entries.
- Confirm button: **"Close"**.
- Dismiss-slot button: **"View full changelog"**, opens the repo's
  `CHANGELOG.md` on GitHub via `Intent(Intent.ACTION_VIEW, ...)`.
- If the asset is missing or unparsable, show "No changelog available."
  instead of crashing (`runCatching { ... }.getOrDefault(emptyList())`).

### Keeping the asset in sync

`app/src/main/assets/CHANGELOG.md` must be a copy of the repo-root
`CHANGELOG.md` (or generated from it). If your app uses the
`changelog/unreleased/*.json` fragment workflow, the consolidation step that
writes the root `CHANGELOG.md` should also copy it into `app/src/main/assets/`
so the in-app dialog always matches the released history.

## Accessibility checklist for Settings

- Every `SettingsNavItem` and other clickable row: `.semantics { role = Role.Button }`
  before `.clickable {}`.
- `SwitchRow` and any other two-state toggle: `Role.Switch` plus
  `stateDescription` reflecting the current state.
- Theme pickers / icon pickers / format pickers: `Role.RadioButton` on each
  option.
- Status text that changes in response to a user action (e.g. permission
  granted/denied) needs `Modifier.semantics { liveRegion = LiveRegionMode.Polite }`
  (or `Assertive` for errors).
- Any colour-coded status (granted/denied, enabled/disabled) must also carry a
  text label or distinct icon shape, not colour alone.
- Run `python3 a11y_check.py` after adding new Settings rows.
