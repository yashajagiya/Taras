package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel (private val userPreferences: UserPreferences) : ViewModel() {
    val userName = userPreferences.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Guest"
    )

    val hasSeenWelcome = userPreferences.hasSeenWelcomeFlow
        .map<Boolean, Boolean?> { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val favoriteDriverNumber = userPreferences.favoriteDriverNumberFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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

    fun setHasSeenWelcome(seen: Boolean = true) {
        viewModelScope.launch {
            userPreferences.setHasSeenWelcome(seen)
        }
    }

    fun dismissWelcome() {
        setHasSeenWelcome(true)
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(newName)
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

class UserViewModelFactory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}