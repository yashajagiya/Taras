package com.example.taras.core.common

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.updateAll
import com.example.taras.core.widgets.CountdownMatrixWidget
import com.example.taras.core.widgets.CountdownMatrixWidgetReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appearanceDataStore by preferencesDataStore(name = "AppearanceSaver")

class OfflineDataStoreAppearance(private val context: Context) {
    companion object {
        val APPEARANCE = stringPreferencesKey("appearance")
        val WIDGET_THEME = stringPreferencesKey("widget_theme")
        private const val TAG = "WidgetThemeStore"
    }

    val appearanceData: Flow<String> = context.appearanceDataStore.data.map {
        it[APPEARANCE] ?: "Light"
    }

    suspend fun saveAppearance(appearance: String) {
        context.appearanceDataStore.edit {
            it[APPEARANCE] = appearance
        }
        triggerAllWidgetUpdates()
    }

    val widgetThemeData: Flow<String> = context.appearanceDataStore.data.map {
        it[WIDGET_THEME] ?: "Dark"
    }

    suspend fun saveWidgetTheme(widgetTheme: String) {
        context.appearanceDataStore.edit {
            it[WIDGET_THEME] = widgetTheme
        }
        triggerAllWidgetUpdates()
    }

    suspend fun triggerAllWidgetUpdates() {
        val appContext = context.applicationContext

        // Update CountdownMatrixWidget safely (only for active widgets with valid AppWidgetInfo)
        // Glance handles its own session management and DataStore updates.
        // We must NOT broadcast ACTION_APPWIDGET_UPDATE here as it triggers a duplicate concurrent SessionWorker.
        try {
            CountdownMatrixWidget().safeUpdateAll(appContext)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating CountdownMatrixWidget", e)
        }
    }
}