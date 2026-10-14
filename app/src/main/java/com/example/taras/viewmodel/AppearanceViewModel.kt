package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.OfflineDataStoreAppearance
import com.example.taras.core.common.TarasApplication
import com.example.taras.domain.repository.AppearanceRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceViewModel(
    private val appearanceRepository: AppearanceRepository = TarasApplication.instance.appContainer.appearanceRepository,
    private val offlineDataStoreAppearance: OfflineDataStoreAppearance = TarasApplication.instance.appContainer.appearanceDataStore
) : ViewModel() {

    // Backwards-compatible constructor
    constructor(offlineDataStoreAppearance: OfflineDataStoreAppearance) : this(
        appearanceRepository = TarasApplication.instance.appContainer.appearanceRepository,
        offlineDataStoreAppearance = offlineDataStoreAppearance
    )

    val appearanceData = appearanceRepository.appearanceData.stateIn(
        scope = viewModelScope,
        initialValue = "Light",
        started = WhileSubscribed(5000)
    )

    val widgetThemeData = appearanceRepository.widgetThemeData.stateIn(
        scope = viewModelScope,
        initialValue = "Dark",
        started = WhileSubscribed(5000)
    )

    fun updateAppearance(appearance: String) {
        viewModelScope.launch {
            appearanceRepository.saveAppearance(appearance)
        }
    }

    fun updateWidgetTheme(theme: String) {
        viewModelScope.launch {
            appearanceRepository.saveWidgetTheme(theme)
        }
    }
}

class AppearanceViewModelFactory(
    private val appearanceStore: OfflineDataStoreAppearance? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppearanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return if (appearanceStore != null) {
                AppearanceViewModel(appearanceStore) as T
            } else {
                AppearanceViewModel() as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
