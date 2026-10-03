package com.example.taras.core.common

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userNameDataStore by preferencesDataStore(name = "userData")

class UserPreferences(private val context: Context) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val FAVORITE_DRIVER_NUMBER_KEY = stringPreferencesKey("favorite_driver_number")
        val FAVORITE_DRIVER_NAME_KEY = stringPreferencesKey("favorite_driver_name")
        val FAVORITE_DRIVER_RANK_KEY = stringPreferencesKey("favorite_driver_rank")
        val FAVORITE_DRIVER_POINTS_KEY = stringPreferencesKey("favorite_driver_points")
        val FAVORITE_TEAM_KEY = stringPreferencesKey("favorite_team")
        val HAS_SEEN_WELCOME_KEY = booleanPreferencesKey("has_seen_welcome")
    }

    val userNameFlow: Flow<String> = context.userNameDataStore.data
        .map {
            it[USERNAME_KEY] ?: "Guest"
        }

    val hasSeenWelcomeFlow: Flow<Boolean> = context.userNameDataStore.data
        .map {
            it[HAS_SEEN_WELCOME_KEY] ?: false
        }

    val favoriteDriverNumberFlow: Flow<String?> = context.userNameDataStore.data
        .map { it[FAVORITE_DRIVER_NUMBER_KEY] }

    val favoriteDriverNameFlow: Flow<String?> = context.userNameDataStore.data
        .map { it[FAVORITE_DRIVER_NAME_KEY] }

    val favoriteDriverRankFlow: Flow<String?> = context.userNameDataStore.data
        .map { it[FAVORITE_DRIVER_RANK_KEY] }

    val favoriteDriverPointsFlow: Flow<String?> = context.userNameDataStore.data
        .map { it[FAVORITE_DRIVER_POINTS_KEY] }

    val favoriteTeamFlow: Flow<String?> = context.userNameDataStore.data
        .map { it[FAVORITE_TEAM_KEY] }

    suspend fun saveUserName(name: String) {
        context.userNameDataStore.edit {
            it[USERNAME_KEY] = name
        }
    }

    suspend fun saveFavoriteDriver(
        driverNumber: String?,
        driverName: String? = null,
        rank: String? = null,
        points: String? = null
    ) {
        context.userNameDataStore.edit { prefs ->
            if (driverNumber != null) {
                prefs[FAVORITE_DRIVER_NUMBER_KEY] = driverNumber
                if (driverName != null) prefs[FAVORITE_DRIVER_NAME_KEY] = driverName
                if (rank != null) prefs[FAVORITE_DRIVER_RANK_KEY] = rank
                if (points != null) prefs[FAVORITE_DRIVER_POINTS_KEY] = points
            } else {
                prefs.remove(FAVORITE_DRIVER_NUMBER_KEY)
                prefs.remove(FAVORITE_DRIVER_NAME_KEY)
                prefs.remove(FAVORITE_DRIVER_RANK_KEY)
                prefs.remove(FAVORITE_DRIVER_POINTS_KEY)
            }
        }
    }

    suspend fun updateFavoriteDriverStats(rank: String, points: String) {
        context.userNameDataStore.edit { prefs ->
            prefs[FAVORITE_DRIVER_RANK_KEY] = rank
            prefs[FAVORITE_DRIVER_POINTS_KEY] = points
        }
    }

    suspend fun saveFavoriteTeam(teamName: String?) {
        context.userNameDataStore.edit { prefs ->
            if (teamName != null) {
                prefs[FAVORITE_TEAM_KEY] = teamName
            } else {
                prefs.remove(FAVORITE_TEAM_KEY)
            }
        }
    }

    suspend fun setHasSeenWelcome(seen: Boolean = true) {
        context.userNameDataStore.edit { prefs ->
            prefs[HAS_SEEN_WELCOME_KEY] = seen
        }
    }
}