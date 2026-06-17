package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapp.ui.AppState
import com.example.myapp.ui.MainViewModel
import com.example.myapp.ui.navigation.Screen
import com.example.myapp.ui.screens.home.HomeScreen
import com.example.myapp.ui.screens.licenses.LicensesScreen
import com.example.myapp.ui.screens.settings.SettingsScreen
import com.example.myapp.ui.theme.AppTheme as AppThemeEnum
import com.example.myapp.ui.theme.MyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MyApplication
        val initialPrefs = runBlocking { app.preferencesStore.preferences.first() }

        setContent {
            val mainVm: MainViewModel = viewModel(
                factory = MainViewModel.Factory(app)
            )
            val appState by mainVm.appState.collectAsState()
            val appPrefs by app.preferencesStore.preferences.collectAsState(initial = initialPrefs)

            val currentTheme = runCatching {
                AppThemeEnum.valueOf(appPrefs.theme)
            }.getOrDefault(AppThemeEnum.CORAL)

            MyAppTheme(appTheme = currentTheme, wcag = appPrefs.wcagMode) {
                when (appState) {
                    AppState.LOADING -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    AppState.READY -> MainNavHost(app = app)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun MainNavHost(app: MyApplication) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val bottomNavRoutes = listOf(Screen.Home.route, Screen.Settings.route)
    val showBottomBar = bottomNavRoutes.any { currentRoute?.startsWith(it) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToLicenses = { navController.navigate(Screen.Licenses.route) }
                )
            }
            composable(Screen.Licenses.route) {
                LicensesScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
