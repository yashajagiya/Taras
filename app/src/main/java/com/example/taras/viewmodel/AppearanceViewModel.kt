package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.OfflineDataStoreAppearance
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceViewModel(private val offlineDataStoreAppearance: OfflineDataStoreAppearance) :
    ViewModel() {
    val appearanceData = offlineDataStoreAppearance.appearanceData.stateIn(
        scope = viewModelScope,
        initialValue = "Light",
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000)
    )

    fun updateAppearance(appearance: String) {
        viewModelScope.launch {
            offlineDataStoreAppearance.saveAppearance(appearance)
        }
    }
}
