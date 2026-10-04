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
import com.example.taras.core.widgets.FavoriteDriverWidget
import com.example.taras.core.widgets.FavoriteDriverWidgetReceiver
import com.example.taras.core.widgets.MyAppWidgetReceiver
import com.example.taras.core.widgets.NextWidget
import com.example.taras.core.widgets.RaceResultWidget
import com.example.taras.core.widgets.RaceResultWidgetReceiver
import com.example.taras.core.widgets.StandingsWidget
import com.example.taras.core.widgets.StandingsWidgetReceiver
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
        it[APPEARANCE] ?: "System Default"
    }

    suspend fun saveAppearance(appearance: String) {
        context.appearanceDataStore.edit {
            it[APPEARANCE] = appearance
        }
        triggerAllWidgetUpdates()
    }

    val widgetThemeData: Flow<String> = context.appearanceDataStore.data.map {
        it[WIDGET_THEME] ?: "System Default"
    }

    suspend fun saveWidgetTheme(widgetTheme: String) {
        context.appearanceDataStore.edit {
            it[WIDGET_THEME] = widgetTheme
        }
        triggerAllWidgetUpdates()
    }

    suspend fun triggerAllWidgetUpdates() {
        val appContext = context.applicationContext

        // 1. Update each Glance widget independently with isolated error handling
        try { NextWidget().updateAll(appContext) } catch (e: Exception) { Log.e(TAG, "Error updating NextWidget", e) }
        try { StandingsWidget().updateAll(appContext) } catch (e: Exception) { Log.e(TAG, "Error updating StandingsWidget", e) }
        try { FavoriteDriverWidget().updateAll(appContext) } catch (e: Exception) { Log.e(TAG, "Error updating FavoriteDriverWidget", e) }
        try { RaceResultWidget().updateAll(appContext) } catch (e: Exception) { Log.e(TAG, "Error updating RaceResultWidget", e) }

        // 2. Broadcast APPWIDGET_UPDATE to notify Android AppWidget host
        val receivers = listOf(
            MyAppWidgetReceiver::class.java,
            StandingsWidgetReceiver::class.java,
            FavoriteDriverWidgetReceiver::class.java,
            RaceResultWidgetReceiver::class.java
        )
        try {
            val appWidgetManager = AppWidgetManager.getInstance(appContext)
            for (receiverClass in receivers) {
                val componentName = ComponentName(appContext, receiverClass)
                val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
                if (widgetIds.isNotEmpty()) {
                    val updateIntent = Intent(appContext, receiverClass).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                    }
                    appContext.sendBroadcast(updateIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error broadcasting widget update", e)
        }
    }
}