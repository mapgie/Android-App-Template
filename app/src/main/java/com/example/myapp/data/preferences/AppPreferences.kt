package com.example.myapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prefsDataStore by preferencesDataStore(name = "myapp_prefs")

data class AppPreferences(
    val theme: String = "CORAL",
    val wcagMode: Boolean = false,
    // Add more user-configurable preferences here
)

class AppPreferencesStore(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val WCAG_MODE = booleanPreferencesKey("wcag_mode")
    }

    val preferences: Flow<AppPreferences> = context.prefsDataStore.data.map { prefs ->
        AppPreferences(
            theme = prefs[Keys.THEME] ?: "CORAL",
            wcagMode = prefs[Keys.WCAG_MODE] ?: false,
        )
    }

    suspend fun setTheme(theme: String) {
        context.prefsDataStore.edit { it[Keys.THEME] = theme }
    }

    suspend fun setWcagMode(enabled: Boolean) {
        context.prefsDataStore.edit { it[Keys.WCAG_MODE] = enabled }
    }
}
