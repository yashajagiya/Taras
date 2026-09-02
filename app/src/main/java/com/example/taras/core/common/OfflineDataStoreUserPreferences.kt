package com.example.taras.core.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userNameDataStore by preferencesDataStore(name = "userData")

class UserPreferences(private val context: Context) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    val userNameFlow: Flow<String> = context.userNameDataStore.data
        .map {
            it[USERNAME_KEY] ?: "Guest"
        }

    suspend fun saveUserName(name: String) {
        context.userNameDataStore.edit {
            it[USERNAME_KEY] = name
        }
    }
}