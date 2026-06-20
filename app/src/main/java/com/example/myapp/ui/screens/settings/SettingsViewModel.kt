package com.example.myapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.db.dao.CustomColorThemeDao
import com.example.myapp.data.db.entities.CustomColorTheme
import com.example.myapp.data.preferences.AppPreferences
import com.example.myapp.data.preferences.AppPreferencesStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesStore: AppPreferencesStore,
    private val customColorThemeDao: CustomColorThemeDao,
) : ViewModel() {

    /** Exposes the full preferences as a [StateFlow] for the settings UI. */
    val preferences: StateFlow<AppPreferences> = preferencesStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppPreferences(),
        )

    /** Live list of all user-saved custom colour themes. */
    val customColorThemes: StateFlow<List<CustomColorTheme>> = customColorThemeDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun setTheme(theme: String) {
        viewModelScope.launch { preferencesStore.setTheme(theme) }
    }

    fun setWcagMode(enabled: Boolean) {
        viewModelScope.launch { preferencesStore.setWcagMode(enabled) }
    }

    fun setDeliveryMode(mode: String) {
        viewModelScope.launch { preferencesStore.setDeliveryMode(mode) }
    }

    fun setCustomHues(primaryHue: Float, secondaryHue: Float, tertiaryHue: Float) {
        viewModelScope.launch {
            preferencesStore.setCustomHues(primaryHue, secondaryHue, tertiaryHue)
        }
    }

    /**
     * Saves the current custom hue values as a named profile.
     * If a profile with the same ID already exists it is updated in place.
     * Sets [customActiveProfileId] to the upserted row ID.
     */
    fun saveCustomColorTheme(name: String) {
        viewModelScope.launch {
            val prefs = preferences.value
            val theme = CustomColorTheme(
                name         = name,
                primaryHue   = prefs.customPrimaryHue,
                secondaryHue = prefs.customSecondaryHue,
                tertiaryHue  = prefs.customTertiaryHue,
                mode         = "LIGHT",  // Custom themes always use the CUSTOM (light) path
            )
            val id = customColorThemeDao.upsert(theme)
            preferencesStore.setCustomActiveProfileId(id)
        }
    }

    /**
     * Loads a saved profile: writes its hues to DataStore, marks it as active,
     * and switches the app theme to AppTheme.CUSTOM.
     */
    fun loadCustomColorTheme(theme: CustomColorTheme) {
        viewModelScope.launch {
            preferencesStore.setCustomHues(theme.primaryHue, theme.secondaryHue, theme.tertiaryHue)
            preferencesStore.setCustomActiveProfileId(theme.id)
            preferencesStore.setTheme("CUSTOM")
        }
    }

    /**
     * Deletes a saved profile. If the deleted profile was the active one,
     * resets [customActiveProfileId] to -1.
     */
    fun deleteCustomColorTheme(theme: CustomColorTheme) {
        viewModelScope.launch {
            customColorThemeDao.delete(theme)
            if (preferences.value.customActiveProfileId == theme.id) {
                preferencesStore.setCustomActiveProfileId(-1L)
            }
        }
    }

    /**
     * Renames a saved profile by upserting it with the new name.
     * The active profile ID is unchanged (same row, same ID).
     */
    fun renameCustomColorTheme(theme: CustomColorTheme, newName: String) {
        viewModelScope.launch {
            customColorThemeDao.upsert(theme.copy(name = newName))
        }
    }

    class Factory(
        private val preferencesStore: AppPreferencesStore,
        private val customColorThemeDao: CustomColorThemeDao,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferencesStore, customColorThemeDao) as T
        }
    }
}
