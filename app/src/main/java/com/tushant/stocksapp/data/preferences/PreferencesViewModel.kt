package com.tushant.stocksapp.data.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val userPreferences: StateFlow<UserPreferences> = preferencesManager.userPreferences
        .catch { exception ->
            emit(UserPreferences())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun toggleDarkTheme() {
        viewModelScope.launch {
            val currentPreferences = userPreferences.value
            preferencesManager.updateDarkTheme(!currentPreferences.isDarkTheme)
        }
    }

    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateDarkTheme(isDarkTheme)
        }
    }
}