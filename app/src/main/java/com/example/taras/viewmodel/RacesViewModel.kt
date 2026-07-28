package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.getTodayDate
import com.example.taras.core.helpercore.toRemoveDateExtra
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.f1apidev.F1ApiDevService
import com.example.taras.network_calls.f1apidev.model.RaceData
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.RacesImageResponse
import com.example.taras.core.helpercore.formatCountdown
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.network_calls.f1apidev.model.Race
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Clock
import kotlin.time.Instant

@Stable
class RacesViewModel : ViewModel() {
    private val logTag = "RacesViewModel"

    private val racesDataService =
        NetworkModule.f1ApiDevRetrofit.create(F1ApiDevService::class.java)
    private val racesImageDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _races = MutableStateFlow<UiState<RaceData>>(UiState.Loading)
    val races = _races.asStateFlow()

    private val _racesImage =
        MutableStateFlow<UiState<ImmutableList<RacesImageResponse>>>(UiState.Loading)
    val racesImage = _racesImage.asStateFlow()

    init {
        fetchRacesData()
    }

    fun fetchRacesData() {
        viewModelScope.launch {
            _races.value = UiState.Loading
            _racesImage.value = UiState.Loading

            supervisorScope {
                launch(Dispatchers.IO) {
                    try {
                        val racesData = racesDataService.getCurrentCircuits()
                        _races.value = UiState.Success(racesData)
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching races data", e)
                        _races.value = UiState.Error("Races API: ${e.message}")
                    }
                }

                launch(Dispatchers.IO) {
                    try {
                        val racesImageData = racesImageDataService.getRacesImage().toImmutableList()
                        _racesImage.value = UiState.Success(racesImageData)
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching races images", e)
                        _racesImage.value = UiState.Error("Images API: ${e.message}")
                    }
                }
            }
        }
    }

    val combinedRaces = combine(_races, _racesImage) { racesState, racesImageState ->
        if (racesState is UiState.Success && racesImageState is UiState.Success) {
            val races = racesState.data.races
            val imagesMap = racesImageState.data.associateBy { it.circuitId }

            val combine = races.map { race ->
                val imageDetail = imagesMap[race.circuit.circuitId]

                RaceClearData(
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
                    fastestLapYear = race.circuit.fastestLapYear ?: 0,
                    driverId = race.winner?.driverId ?: "",
                    name = "${race.winner?.name ?: ""} ${race.winner?.surname ?: ""}".trim(),
                    number = race.winner?.number ?: 0,
                    race = SessionTime(race.schedule.race.date, race.schedule.race.time),
                    qualy = SessionTime(race.schedule.qualy.date, race.schedule.qualy.time),
                    fp1 = SessionTime(race.schedule.fp1.date, race.schedule.fp1.time),
                    fp2 = SessionTime(race.schedule.fp2.date, race.schedule.fp2.time),
                    fp3 = SessionTime(race.schedule.fp3.date, race.schedule.fp3.time),
                    sprintQualy = SessionTime(
                        race.schedule.sprintQualy.date,
                        race.schedule.sprintQualy.time
                    ),
                    sprintRace = SessionTime(
                        race.schedule.sprintRace.date,
                        race.schedule.sprintRace.time
                    ),
                    trackImage = imageDetail?.trackImage ?: "",
                    gpName = imageDetail?.gpName ?: ""
                )
            }
            UiState.Success(combine.toImmutableList())
        } else if (racesState is UiState.Error) {
            UiState.Error(racesState.message)
        } else if (racesImageState is UiState.Error) {
            UiState.Error(racesImageState.message)
        } else {
            UiState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(1000),
        UiState.Loading
    )

    val currentRaces = _races.map { racesState ->
        when (racesState) {
            is UiState.Success -> {
                val races = racesState.data.races
                val today = getTodayDate().toRemoveDateExtra()

                var upcomingRaces = races.mapNotNull { race ->
                    val raceDate = race.schedule.race.date?.toRemoveDateExtra() ?: 0
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
        SharingStarted.WhileSubscribed(1000),
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
        SharingStarted.WhileSubscribed(1000),
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
        SharingStarted.WhileSubscribed(1000),
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
                )
            }
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    private fun mapToCurrentRace(race: Race): CurrentRace {
        val parsedSessions = listOfNotNull(
            createParsedSession("FP1", race.schedule.fp1.date, race.schedule.fp1.time),
            createParsedSession("FP2", race.schedule.fp2.date, race.schedule.fp2.time),
            createParsedSession("FP3", race.schedule.fp3.date, race.schedule.fp3.time),
            createParsedSession("Qualifying", race.schedule.qualy.date, race.schedule.qualy.time),
            createParsedSession(
                "Sprint Qualifying",
                race.schedule.sprintQualy.date,
                race.schedule.sprintQualy.time
            ),
            createParsedSession(
                "Sprint Race",
                race.schedule.sprintRace.date,
                race.schedule.sprintRace.time
            ),
            createParsedSession("Race", race.schedule.race.date, race.schedule.race.time)
        )

        return CurrentRace(
            roundNumber = race.round,
            circuitId = race.circuit.circuitId,
            raceName = race.raceName,
            circuitName = race.circuit.circuitName,
            driverId = race.winner?.driverId ?: "",
            name = "${race.winner?.name ?: ""} ${race.winner?.surname ?: ""}".trim(),
            number = race.winner?.number ?: 0,
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
)

@Immutable
data class RaceClearData(
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
    val fastestLapYear: Int,
    val driverId: String,
    val name: String,
    val number: Int,
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
    val parsedSessions: ImmutableList<ParsedSession>
)