package com.example.myapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.preferences.AppPreferences
import com.example.myapp.data.preferences.AppPreferencesStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferencesStore: AppPreferencesStore) : ViewModel() {

    /** Exposes the full preferences as a [StateFlow] for the settings UI. */
    val preferences: StateFlow<AppPreferences> = preferencesStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppPreferences(),
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

    class Factory(private val preferencesStore: AppPreferencesStore) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferencesStore) as T
        }
    }
}
