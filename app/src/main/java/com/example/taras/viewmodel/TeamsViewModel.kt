package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.TeamsImageResponse
import com.example.taras.network_calls.taras.model.TeamsPerRaceResponse
import com.example.taras.core.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.milliseconds

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

    val combinedTeams = combine(_teams, _teamsImage) { teamsState, imagesState ->
        if (teamsState is UiState.Success && imagesState is UiState.Success) {
            val teams = teamsState.data.entries
            val images = imagesState.data
            val combined = teams.map { teamEntry ->
                val imageDetail = images.find { it.teamName.equals(teamEntry.team, ignoreCase = true) }
                TeamUiModel(
                    teamName = teamEntry.team,
                    rank = teamEntry.rank,
                    points = teamEntry.points.displayValue,
                    teamColor = imageDetail?.teamColor,
                    teamLogo = imageDetail?.teamLogo,
                    teamCar = imageDetail?.teamCar
                )
            }
            UiState.Success(combined)
        } else if (teamsState is UiState.Error || imagesState is UiState.Error) {
            UiState.Error("Something went wrong")
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        fetchTeams()
    }

    fun fetchTeams() {
        viewModelScope.launch {
            _teams.value = UiState.Loading
            _teamsImage.value = UiState.Loading
            delay(2000.milliseconds)

            try {
                supervisorScope {
                    val teamsDataDeferred =
                        async(Dispatchers.IO) { tarasDataService.getTeamStandings() }
                    val teamsImageDataDeferred =
                        async(Dispatchers.IO) { tarasDataService.getTeamsImage() }

                    try {
                        _teams.value = UiState.Success(teamsDataDeferred.await())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching teams standings", e)
                        _teams.value = UiState.Error("Something went wrong")
                    }

                    try {
                        _teamsImage.value = UiState.Success(teamsImageDataDeferred.await())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching teams images", e)
                        _teamsImage.value = UiState.Error("Something went wrong")
                    }
                }
            } catch (e: Exception) {
                Log.e(logTag, "Error in fetchTeams", e)
                _teams.value = UiState.Error("Something went wrong")
                _teamsImage.value = UiState.Error("Something went wrong")
            }
        }
    }
}

data class TeamUiModel(
    val teamName: String,
    val rank: Int,
    val points: String,
    val teamColor: String?,
    val teamLogo: String?,
    val teamCar: String?
)
