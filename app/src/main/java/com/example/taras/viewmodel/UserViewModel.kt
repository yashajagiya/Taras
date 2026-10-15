package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.TarasApplication
import com.example.taras.core.common.UserPreferences
import com.example.taras.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = TarasApplication.instance.appContainer.userPreferencesRepository,
    private val userPreferences: UserPreferences = TarasApplication.instance.appContainer.userPreferences
) : ViewModel() {

    // Backwards-compatible constructor
    constructor(userPreferences: UserPreferences) : this(
        userPreferencesRepository = TarasApplication.instance.appContainer.userPreferencesRepository,
        userPreferences = userPreferences
    )
    val userName = userPreferences.userNameFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = "Guest"
    )

    val hasSeenWelcome = userPreferences.hasSeenWelcomeFlow
        .map<Boolean, Boolean?> { it }
        .onEach { seen ->
            if (seen != null) userPreferences.cacheHasSeenWelcome(seen)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = userPreferences.getCachedHasSeenWelcome()
        )

    val favoriteDriverNumber = userPreferences.favoriteDriverNumberFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteDriverName = userPreferences.favoriteDriverNameFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteDriverRank = userPreferences.favoriteDriverRankFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteDriverPoints = userPreferences.favoriteDriverPointsFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteDriverTeam = userPreferences.favoriteDriverTeamFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteTeam = userPreferences.favoriteTeamFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    val notificationsEnabled = userPreferences.notificationsEnabledFlow.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = true
    )

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.saveNotificationsEnabled(enabled)
        }
    }

    fun setHasSeenWelcome(seen: Boolean = true) {
        viewModelScope.launch {
            userPreferences.setHasSeenWelcome(seen)
        }
    }

    fun dismissWelcome() {
        setHasSeenWelcome(true)
    }

    fun resetOnboarding() {
        setHasSeenWelcome(false)
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(newName)
        }
    }

    fun setFavoriteDriver(
        driverNumber: String?,
        driverName: String? = null,
        rank: String? = null,
        points: String? = null,
        teamName: String? = null
    ) {
        viewModelScope.launch {
            userPreferences.saveFavoriteDriver(driverNumber, driverName, rank, points, teamName)
        }
    }

    fun setFavoriteTeam(teamName: String?) {
        viewModelScope.launch {
            userPreferences.saveFavoriteTeam(teamName)
        }
    }

    fun toggleFavoriteDriver(
        driverNumber: String,
        driverName: String = "",
        rank: String = "",
        points: String = "",
        teamName: String = ""
    ) {
        viewModelScope.launch {
            val isCurrentFavorite = favoriteDriverNumber.value == driverNumber
            if (isCurrentFavorite) {
                userPreferences.saveFavoriteDriver(null)
            } else {
                userPreferences.saveFavoriteDriver(driverNumber, driverName, rank, points, teamName)
            }
        }
    }

    fun toggleFavoriteTeam(teamName: String) {
        viewModelScope.launch {
            val isCurrentFavorite = favoriteTeam.value.equals(teamName, ignoreCase = true)
            if (isCurrentFavorite) {
                userPreferences.saveFavoriteTeam(null)
            } else {
                userPreferences.saveFavoriteTeam(teamName)
            }
        }
    }

    fun updateFavoriteDriverStats(driverNumber: String, rank: String, points: String, teamName: String? = null) {
        if (favoriteDriverNumber.value == driverNumber) {
            viewModelScope.launch {
                userPreferences.updateFavoriteDriverStats(rank, points, teamName)
            }
        }
    }
}

class UserViewModelFactory(private val userPreferences: UserPreferences? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return if (userPreferences != null) {
                UserViewModel(userPreferences) as T
            } else {
                UserViewModel() as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}