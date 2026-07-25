package com.example.taras.viewmodel

import com.example.taras.network_calls.f1apidev.model.RaceData
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.getTodayDate
import com.example.taras.core.helpercore.toRemoveDateExtra
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.f1apidev.F1ApiDevService
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.RacesImageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import com.example.taras.core.helpercore.formatCountdown
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Clock
import kotlin.time.Instant

class RacesViewModel : ViewModel() {
    private val logTag = "RacesViewModel"

    private val racesDataService =
        NetworkModule.f1ApiDevRetrofit.create(F1ApiDevService::class.java)

    private val racesImageDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _races = MutableStateFlow<UiState<RaceData>>(UiState.Loading)
    val races = _races

    private val _racesImage = MutableStateFlow<UiState<List<RacesImageResponse>>>(UiState.Loading)
    val racesImage = _racesImage

    init {
        fetchRacesData()
    }

    val combinedRaces = combine(_races, _racesImage) { racesState, racesImageState ->
        if (racesState is UiState.Success && racesImageState is UiState.Success) {
            val races = racesState.data.races
            val racesImage = racesImageState.data
            val combine = races.map { race ->
                val imageDetail = racesImage.find { it.circuitId == race.circuit.circuitId }
                RaceClearData(
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
                    race = RaceTime(race.schedule.race.date, race.schedule.race.time),
                    qualy = QualyTime(race.schedule.qualy.date, race.schedule.qualy.time),
                    fp1 = Fp1Time(race.schedule.fp1.date, race.schedule.fp1.time),
                    fp2 = Fp2Time(race.schedule.fp2.date, race.schedule.fp2.time),
                    fp3 = Fp3Time(race.schedule.fp3.date, race.schedule.fp3.time),
                    sprintQualy = SprintQualyTime(
                        race.schedule.sprintQualy.date,
                        race.schedule.sprintQualy.time
                    ),
                    sprintRace = SprintRaceTime(
                        race.schedule.sprintRace.date,
                        race.schedule.sprintRace.time
                    ),
                    trackImage = imageDetail?.trackImage ?: ""
                )
            }
            UiState.Success(combine)
        } else if (racesState is UiState.Error) {
            UiState.Error(racesState.message)
        } else if (racesImageState is UiState.Error) {
            UiState.Error(racesImageState.message)
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(1000), UiState.Loading)

    fun fetchRacesData() {
        viewModelScope.launch {
            _races.value = UiState.Loading
            _racesImage.value = UiState.Loading

            supervisorScope {
                launch(Dispatchers.IO) {
                    try {
                        val racesData = racesDataService.getCurrentCircuits()
                        Log.d(logTag, "Fetched ${racesData.races.size} races")
                        _races.value = UiState.Success(racesData)
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching races data", e)
                        _races.value = UiState.Error("Races API: ${e.message}")
                    }
                }

                launch(Dispatchers.IO) {
                    try {
                        val racesImageData = racesImageDataService.getRacesImage()
                        Log.d(logTag, "Fetched ${racesImageData.size} images")
                        _racesImage.value = UiState.Success(racesImageData)
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching races images", e)
                        _racesImage.value = UiState.Error("Images API: ${e.message}")
                    }
                }
            }
        }
    }

    val currentRces = _races.map { racesState ->
        if (racesState is UiState.Success) {
            val races = racesState.data.races
            val today = getTodayDate().toRemoveDateExtra()
            var upcomingRaces = races.mapNotNull { race ->
                val raceDate = race.schedule.race.date?.toRemoveDateExtra() ?: 0
                if (raceDate >= today) {
                    CurrentRace(
                        circuitId = race.circuit.circuitId,
                        raceName = race.raceName,
                        race = RaceTime(race.schedule.race.date, race.schedule.race.time),
                        qualy = QualyTime(race.schedule.qualy.date, race.schedule.qualy.time),
                        fp1 = Fp1Time(race.schedule.fp1.date, race.schedule.fp1.time),
                        fp2 = Fp2Time(race.schedule.fp2.date, race.schedule.fp2.time),
                        fp3 = Fp3Time(race.schedule.fp3.date, race.schedule.fp3.time),
                        sprintQualy = SprintQualyTime(
                            race.schedule.sprintQualy.date,
                            race.schedule.sprintQualy.time
                        ),
                        sprintRace = SprintRaceTime(
                            race.schedule.sprintRace.date,
                            race.schedule.sprintRace.time
                        ),
                        circuitName = race.circuit.circuitName,
                        driverId = race.winner?.driverId ?: "",
                        name = "${race.winner?.name ?: ""} ${race.winner?.surname ?: ""}".trim(),
                        number = race.winner?.number ?: 0,
                    )
                } else null
            }

            if (upcomingRaces.isEmpty() && races.isNotEmpty()) {
                val lastRace = races.last()
                upcomingRaces = listOf(
                    CurrentRace(
                        circuitId = lastRace.circuit.circuitId,
                        raceName = lastRace.raceName,
                        race = RaceTime(lastRace.schedule.race.date, lastRace.schedule.race.time),
                        qualy = QualyTime(
                            lastRace.schedule.qualy.date,
                            lastRace.schedule.qualy.time
                        ),
                        fp1 = Fp1Time(lastRace.schedule.fp1.date, lastRace.schedule.fp1.time),
                        fp2 = Fp2Time(lastRace.schedule.fp2.date, lastRace.schedule.fp2.time),
                        fp3 = Fp3Time(lastRace.schedule.fp3.date, lastRace.schedule.fp3.time),
                        sprintQualy = SprintQualyTime(
                            lastRace.schedule.sprintQualy.date,
                            lastRace.schedule.sprintQualy.time
                        ),
                        sprintRace = SprintRaceTime(
                            lastRace.schedule.sprintRace.date,
                            lastRace.schedule.sprintRace.time
                        ),
                        circuitName = lastRace.circuit.circuitName,
                        driverId = lastRace.winner?.driverId ?: "",
                        name = "${lastRace.winner?.name ?: ""} ${lastRace.winner?.surname ?: ""}".trim(),
                        number = lastRace.winner?.number ?: 0,
                    )
                )
            }
            UiState.Success(upcomingRaces)
        } else if (racesState is UiState.Error) UiState.Error(racesState.message)
        else UiState.Loading
    }.stateIn(viewModelScope, WhileSubscribed(1000), UiState.Loading)

    val oneRace = currentRces.map { state ->
        if (state is UiState.Success) UiState.Success(state.data.firstOrNull())
        else if (state is UiState.Error) UiState.Error(state.message)
        else UiState.Loading
    }.stateIn(viewModelScope, WhileSubscribed(1000), UiState.Loading)

    val nextSessionInfo = currentRces.combine(flow {
        while (true) {
            emit(Clock.System.now())
            delay(1000.milliseconds)
        }
    }) { racesState: UiState<List<CurrentRace>>, now: Instant ->
        if (racesState is UiState.Success) {
            val races = racesState.data
            var foundSession: SessionInfo? = null

            for (race in races) {
                val sessions = listOf(
                    "FP1" to (race.fp1.date to race.fp1.time),
                    "FP2" to (race.fp2.date to race.fp2.time),
                    "FP3" to (race.fp3.date to race.fp3.time),
                    "Qualifying" to (race.qualy.date to race.qualy.time),
                    "Sprint Qualifying" to (race.sprintQualy.date to race.sprintQualy.time),
                    "Sprint Race" to (race.sprintRace.date to race.sprintRace.time),
                    "Race" to (race.race.date to race.race.time)
                )

                val upcomingSession = sessions.mapNotNull { (name, timeData) ->
                    val instant = parseSessionTimeToInstant(timeData.first, timeData.second)
                    if (instant != null && instant > now) name to instant
                    else null
                }.minByOrNull { it.second }

                if (upcomingSession != null) {
                    val duration = upcomingSession.second - now
                    foundSession = SessionInfo(
                        sessionName = upcomingSession.first,
                        countdown = formatCountdown(duration),
                        circuitName = race.circuitName,
                        raceName = race.raceName
                    )
                    break
                }
            }
            foundSession
        } else null
    }.stateIn(viewModelScope, WhileSubscribed(1000), null)
}

data class SessionInfo(
    val sessionName: String,
    val countdown: String,
    val circuitName: String,
    val raceName: String
)

data class RaceClearData(
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
    val race: RaceTime,
    val qualy: QualyTime,
    val fp1: Fp1Time,
    val fp2: Fp2Time,
    val fp3: Fp3Time,
    val sprintQualy: SprintQualyTime,
    val sprintRace: SprintRaceTime,
    val trackImage: String
)

data class RaceTime(val date: String?, val time: String?)
data class QualyTime(val date: String?, val time: String?)
data class Fp1Time(val date: String?, val time: String?)
data class Fp2Time(val date: String?, val time: String?)
data class Fp3Time(val date: String?, val time: String?)
data class SprintQualyTime(val date: String?, val time: String?)
data class SprintRaceTime(val date: String?, val time: String?)

data class CurrentRace(
    val circuitId: String,
    val raceName: String,
    val race: RaceTime,
    val qualy: QualyTime,
    val fp1: Fp1Time,
    val fp2: Fp2Time,
    val fp3: Fp3Time,
    val sprintQualy: SprintQualyTime,
    val sprintRace: SprintRaceTime,
    val circuitName: String,
    val driverId: String,
    val name: String,
    val number: Int
)
