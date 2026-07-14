package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.api.NetworkModule
import com.example.taras.api.openf1.OpenF1Service
import com.example.taras.api.openf1.model.TeamStanding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamsViewModel : ViewModel() {
    private val logTag = "TeamsViewModel"
    private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)

    private val _teams = MutableStateFlow<List<TeamStanding>>(emptyList())
    val teams: StateFlow<List<TeamStanding>> = _teams.asStateFlow()

    init {
        fetchTeams()
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            try {
                _teams.value = openF1Service.getTeamStandings()
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching teams data", e)
            }
        }
    }
}
