package com.example.myapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppState { LOADING, READY }

class MainViewModel(private val app: MyApplication) : ViewModel() {

    private val _appState = MutableStateFlow(AppState.LOADING)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        viewModelScope.launch {
            // Perform any async startup work here (e.g. DB migrations, one-time seeds).
            // Set READY when the app is ready to show content.
            _appState.value = AppState.READY
        }
    }

    class Factory(private val app: MyApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app) as T
        }
    }
}
