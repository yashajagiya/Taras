package com.example.taras.data.repository

import com.example.taras.core.common.UserPreferences
import com.example.taras.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation of UserPreferencesRepository wrapping UserPreferences DataStore.
 */
class UserPreferencesRepositoryImpl(
    private val userPreferences: UserPreferences
) : UserPreferencesRepository {

    override val userNameFlow: Flow<String>
        get() = userPreferences.userNameFlow

    override val hasSeenWelcomeFlow: Flow<Boolean>
        get() = userPreferences.hasSeenWelcomeFlow

    override val notificationsEnabledFlow: Flow<Boolean>
        get() = userPreferences.notificationsEnabledFlow

    override val favoriteDriverNumberFlow: Flow<String?>
        get() = userPreferences.favoriteDriverNumberFlow

    override val favoriteDriverNameFlow: Flow<String?>
        get() = userPreferences.favoriteDriverNameFlow

    override val favoriteDriverRankFlow: Flow<String?>
        get() = userPreferences.favoriteDriverRankFlow

    override val favoriteDriverPointsFlow: Flow<String?>
        get() = userPreferences.favoriteDriverPointsFlow

    override val favoriteDriverTeamFlow: Flow<String?>
        get() = userPreferences.favoriteDriverTeamFlow

    override val favoriteTeamFlow: Flow<String?>
        get() = userPreferences.favoriteTeamFlow

    override fun getCachedHasSeenWelcome(): Boolean? {
        return userPreferences.getCachedHasSeenWelcome()
    }

    override fun cacheHasSeenWelcome(seen: Boolean) {
        userPreferences.cacheHasSeenWelcome(seen)
    }

    override suspend fun saveUserName(name: String) {
        userPreferences.saveUserName(name)
    }

    override suspend fun setHasSeenWelcome(seen: Boolean) {
        userPreferences.setHasSeenWelcome(seen)
    }

    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        userPreferences.saveNotificationsEnabled(enabled)
    }

    override suspend fun saveFavoriteDriver(
        driverNumber: String?,
        driverName: String?,
        rank: String?,
        points: String?,
        teamName: String?
    ) {
        userPreferences.saveFavoriteDriver(driverNumber, driverName, rank, points, teamName)
    }

    override suspend fun updateFavoriteDriverStats(
        rank: String,
        points: String,
        teamName: String?,
        triggerWidgetUpdate: Boolean
    ) {
        userPreferences.updateFavoriteDriverStats(rank, points, teamName, triggerWidgetUpdate)
    }

    override suspend fun saveFavoriteTeam(teamName: String?) {
        userPreferences.saveFavoriteTeam(teamName)
    }
}
