package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.getTodayDate
import com.example.taras.core.helpercore.toRemoveDateExtra
import com.example.taras.core.helpercore.formatCountdown
import com.example.taras.core.helpercore.formatCountdownWidgets
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.F1RacesInfoResponse
import com.example.taras.network_calls.taras.model.RaceEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Clock
import kotlin.time.Instant

@Stable
class RacesViewModel(
    private val currentData: CurrentData
) : ViewModel() {
    private val logTag = "RacesViewModel"

    private val racesDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _races = MutableStateFlow<UiState<F1RacesInfoResponse>>(UiState.Loading)
    val races = _races.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchRacesData(isRefresh = false)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentData.racesData.firstOrNull()?.let { json ->
                    val cachedData = Json.decodeFromString<F1RacesInfoResponse>(json)
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
                val racesData = racesDataService.getRaceInfoData()
                _races.value = UiState.Success(racesData)
                try {
                    val json = Json.encodeToString(racesData)
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
                        val cachedData = Json.decodeFromString<F1RacesInfoResponse>(cachedJson)
                        _races.value = UiState.Success(cachedData)
                    } else if (_races.value !is UiState.Success) {
                        _races.value = UiState.Error("Races API: ${e.message}")
                    }
                } catch (cacheEx: Exception) {
                    Log.e(logTag, "Error loading races data from cache", cacheEx)
                    if (_races.value !is UiState.Success) {
                        _races.value = UiState.Error("Races API: ${e.message}")
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
                val races = racesState.data.races

                val combine = races.map { race ->
                    RaceClearData(
                        raceId = race.raceId,
                        roundNumber = race.round,
                        raceName = race.raceName,
                        laps = race.laps,
                        circuitId = race.circuit.circuitId,
                        circuitName = race.circuit.circuitName,
                        country = race.circuit.country,
                        city = race.circuit.city,
                        circuitLength = race.circuit.circuitLength,
                        lapRecord = race.circuit.lapRecord,
                        firstParticipationYear = race.circuit.firstParticipationYear,
                        corners = race.circuit.corners,
                        fastestLapDriverId = race.circuit.fastestLapDriverId,
                        fastestLapTeamId = race.circuit.fastestLapTeamId,
                        fastestLapYear = race.circuit.fastestLapYear,
                        winnerName = race.winner?.fullName ?: "",
                        winnerNumber = race.winner?.drivernumber ?: 0,
                        winnerTeam = race.winner?.teamWinner ?: "",
                        race = SessionTime(race.schedule.race.date, race.schedule.race.time),
                        qualy = SessionTime(race.schedule.qualy.date, race.schedule.qualy.time),
                        fp1 = SessionTime(race.schedule.fp1.date, race.schedule.fp1.time),
                        fp2 = SessionTime(race.schedule.fp2?.date, race.schedule.fp2?.time),
                        fp3 = SessionTime(race.schedule.fp3?.date, race.schedule.fp3?.time),
                        sprintQualy = SessionTime(
                            race.schedule.sprintQualy?.date,
                            race.schedule.sprintQualy?.time
                        ),
                        sprintRace = SessionTime(
                            race.schedule.sprintRace?.date,
                            race.schedule.sprintRace?.time
                        ),
                        trackImage = race.circuit.trackImage,
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
                val races = racesState.data.races
                val today = getTodayDate().toRemoveDateExtra()

                var upcomingRaces = races.mapNotNull { race ->
                    val raceDate = race.schedule.race.date.toRemoveDateExtra()
                    if (raceDate >= today) {
                        mapToCurrentRace(race)
                    } else null
                }

                if (upcomingRaces.isEmpty() && races.isNotEmpty()) {
                    upcomingRaces = listOf(mapToCurrentRace(races.last()))
                }
                UiState.Success(upcomingRaces)
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

    val nextSessionInfo = currentRaces.combine(flow {
        while (true) {
            emit(Clock.System.now())
            delay(1000.milliseconds)
        }
    }) { racesState, now ->
        if (racesState is UiState.Success) {
            var foundSession: SessionInfo? = null

            for (race in racesState.data) {
                val upcomingSession = race.parsedSessions
                    .filter { it.instant > now }
                    .minByOrNull { it.instant }

                if (upcomingSession != null) {
                    val duration = upcomingSession.instant - now
                    foundSession = SessionInfo(
                        roundNumber = race.roundNumber,
                        sessionName = upcomingSession.name,
                        countdown = formatCountdown(duration),
                        sessionTime = upcomingSession.instant.toString(),
                        circuitName = race.circuitName,
                        raceName = race.raceName
                    )
                    break
                }
            }
            foundSession
        } else null
    }.stateIn(
        viewModelScope,
        WhileSubscribed(1000),
        null
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

    private fun mapToCurrentRace(race: RaceEvent): CurrentRace {
        val parsedSessions = listOfNotNull(
            createParsedSession("FP1", race.schedule.fp1.date, race.schedule.fp1.time),
            createParsedSession("FP2", race.schedule.fp2?.date, race.schedule.fp2?.time),
            createParsedSession("FP3", race.schedule.fp3?.date, race.schedule.fp3?.time),
            createParsedSession(
                "Sprint Qualifying",
                race.schedule.sprintQualy?.date,
                race.schedule.sprintQualy?.time
            ),
            createParsedSession(
                "Sprint Race",
                race.schedule.sprintRace?.date,
                race.schedule.sprintRace?.time
            ),
            createParsedSession("Qualifying", race.schedule.qualy.date, race.schedule.qualy.time),
            createParsedSession("Race", race.schedule.race.date, race.schedule.race.time)
        )

        return CurrentRace(
            roundNumber = race.round,
            circuitId = race.circuit.circuitId,
            raceName = race.raceName,
            circuitName = race.circuit.circuitName,
            driverId = "",
            name = race.winner?.fullName ?: "",
            number = race.winner?.drivernumber ?: 0,
            winnerTeam = race.winner?.teamWinner ?: "",
            trackImage = race.circuit.trackImage,
            parsedSessions = parsedSessions.toImmutableList()
        )
    }

    private fun createParsedSession(name: String, date: String?, time: String?): ParsedSession? {
        val instant = parseSessionTimeToInstant(date, time) ?: return null
        return ParsedSession(name, instant)
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
    val countdown: String,
    val sessionTime: String,
    val circuitName: String,
    val raceName: String
)

@Immutable
data class ParsedSession(
    val name: String,
    val instant: Instant
)


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
    val firstParticipationYear: Int,
    val corners: Int,
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
