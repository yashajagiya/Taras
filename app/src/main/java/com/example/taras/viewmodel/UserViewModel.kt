package com.example.taras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel (private val userPreferences: UserPreferences) : ViewModel() {
    val userName = userPreferences.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Guest"
    )
    fun updateName(newName: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(newName)
        }
    }
}