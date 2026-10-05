package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.AppError
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.repository.F1InfoRepository
import com.example.taras.network_calls.taras.model.Racedata
import com.example.taras.network_calls.taras.model.SeasonStats
import com.example.taras.network_calls.taras.model.TeamSummary
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class TeamsViewModel(
    private val f1InfoRepository: F1InfoRepository = F1InfoRepository.instance
) : ViewModel() {
    private val logTag = "TeamsViewModel"

    private val _teamsList =
        MutableStateFlow<UiState<List<TeamDetailResponseV2>>>(UiState.Loading)
    val teamsList = _teamsList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val combinedTeams = _teamsList.map { state ->
        when (state) {
            is UiState.Success -> {
                val combined = state.data.map { team ->
                    TeamUiModel(
                        teamName = team.name,
                        rank = team.standings.rank,
                        points = team.standings.points.toString(),
                        teamColor = team.colors.colorHex,
                        teamLogo = team.images.logo,
                        teamCar = team.images.car
                    )
                }.toImmutableList()
                UiState.Success(combined)
            }
            is UiState.Error -> UiState.Error(state.appError ?: AppError.Unknown(state.message))
            UiState.Loading -> UiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val combinedDetailedTeams = _teamsList.map { state ->
        when (state) {
            is UiState.Success -> {
                val combinedList = state.data.map { team ->
                    DetailedTeamUiModel(
                        rank = team.standings.rank,
                        teamName = team.name,
                        currentPoints = team.standings.points,
                        currentPointsDisplay = team.standings.points.toString(),
                        races = team.standings.races.map { r ->
                            Racedata(
                                name = r.raceName,
                                displayName = r.raceCode,
                                played = r.played,
                                value = r.points,
                                displayValue = r.displayValue
                            )
                        }.toImmutableList(),

                        slug = team.id,
                        url = team.f1Url,
                        teamColor = team.colors.colorHex.orEmpty(),
                        accessibleColor = team.colors.accessibleColor.orEmpty(),
                        teamCarUrl = team.images.car,
                        teamLogoUrl = team.images.logo,

                        biography = team.biography,
                        fullTeamName = team.fullTeamName.ifBlank { team.name },
                        baseLocation = team.base,
                        teamChief = team.teamChief,
                        technicalChief = team.technicalChief,
                        chassis = team.chassis,
                        powerUnit = team.powerUnit,
                        reserveDriver = team.reserveDriver,
                        firstTeamEntryYear = team.firstTeamEntry,
                        seasonStats = team.seasonStats,
                        teamSummary = team.teamSummary
                    )
                }.toImmutableList()
                UiState.Success(combinedList)
            }
            is UiState.Error -> UiState.Error(state.appError ?: AppError.Unknown(state.message))
            UiState.Loading -> UiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    init {
        fetchTeams(isRefresh = false)
    }

    fun fetchTeams(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_teamsList.value !is UiState.Success) {
                _teamsList.value = UiState.Loading
            }

            try {
                val teams = f1InfoRepository.getTeamsV2(forceRefresh = isRefresh)
                _teamsList.value = UiState.Success(teams)
            } catch (e: Exception) {
                Log.e(logTag, "Error in fetchTeams", e)
                _teamsList.value = UiState.Error(e.toAppError())
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

@Immutable
data class TeamUiModel(
    val teamName: String,
    val rank: Int,
    val points: String,
    val teamColor: String?,
    val teamLogo: String?,
    val teamCar: String?
)

@Immutable
data class DetailedTeamUiModel(
    val rank: Int,
    val teamName: String,
    val currentPoints: Int,
    val currentPointsDisplay: String,
    val races: ImmutableList<Racedata>,

    val slug: String,
    val url: String,
    val teamColor: String,
    val accessibleColor: String,
    val teamCarUrl: String?,
    val teamLogoUrl: String?,

    val biography: String,
    val fullTeamName: String,
    val baseLocation: String,
    val teamChief: String,
    val technicalChief: String,
    val chassis: String,
    val powerUnit: String,
    val reserveDriver: String,
    val firstTeamEntryYear: String,

    val seasonStats: SeasonStats?,
    val teamSummary: TeamSummary?
)