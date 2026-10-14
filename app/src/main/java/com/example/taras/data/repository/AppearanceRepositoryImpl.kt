package com.example.taras.data.repository

import com.example.taras.core.common.OfflineDataStoreAppearance
import com.example.taras.domain.repository.AppearanceRepository
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation of AppearanceRepository wrapping OfflineDataStoreAppearance.
 */
class AppearanceRepositoryImpl(
    private val appearanceDataStore: OfflineDataStoreAppearance
) : AppearanceRepository {

    override val appearanceData: Flow<String>
        get() = appearanceDataStore.appearanceData

    override val widgetThemeData: Flow<String>
        get() = appearanceDataStore.widgetThemeData

    override suspend fun saveAppearance(appearance: String) {
        appearanceDataStore.saveAppearance(appearance)
    }

    override suspend fun saveWidgetTheme(widgetTheme: String) {
        appearanceDataStore.saveWidgetTheme(widgetTheme)
    }
}
