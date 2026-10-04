package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.OfflineDataStoreAppearance
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceViewModel(private val offlineDataStoreAppearance: OfflineDataStoreAppearance) :
    ViewModel() {
    val appearanceData = offlineDataStoreAppearance.appearanceData.stateIn(
        scope = viewModelScope,
        initialValue = "Light",
        started = WhileSubscribed(5000)
    )

    val widgetThemeData = offlineDataStoreAppearance.widgetThemeData.stateIn(
        scope = viewModelScope,
        initialValue = "System Default",
        started = WhileSubscribed(5000)
    )

    fun updateAppearance(appearance: String) {
        viewModelScope.launch {
            offlineDataStoreAppearance.saveAppearance(appearance)
        }
    }

    fun updateWidgetTheme(theme: String) {
        viewModelScope.launch {
            offlineDataStoreAppearance.saveWidgetTheme(theme)
        }
    }
}
