package com.example.taras.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture repository contract for User preferences and favorite entity stats.
 */
interface UserPreferencesRepository {
    val userNameFlow: Flow<String>
    val hasSeenWelcomeFlow: Flow<Boolean>
    val notificationsEnabledFlow: Flow<Boolean>
    val favoriteDriverNumberFlow: Flow<String?>
    val favoriteDriverNameFlow: Flow<String?>
    val favoriteDriverRankFlow: Flow<String?>
    val favoriteDriverPointsFlow: Flow<String?>
    val favoriteDriverTeamFlow: Flow<String?>
    val favoriteTeamFlow: Flow<String?>

    fun getCachedHasSeenWelcome(): Boolean?
    fun cacheHasSeenWelcome(seen: Boolean)
    suspend fun saveUserName(name: String)
    suspend fun setHasSeenWelcome(seen: Boolean = true)
    suspend fun saveNotificationsEnabled(enabled: Boolean)
    suspend fun saveFavoriteDriver(
        driverNumber: String?,
        driverName: String? = null,
        rank: String? = null,
        points: String? = null,
        teamName: String? = null
    )
    suspend fun updateFavoriteDriverStats(
        rank: String,
        points: String,
        teamName: String? = null,
        triggerWidgetUpdate: Boolean = true
    )
    suspend fun saveFavoriteTeam(teamName: String?)
}
