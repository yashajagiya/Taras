package com.example.taras.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture repository contract for Theme and Widget Appearance.
 */
interface AppearanceRepository {
    val appearanceData: Flow<String>
    val widgetThemeData: Flow<String>

    suspend fun saveAppearance(appearance: String)
    suspend fun saveWidgetTheme(widgetTheme: String)
}
