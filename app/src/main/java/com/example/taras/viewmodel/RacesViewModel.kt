package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.SessionType
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.engine.RaceStateEngine
import com.example.taras.core.engine.RaceWeekendState
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import kotlin.time.Instant

@Stable
class RacesViewModel(
    private val currentData: CurrentData
) : ViewModel() {
    private val logTag = "RacesViewModel"

    private val racesDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _races = MutableStateFlow<UiState<List<CalendarRaceEvent>>>(UiState.Loading)
    val races = _races.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchRacesData(isRefresh = false)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentData.racesData.firstOrNull()?.let { json ->
                    val cachedData = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(json)
                    if (_races.value is UiState.Loading) {
                        _races.value = UiState.Success(cachedData)
                    }
                }
            } catch (e: Exception) {
                Log.e(logTag, "Error loading initial races cache", e)
            }
        }
    }

    fun fetchRacesData(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (_races.value !is UiState.Success) {
                _races.value = UiState.Loading
            }

            try {
                val calendarData = racesDataService.getCalendar()
                _races.value = UiState.Success(calendarData)
                try {
                    val json = NetworkModule.json.encodeToString(calendarData)
                    currentData.saveRacesData(json)
                } catch (e: Exception) {
                    Log.e(logTag, "Error saving races data to cache", e)
                }
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching races data", e)

                // Fallback to cache if not already loaded OR if it's a refresh failure
                try {
                    val cachedJson = currentData.racesData.first()
                    if (cachedJson != null) {
                        val cachedData = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(cachedJson)
                        _races.value = UiState.Success(cachedData)
                    } else if (_races.value !is UiState.Success) {
                        _races.value = UiState.Error(e.toAppError())
                    }
                } catch (cacheEx: Exception) {
                    Log.e(logTag, "Error loading races data from cache", cacheEx)
                    if (_races.value !is UiState.Success) {
                        _races.value = UiState.Error(e.toAppError())
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    val combinedRaces = _races.map { racesState ->
        when (racesState) {
            is UiState.Success -> {
                val races = racesState.data

                val combine = races.map { race ->
                    val qualySession = race.schedule.qualifying ?: race.schedule.qualy
                    RaceClearData(
                        raceId = race.id,
                        roundNumber = race.round,
                        raceName = race.name,
                        laps = race.laps,
                        circuitId = race.circuit.id.ifBlank { race.id },
                        circuitName = race.circuit.name,
                        country = race.circuit.country,
                        city = race.circuit.city,
                        circuitLength = race.circuit.length,
                        lapRecord = race.circuit.lapRecord,
                        firstParticipationYear = race.circuit.firstParticipationYear,
                        corners = race.circuit.corners,
                        fastestLapDriverId = race.circuit.fastestLapDriver,
                        fastestLapTeamId = race.circuit.fastestLapTeam,
                        fastestLapYear = race.circuit.fastestLapYear,
                        winnerName = race.winner?.fullName ?: "",
                        winnerNumber = race.winner?.drivernumber ?: 0,
                        winnerTeam = race.winner?.teamWinner ?: "",
                        race = SessionTime(race.schedule.race?.date, race.schedule.race?.time),
                        qualy = SessionTime(qualySession?.date, qualySession?.time),
                        fp1 = SessionTime(race.schedule.fp1?.date, race.schedule.fp1?.time),
                        fp2 = SessionTime(race.schedule.fp2?.date, race.schedule.fp2?.time),
                        fp3 = SessionTime(race.schedule.fp3?.date, race.schedule.fp3?.time),
                        sprintQualy = SessionTime(
                            race.schedule.sprintQualifying?.date,
                            race.schedule.sprintQualifying?.time
                        ),
                        sprintRace = SessionTime(
                            race.schedule.sprintRace?.date,
                            race.schedule.sprintRace?.time
                        ),
                        trackImage = race.circuit.trackImage.orEmpty(),
                        gpName = race.circuit.gpName
                    )
                }
                UiState.Success(combine.toImmutableList())
            }

            is UiState.Error -> {
                UiState.Error(racesState.message)
            }

            else -> {
                UiState.Loading
            }
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(1000),
        UiState.Loading
    )

    val currentRaces = _races.map { racesState ->
        when (racesState) {
            is UiState.Success -> {
                val upcomingRaces = RaceStateEngine.findCurrentOrUpcomingRaces(racesState.data)
                UiState.Success(upcomingRaces.toImmutableList())
            }

            is UiState.Error -> UiState.Error(racesState.message)
            else -> UiState.Loading
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(1000),
        UiState.Loading
    )

    val oneRace = currentRaces.map { state ->
        when (state) {
            is UiState.Success -> UiState.Success(state.data.firstOrNull())
            is UiState.Error -> UiState.Error(state.message)
            else -> UiState.Loading
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(1000),
        UiState.Loading
    )

    val nextSessionInfo = currentRaces.map { racesState ->
        if (racesState is UiState.Success) {
            RaceStateEngine.findNextSessionInfo(racesState.data)
        } else null
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000),
        null
    )

    val raceWeekendState = oneRace.map { state ->
        when (state) {
            is UiState.Success -> state.data?.let { RaceStateEngine.determineWeekendState(it) } ?: RaceWeekendState.OffSeason
            else -> RaceWeekendState.OffSeason
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000),
        RaceWeekendState.OffSeason
    )

    val upcomingRoundInfo = nextSessionInfo
        .map { session ->
            if (session == null) {
                null
            } else {
                CurrentRound(
                    roundNumber = session.roundNumber,
                    sessionName = session.sessionName,
                    sessionTime = session.sessionTime
                )
            }
        }
        .distinctUntilChanged()
        .onEach { round ->
            round?.let {
                saveSessionStatus(it.sessionName, it.sessionTime)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(1000),
            initialValue = null
        )

    private fun saveSessionStatus(name: String, time: String) {
        viewModelScope.launch {
            currentData.saveCurrentSessionStatus(name, time)
        }
    }
}

@Immutable
data class SessionTime(
    val date: String?,
    val time: String?
)

@Immutable
data class SessionInfo(
    val roundNumber: Int,
    val sessionName: String,
    val countdown: String = "00:00:00",
    val sessionTime: String,
    val targetInstant: Instant? = null,
    val circuitName: String,
    val raceName: String
)

@Immutable
data class ParsedSession(
    val sessionType: SessionType,
    val instant: Instant
) {
    val name: String get() = sessionType.displayName
}

@Immutable
data class CurrentRound(
    val roundNumber: Int,
    val sessionName: String,
    val sessionTime: String
)

@Immutable
data class RaceClearData(
    val raceId: String,
    val roundNumber: Int,
    val raceName: String,
    val laps: Int?,
    val circuitId: String,
    val circuitName: String,
    val country: String,
    val city: String,
    val circuitLength: String,
    val lapRecord: String?,
    val firstParticipationYear: Int?,
    val corners: Int?,
    val fastestLapDriverId: String?,
    val fastestLapTeamId: String?,
    val fastestLapYear: Int?,
    val winnerName: String,
    val winnerNumber: Int,
    val winnerTeam: String,
    val race: SessionTime,
    val qualy: SessionTime,
    val fp1: SessionTime,
    val fp2: SessionTime,
    val fp3: SessionTime,
    val sprintQualy: SessionTime,
    val sprintRace: SessionTime,
    val trackImage: String,
    val gpName: String
)

@Immutable
data class CurrentRace(
    val roundNumber: Int,
    val circuitId: String,
    val raceName: String,
    val circuitName: String,
    val driverId: String,
    val name: String,
    val number: Int,
    val winnerTeam: String,
    val trackImage: String,
    val parsedSessions: ImmutableList<ParsedSession>
)

class RacesViewModelFactory(private val currentData: CurrentData) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RacesViewModel(currentData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
