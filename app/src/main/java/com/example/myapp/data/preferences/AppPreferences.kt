package com.example.myapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prefsDataStore by preferencesDataStore(name = "myapp_prefs")

data class AppPreferences(
    val theme: String = "CORAL",
    val wcagMode: Boolean = false,
    val deliveryMode: String = "NOTIFICATION",
    val reminderEnabled: Boolean = false,
    // Custom HSL hues for AppTheme.CUSTOM
    val customPrimaryHue: Float = 0f,
    val customSecondaryHue: Float = 120f,
    val customTertiaryHue: Float = 240f,
    // ID of the active saved custom colour profile (-1 = none)
    val customActiveProfileId: Long = -1L,
    // Add more user-configurable preferences here
)

class AppPreferencesStore(private val context: Context) {

    private object Keys {
        val THEME                    = stringPreferencesKey("theme")
        val WCAG_MODE                = booleanPreferencesKey("wcag_mode")
        val DELIVERY_MODE            = stringPreferencesKey("delivery_mode")
        val REMINDER_ENABLED         = booleanPreferencesKey("reminder_enabled")
        val CUSTOM_PRIMARY_HUE       = floatPreferencesKey("custom_primary_hue")
        val CUSTOM_SECONDARY_HUE     = floatPreferencesKey("custom_secondary_hue")
        val CUSTOM_TERTIARY_HUE      = floatPreferencesKey("custom_tertiary_hue")
        val CUSTOM_ACTIVE_PROFILE_ID = longPreferencesKey("custom_active_profile_id")
    }

    val preferences: Flow<AppPreferences> = context.prefsDataStore.data.map { prefs ->
        AppPreferences(
            theme                  = prefs[Keys.THEME] ?: "CORAL",
            wcagMode               = prefs[Keys.WCAG_MODE] ?: false,
            deliveryMode           = prefs[Keys.DELIVERY_MODE] ?: "NOTIFICATION",
            reminderEnabled        = prefs[Keys.REMINDER_ENABLED] ?: false,
            customPrimaryHue       = prefs[Keys.CUSTOM_PRIMARY_HUE] ?: 0f,
            customSecondaryHue     = prefs[Keys.CUSTOM_SECONDARY_HUE] ?: 120f,
            customTertiaryHue      = prefs[Keys.CUSTOM_TERTIARY_HUE] ?: 240f,
            customActiveProfileId  = prefs[Keys.CUSTOM_ACTIVE_PROFILE_ID] ?: -1L,
        )
    }

    suspend fun setTheme(theme: String) {
        context.prefsDataStore.edit { it[Keys.THEME] = theme }
    }

    suspend fun setWcagMode(enabled: Boolean) {
        context.prefsDataStore.edit { it[Keys.WCAG_MODE] = enabled }
    }

    suspend fun setDeliveryMode(mode: String) {
        context.prefsDataStore.edit { it[Keys.DELIVERY_MODE] = mode }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.prefsDataStore.edit { it[Keys.REMINDER_ENABLED] = enabled }
    }

    suspend fun setCustomHues(primaryHue: Float, secondaryHue: Float, tertiaryHue: Float) {
        context.prefsDataStore.edit { prefs ->
            prefs[Keys.CUSTOM_PRIMARY_HUE]   = primaryHue
            prefs[Keys.CUSTOM_SECONDARY_HUE] = secondaryHue
            prefs[Keys.CUSTOM_TERTIARY_HUE]  = tertiaryHue
        }
    }

    suspend fun setCustomActiveProfileId(id: Long) {
        context.prefsDataStore.edit { it[Keys.CUSTOM_ACTIVE_PROFILE_ID] = id }
    }
}
