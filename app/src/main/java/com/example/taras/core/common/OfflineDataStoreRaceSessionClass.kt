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
        val RACES_DATA_KEY = stringPreferencesKey("racesData")
    }

    suspend fun saveRacesData(json: String) {
        context.raceSessionDataStore.edit {
            it[RACES_DATA_KEY] = json
        }
    }

    val racesData = context.raceSessionDataStore.data.map {
        it[RACES_DATA_KEY]
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
}

