package com.example.myapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
    // Custom theme: legacy HSL hues, kept as fallback for pre-ARGB installs
    val customPrimaryHue: Float = 0f,
    val customSecondaryHue: Float = 120f,
    val customTertiaryHue: Float = 240f,
    // Custom theme: exact picked colours (0 = unset, fall back to the hue above)
    val customPrimaryArgb: Int = 0,
    val customSecondaryArgb: Int = 0,
    val customTertiaryArgb: Int = 0,
    // Custom theme: background overrides per mode (0 = derived from primary)
    val customLightBackgroundArgb: Int = 0,
    val customDarkBackgroundArgb: Int = 0,
    // Custom theme: "LIGHT", "DARK", or "SYSTEM"
    val customThemeMode: String = "SYSTEM",
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
        val CUSTOM_PRIMARY_ARGB      = intPreferencesKey("custom_primary_argb")
        val CUSTOM_SECONDARY_ARGB    = intPreferencesKey("custom_secondary_argb")
        val CUSTOM_TERTIARY_ARGB     = intPreferencesKey("custom_tertiary_argb")
        val CUSTOM_LIGHT_BG_ARGB     = intPreferencesKey("custom_light_bg_argb")
        val CUSTOM_DARK_BG_ARGB      = intPreferencesKey("custom_dark_bg_argb")
        val CUSTOM_THEME_MODE        = stringPreferencesKey("custom_theme_mode")
        val CUSTOM_ACTIVE_PROFILE_ID = longPreferencesKey("custom_active_profile_id")
    }

    val preferences: Flow<AppPreferences> = context.prefsDataStore.data.map { prefs ->
        AppPreferences(
            theme                     = prefs[Keys.THEME] ?: "CORAL",
            wcagMode                  = prefs[Keys.WCAG_MODE] ?: false,
            deliveryMode              = prefs[Keys.DELIVERY_MODE] ?: "NOTIFICATION",
            reminderEnabled           = prefs[Keys.REMINDER_ENABLED] ?: false,
            customPrimaryHue          = prefs[Keys.CUSTOM_PRIMARY_HUE] ?: 0f,
            customSecondaryHue        = prefs[Keys.CUSTOM_SECONDARY_HUE] ?: 120f,
            customTertiaryHue         = prefs[Keys.CUSTOM_TERTIARY_HUE] ?: 240f,
            customPrimaryArgb         = prefs[Keys.CUSTOM_PRIMARY_ARGB] ?: 0,
            customSecondaryArgb       = prefs[Keys.CUSTOM_SECONDARY_ARGB] ?: 0,
            customTertiaryArgb        = prefs[Keys.CUSTOM_TERTIARY_ARGB] ?: 0,
            customLightBackgroundArgb = prefs[Keys.CUSTOM_LIGHT_BG_ARGB] ?: 0,
            customDarkBackgroundArgb  = prefs[Keys.CUSTOM_DARK_BG_ARGB] ?: 0,
            customThemeMode           = prefs[Keys.CUSTOM_THEME_MODE] ?: "SYSTEM",
            customActiveProfileId     = prefs[Keys.CUSTOM_ACTIVE_PROFILE_ID] ?: -1L,
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

    suspend fun setCustomArgbs(primaryArgb: Int, secondaryArgb: Int, tertiaryArgb: Int) {
        context.prefsDataStore.edit { prefs ->
            prefs[Keys.CUSTOM_PRIMARY_ARGB]   = primaryArgb
            prefs[Keys.CUSTOM_SECONDARY_ARGB] = secondaryArgb
            prefs[Keys.CUSTOM_TERTIARY_ARGB]  = tertiaryArgb
        }
    }

    suspend fun setCustomBackgroundArgbs(lightArgb: Int, darkArgb: Int) {
        context.prefsDataStore.edit { prefs ->
            prefs[Keys.CUSTOM_LIGHT_BG_ARGB] = lightArgb
            prefs[Keys.CUSTOM_DARK_BG_ARGB]  = darkArgb
        }
    }

    suspend fun setCustomThemeMode(mode: String) {
        context.prefsDataStore.edit { it[Keys.CUSTOM_THEME_MODE] = mode }
    }

    suspend fun setCustomActiveProfileId(id: Long) {
        context.prefsDataStore.edit { it[Keys.CUSTOM_ACTIVE_PROFILE_ID] = id }
    }
}
