package com.example.myapp.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.BuildConfig
import com.example.myapp.MyApplication
import com.example.myapp.ui.theme.AppTheme
import com.example.myapp.ui.theme.CompactThemePicker
import com.example.myapp.ui.theme.SavedThemesList

private enum class SettingsSubScreen { APPEARANCE, ABOUT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToLicenses: () -> Unit,
    app: MyApplication = LocalContext.current.applicationContext as MyApplication,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(app.preferencesStore, app.customColorThemeDao)
    ),
) {
    var subScreen by rememberSaveable { mutableStateOf<SettingsSubScreen?>(null) }

    BackHandler(enabled = subScreen != null) {
        subScreen = null
    }

    when (subScreen) {
        null -> SettingsMainList(onNavigate = { subScreen = it })
        SettingsSubScreen.APPEARANCE -> AppearanceSubScreen(
            onBack = { subScreen = null },
            viewModel = viewModel,
        )
        SettingsSubScreen.ABOUT -> AboutSubScreen(
            onBack = { subScreen = null },
            onNavigateToLicenses = onNavigateToLicenses,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsMainList(
    onNavigate: (SettingsSubScreen) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            SettingsSectionHeader("Personalisation")
            SettingsNavItem(
                title    = "Appearance",
                subtitle = "Light, dark, or system theme",
                icon     = Icons.Filled.Palette,
                onClick  = { onNavigate(SettingsSubScreen.APPEARANCE) },
            )

            HorizontalDivider()

            SettingsSectionHeader("About")
            SettingsNavItem(
                title    = "About",
                subtitle = "Version, what's new, and licenses",
                icon     = Icons.Filled.Info,
                onClick  = { onNavigate(SettingsSubScreen.ABOUT) },
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AppearanceSubScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel,
) {
    val prefs by viewModel.preferences.collectAsState()
    val currentTheme = runCatching { AppTheme.valueOf(prefs.theme) }.getOrDefault(AppTheme.CORAL)
    val savedThemes  by viewModel.customColorThemes.collectAsState()

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    SettingsSubScreenScaffold(title = "Appearance", onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CompactThemePicker(
                currentTheme    = currentTheme,
                wcagMode        = prefs.wcagMode,
                onThemeSelected = { viewModel.setTheme(it.name) },
                onWcagToggled   = { viewModel.setWcagMode(it) },
            )

            HorizontalDivider()

            // ── Save current custom theme ─────────────────────────────────────
            Text(
                "Saved themes",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedButton(
                onClick  = { showSaveDialog = true },
                enabled  = currentTheme == AppTheme.CUSTOM,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save current theme")
            }

            if (currentTheme != AppTheme.CUSTOM) {
                Text(
                    "Switch to a custom theme to save it as a profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // ── Saved themes list ─────────────────────────────────────────────
            SavedThemesList(
                themes          = savedThemes,
                activeProfileId = prefs.customActiveProfileId,
                onLoad          = { viewModel.loadCustomColorTheme(it) },
                onDelete        = { viewModel.deleteCustomColorTheme(it) },
                onRename        = { theme, name -> viewModel.renameCustomColorTheme(theme, name) },
            )
        }
    }

    // ── Save-theme name dialog ────────────────────────────────────────────────
    if (showSaveDialog) {
        SaveThemeDialog(
            initialName = "My Theme",
            onConfirm   = { name ->
                viewModel.saveCustomColorTheme(name)
                showSaveDialog = false
            },
            onDismiss   = { showSaveDialog = false },
        )
    }
}

@Composable
private fun SaveThemeDialog(
    initialName: String,
    onConfirm:   (String) -> Unit,
    onDismiss:   () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Save theme") },
        text             = {
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                singleLine    = true,
                label         = { Text("Theme name") },
                modifier      = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick  = { if (name.trim().isNotEmpty()) onConfirm(name.trim()) },
                enabled  = name.trim().isNotEmpty(),
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun AboutSubScreen(
    onBack: () -> Unit,
    onNavigateToLicenses: () -> Unit,
) {
    val context = LocalContext.current
    var showChangelog by remember { mutableStateOf(false) }

    SettingsSubScreenScaffold(title = "About", onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // TODO: Replace with your app's About text.
            Text(
                "Add your app description here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            FilledTonalButton(
                onClick  = { showChangelog = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("What's New")
            }

            OutlinedButton(
                onClick  = onNavigateToLicenses,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open-source licenses")
            }

            Text(
                "Version ${BuildConfig.VERSION_NAME}",
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
        }
    }

    if (showChangelog) {
        val entries = remember {
            runCatching {
                context.assets.open("CHANGELOG.md").bufferedReader().readText()
                    .let { parseChangelog(it) }
            }.getOrDefault(emptyList())
        }
        ChangelogDialog(
            entries   = entries,
            onDismiss = { showChangelog = false },
        )
    }
}
