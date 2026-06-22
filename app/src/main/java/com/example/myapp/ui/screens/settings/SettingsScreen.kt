package com.example.myapp.ui.screens.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.BuildConfig
import com.example.myapp.MyApplication
import com.example.myapp.ui.theme.AppTheme
import com.example.myapp.ui.theme.CompactThemePicker
import com.example.myapp.ui.theme.SavedThemesList

private enum class SettingsSubScreen { APPEARANCE, NOTIFICATIONS, ABOUT }

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
        SettingsSubScreen.NOTIFICATIONS -> NotificationsSubScreen(
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

            SettingsSectionHeader("Notifications")
            SettingsNavItem(
                title    = "Notifications",
                subtitle = "Reminders and permission settings",
                icon     = Icons.Filled.Notifications,
                onClick  = { onNavigate(SettingsSubScreen.NOTIFICATIONS) },
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
    val activeProfileId = prefs.customActiveProfileId

    var themeName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(activeProfileId) {
        if (activeProfileId != -1L) {
            val active = savedThemes.find { it.id == activeProfileId }
            if (active != null) themeName = active.name
        } else {
            themeName = "Theme ${savedThemes.size + 1}"
        }
    }

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
                currentTheme       = currentTheme,
                wcagMode           = prefs.wcagMode,
                onThemeSelected    = { viewModel.setTheme(it.name) },
                onWcagToggled      = { viewModel.setWcagMode(it) },
                customPrimaryHue   = prefs.customPrimaryHue,
                customSecondaryHue = prefs.customSecondaryHue,
                customTertiaryHue  = prefs.customTertiaryHue,
                onCustomHuesChange = { pH, sH, tH -> viewModel.setCustomHues(pH, sH, tH) },
            )

            HorizontalDivider()

            // ── Save current custom theme ─────────────────────────────────────
            Text(
                "Saved themes",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (currentTheme == AppTheme.CUSTOM) {
                OutlinedTextField(
                    value         = themeName,
                    onValueChange = { themeName = it },
                    label         = { Text("Theme name") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                )
                if (activeProfileId != -1L) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick  = { if (themeName.isNotBlank()) viewModel.saveCustomColorTheme(themeName.trim()) },
                            enabled  = themeName.isNotBlank(),
                            modifier = Modifier.weight(1f),
                        ) { Text("Save as new") }
                        FilledTonalButton(
                            onClick  = { if (themeName.isNotBlank()) viewModel.updateCustomColorTheme(themeName.trim()) },
                            enabled  = themeName.isNotBlank(),
                            modifier = Modifier.weight(1f),
                        ) { Text("Update") }
                    }
                } else {
                    FilledTonalButton(
                        onClick  = { if (themeName.isNotBlank()) viewModel.saveCustomColorTheme(themeName.trim()) },
                        enabled  = themeName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Save") }
                }
            }

            // ── Saved themes list ─────────────────────────────────────────────
            SavedThemesList(
                themes          = savedThemes,
                activeProfileId = activeProfileId,
                onLoad          = { viewModel.loadCustomColorTheme(it) },
                onDelete        = { viewModel.deleteCustomColorTheme(it) },
                onRename        = { theme, name -> viewModel.renameCustomColorTheme(theme, name) },
            )
        }
    }

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

@Composable
private fun NotificationsSubScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel,
) {
    val prefs by viewModel.preferences.collectAsState()
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            else true
        )
    }
    var hasExactAlarmPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
            else true
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                else true
                hasExactAlarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
                else true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasNotificationPermission = granted }

    SettingsSubScreenScaffold(title = "Notifications", onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
        ) {
            SwitchRow(
                label         = "Reminders",
                supportingText = "Enable reminders and notifications",
                checked       = prefs.reminderEnabled,
                onCheckedChange = { viewModel.setReminderEnabled(it) },
            )
            if (prefs.reminderEnabled) {
                PermissionWarningBanner(
                    hasNotificationPermission = hasNotificationPermission,
                    hasExactAlarmPermission   = hasExactAlarmPermission,
                    onFixNotification = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    },
                    onFixExactAlarm = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            context.startActivity(
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    .apply { data = Uri.fromParts("package", context.packageName, null) }
                            )
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}
