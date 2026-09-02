package com.example.taras.core.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appearanceDataStore by preferencesDataStore(name = "AppearanceSaver")

class OfflineDataStoreAppearance(private val context: Context) {
    companion object {
        val APPEARANCE = stringPreferencesKey("appearance")
    }
//    suspend fun saveData (json: String) {
//        context.appearanceDataStore.edit {
//            it[APPEARANCE] = json
//        }
//    }
    val appearanceData : Flow<String>  = context.appearanceDataStore.data.map {
        it[APPEARANCE] ?: "Light"
    }
    suspend fun saveAppearance(appearance: String) {
        context.appearanceDataStore.edit {
            it[APPEARANCE] = appearance
        }
    }

}