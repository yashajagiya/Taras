package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.api.NetworkModule
import com.example.taras.api.openf1.OpenF1Service
import com.example.taras.api.openf1.model.TeamStanding
import com.example.taras.api.taras.TarasDataService
import com.example.taras.api.taras.model.TeamsImageResponse
import com.example.taras.api.taras.model.TeamsPerRaceResponse
import com.example.taras.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamsViewModel : ViewModel() {
    private val logTag = "TeamsViewModel"


    //  private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)


    // private val _teams = MutableStateFlow<UiState<List<TeamStanding>>>(UiState.Loading)

    private val _teams = MutableStateFlow<UiState<TeamsPerRaceResponse>>(UiState.Loading)
    val teams = _teams.asStateFlow()

    private val _teamsImage = MutableStateFlow<UiState<List<TeamsImageResponse>>>(UiState.Loading)

    val teamsImage = _teamsImage.asStateFlow()

    init {
        fetchTeams()
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            _teams.value = UiState.Loading

            val teamsData = async(Dispatchers.IO) {
                tarasDataService.getTeamStandings()
            }
            val teamsImageData = async(Dispatchers.IO) {
                tarasDataService.getTeamsImage()
            }
            try {

                //    _teams.value = UiState.Success(openF1Service.getTeamStandings())


                _teams.value = UiState.Success(teamsData.await())

                _teamsImage.value = UiState.Success(teamsImageData.await())

            } catch (e: Exception) {
                Log.e(logTag, "Error fetching teams data", e)
                _teams.value = UiState.Error("Something went wrong")
                _teamsImage.value = UiState.Error("Something went wrong")
            }
        }
    }
}
