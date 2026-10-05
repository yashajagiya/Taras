package com.example.taras.core.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.raceSessionDataStore by preferencesDataStore(name = "offlineData")

class CurrentData(private val context: Context) {

    companion object {
        val SESSION_NAME_KEY = stringPreferencesKey("sessionName")
        val SESSION_TIME_KEY = stringPreferencesKey("sessionTime")

        // v2 Namespaced Cache Keys
        val OVERVIEW_DATA_KEY = stringPreferencesKey("overviewData_v2")
        val RACES_DATA_KEY = stringPreferencesKey("racesData_v2")
        val LAST_RACE_RESULT_KEY = stringPreferencesKey("lastRaceResult_v2")

        // Legacy v1 keys (for cleanup)
        private val LEGACY_RACES_DATA_KEY = stringPreferencesKey("racesData")
        private val LEGACY_LAST_RACE_RESULT_KEY = stringPreferencesKey("lastRaceResult")
    }

    suspend fun saveOverviewData(json: String) {
        context.raceSessionDataStore.edit {
            it[OVERVIEW_DATA_KEY] = json
        }
    }

    val overviewData = context.raceSessionDataStore.data.map {
        it[OVERVIEW_DATA_KEY]
    }

    suspend fun saveRacesData(json: String) {
        context.raceSessionDataStore.edit {
            it[RACES_DATA_KEY] = json
        }
    }

    val racesData = context.raceSessionDataStore.data.map {
        it[RACES_DATA_KEY]
    }

    suspend fun saveLastRaceResult(json: String) {
        context.raceSessionDataStore.edit {
            it[LAST_RACE_RESULT_KEY] = json
        }
    }

    val lastRaceResult = context.raceSessionDataStore.data.map {
        it[LAST_RACE_RESULT_KEY]
    }

    suspend fun saveCurrentSessionStatus(sessionName: String, sessionTime: String) {
        context.raceSessionDataStore.edit {
            it[SESSION_NAME_KEY] = sessionName
            it[SESSION_TIME_KEY] = sessionTime
        }
    }

    val isCurrentSessionSaved = context.raceSessionDataStore.data.map {
        it.contains(SESSION_NAME_KEY) && it.contains(SESSION_TIME_KEY)
    }

    suspend fun clearLegacyCache() {
        context.raceSessionDataStore.edit {
            it.remove(LEGACY_RACES_DATA_KEY)
            it.remove(LEGACY_LAST_RACE_RESULT_KEY)
        }
    }
}
