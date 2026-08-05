package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.DriverRaceQualifyingResponse
import com.example.taras.network_calls.taras.model.DriverRaceResultResponse
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.network_calls.taras.model.Fp1Response
import com.example.taras.network_calls.taras.model.Fp2Response
import com.example.taras.network_calls.taras.model.Fp3Response
import com.example.taras.network_calls.taras.model.SprintQulyResponse
import SprintResultResponse
import com.example.taras.core.helpercore.getTodayDate
import com.example.taras.core.helpercore.toRemoveDateExtra
import com.example.taras.core.helpercore.formatToLocalFull
import com.example.taras.core.helpercore.getCurrentMoment
import com.example.taras.core.helpercore.parseSessionTimeToInstant
import com.example.taras.network_calls.taras.model.Schedule
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class ResultRowData(
    val position: String,
    val number: String,
    val driver: String,
    val team: String,
    val extra: String,
    val laps: String,
    val points: String? = null,
    val headshotUrl: String? = null
)

@Immutable
data class ResultHeader(
    val raceName: String,
    val circuitName: String
)

@Immutable
data class SessionResultUiState(
    val header: ResultHeader? = null,
    val results: ImmutableList<ResultRowData> = persistentListOf(),
    val isStarted: Boolean = true,
    val scheduledTime: String? = null
)

@Stable
class ResultViewModel : ViewModel() {

    private val lagtag = "ResultViewModel"

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _racesResult = MutableStateFlow<UiState<DriverRaceResultResponse>>(UiState.Loading)
    private val _racesQulyResult =
        MutableStateFlow<UiState<DriverRaceQualifyingResponse>>(UiState.Loading)
    private val _racesFp3Result = MutableStateFlow<UiState<Fp3Response>>(UiState.Loading)
    private val _racesFp2Result = MutableStateFlow<UiState<Fp2Response>>(UiState.Loading)
    private val _racesFp1Result = MutableStateFlow<UiState<Fp1Response>>(UiState.Loading)
    private val _racesSprintQualyResult =
        MutableStateFlow<UiState<SprintQulyResponse>>(UiState.Loading)
    private val _racesSprintRaceResult =
        MutableStateFlow<UiState<SprintResultResponse>>(UiState.Loading)
    private val _isSprintWeekend = MutableStateFlow(false)
    val isSprintWeekend = _isSprintWeekend.asStateFlow()

    private val _driversInfoData =
        MutableStateFlow<UiState<List<F1DriversInfoResponse>>>(UiState.Loading)
    private val _activeSchedule = MutableStateFlow<Schedule?>(null)

    val fp1Results = combine(
        _racesFp1Result,
        _driversInfoData,
        _activeSchedule
    ) { sessionState, detailsState, schedule ->
        val isStarted = schedule?.fp1?.let { it ->
            parseSessionTimeToInstant(
                it.date,
                it.time
            )?.let { it < getCurrentMoment() }
        } ?: true
        val timeStr = schedule?.fp1?.let { formatToLocalFull(it.date, it.time) }

        if (!isStarted) {
            UiState.Success(SessionResultUiState(isStarted = false, scheduledTime = timeStr))
        } else if (sessionState is UiState.Loading || detailsState is UiState.Loading) {
            UiState.Loading
        } else if (sessionState is UiState.Error) {
            UiState.Error(sessionState.message)
        } else if (sessionState is UiState.Success) {
            val data = sessionState.data
            val results = data.results.map { res ->
                val info = findDriverInfo(res.driver, detailsState)
                ResultRowData(
                    position = res.position,
                    number = res.number,
                    driver = res.driver,
                    team = res.team,
                    extra = res.timeOrGap,
                    laps = res.laps,
                    headshotUrl = info?.hero?.driverImage
                )
            }.toImmutableList()
            UiState.Success(
                SessionResultUiState(
                    ResultHeader(data.raceName, data.circuitName),
                    results
                )
            )
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp2Results = combine(
        _racesFp2Result,
        _racesSprintQualyResult,
        _driversInfoData,
        isSprintWeekend,
        _activeSchedule
    ) { fp2State: UiState<Fp2Response>, sqState: UiState<SprintQulyResponse>, detailsState: UiState<List<F1DriversInfoResponse>>, isSprint: Boolean, schedule: Schedule? ->
        val sessionData = if (isSprint) sqState else fp2State
        val sessionSchedule = if (isSprint) schedule?.sprintQualy else schedule?.fp2
        val isStarted = sessionSchedule?.let { it ->
            parseSessionTimeToInstant(
                it.date,
                it.time
            )?.let { it < getCurrentMoment() }
        } ?: true
        val timeStr = sessionSchedule?.let { formatToLocalFull(it.date, it.time) }

        if (!isStarted) {
            UiState.Success(SessionResultUiState(isStarted = false, scheduledTime = timeStr))
        } else if (sessionData is UiState.Loading || detailsState is UiState.Loading) {
            UiState.Loading
        } else if (sessionData is UiState.Error) {
            UiState.Error(sessionData.message)
        } else if (sessionData is UiState.Success) {
            val data = sessionData.data
            val results: ImmutableList<ResultRowData> =
                if (isSprint && data is SprintQulyResponse) {
                    data.results.map { res ->
                        val info = findDriverInfo(res.driverName, detailsState)
                        ResultRowData(
                            position = res.position,
                            number = res.driverNumber,
                            driver = res.driverName,
                            team = res.team,
                            extra = res.q3.ifBlank { res.q2.ifBlank { res.q1 } },
                            laps = res.laps,
                            headshotUrl = info?.hero?.driverImage
                        )
                    }.toImmutableList()
                } else if (!isSprint && data is Fp2Response) {
                    data.results.map { res ->
                        val info = findDriverInfo(res.driver, detailsState)
                        ResultRowData(
                            position = res.position,
                            number = res.number,
                            driver = res.driver,
                            team = res.team,
                            extra = res.timeOrGap,
                            laps = res.laps,
                            headshotUrl = info?.hero?.driverImage
                        )
                    }.toImmutableList()
                } else {
                   persistentListOf()
                }

            val header = when (data) {
                is SprintQulyResponse -> ResultHeader(data.raceName, data.circuitName)
                is Fp2Response -> ResultHeader(data.raceName, data.circuitName)
                else -> null
            }
            UiState.Success(SessionResultUiState(header, results))
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp3Results = combine(
        _racesFp3Result,
        _racesSprintRaceResult,
        _driversInfoData,
        isSprintWeekend,
        _activeSchedule
    ) { fp3State: UiState<Fp3Response>, srState: UiState<SprintResultResponse>, detailsState: UiState<List<F1DriversInfoResponse>>, isSprint: Boolean, schedule: Schedule? ->
        val sessionData = if (isSprint) srState else fp3State
        val sessionSchedule = if (isSprint) schedule?.sprintRace else schedule?.fp3
        val isStarted = sessionSchedule?.let { it ->
            parseSessionTimeToInstant(
                it.date,
                it.time
            )?.let { it < getCurrentMoment() }
        } ?: true
        val timeStr = sessionSchedule?.let { formatToLocalFull(it.date, it.time) }

        if (!isStarted) {
            UiState.Success(SessionResultUiState(isStarted = false, scheduledTime = timeStr))
        } else if (sessionData is UiState.Loading || detailsState is UiState.Loading) {
            UiState.Loading
        } else if (sessionData is UiState.Error) {
            UiState.Error(sessionData.message)
        } else if (sessionData is UiState.Success) {
            val data = sessionData.data
            val results: ImmutableList<ResultRowData> =
                if (isSprint && data is SprintResultResponse) {
                    data.results.map { res ->
                        val info = findDriverInfo(res.driverName, detailsState)
                        ResultRowData(
                            position = res.position,
                            number = res.driverNumber,
                            driver = res.driverName,
                            team = res.team,
                            extra = res.timeOrRetired,
                            laps = res.laps,
                            points = res.points,
                            headshotUrl = info?.hero?.driverImage
                        )
                    }.toImmutableList()
                } else if (!isSprint && data is Fp3Response) {
                    data.results.map { res ->
                        val info = findDriverInfo(res.driver, detailsState)
                        ResultRowData(
                            position = res.position,
                            number = res.number,
                            driver = res.driver,
                            team = res.team,
                            extra = res.timeOrGap,
                            laps = res.laps,
                            headshotUrl = info?.hero?.driverImage
                        )
                    }.toImmutableList()
                } else {
                    persistentListOf()
                }

            val header = when (data) {
                is SprintResultResponse -> ResultHeader(data.raceName, data.circuitName)
                is Fp3Response -> ResultHeader(data.raceName, data.circuitName)
                else -> null
            }
            UiState.Success(SessionResultUiState(header, results))
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val qualifyResults = combine(
        _racesQulyResult,
        _driversInfoData,
        _activeSchedule
    ) { sessionState, detailsState, schedule ->
        val isStarted = schedule?.qualy?.let {
            parseSessionTimeToInstant(
                it.date,
                it.time
            )?.let { it < getCurrentMoment() }
        } ?: true
        val timeStr = schedule?.qualy?.let { formatToLocalFull(it.date, it.time) }

        if (!isStarted) {
            UiState.Success(SessionResultUiState(isStarted = false, scheduledTime = timeStr))
        } else if (sessionState is UiState.Loading || detailsState is UiState.Loading) {
            UiState.Loading
        } else if (sessionState is UiState.Error) {
            UiState.Error(sessionState.message)
        } else if (sessionState is UiState.Success) {
            val data = sessionState.data
            val results = data.results.map { res ->
                val info = findDriverInfo(res.driverName, detailsState)
                ResultRowData(
                    position = res.position,
                    number = res.driverNumber,
                    driver = res.driverName,
                    team = res.team,
                    extra = res.q3.ifBlank { res.q2.ifBlank { res.q1 } },
                    laps = res.laps,
                    headshotUrl = info?.hero?.driverImage
                )
            }.toImmutableList()
            UiState.Success(
                SessionResultUiState(
                    ResultHeader(data.raceName, data.circuitName),
                    results
                )
            )
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val raceResults = combine(
        _racesResult,
        _driversInfoData,
        _activeSchedule
    ) { sessionState, detailsState, schedule ->
        val isStarted = schedule?.race?.let {
            parseSessionTimeToInstant(
                it.date,
                it.time
            )?.let { it < getCurrentMoment() }
        } ?: true
        val timeStr = schedule?.race?.let { formatToLocalFull(it.date, it.time) }

        if (!isStarted) {
            UiState.Success(SessionResultUiState(isStarted = false, scheduledTime = timeStr))
        } else if (sessionState is UiState.Loading || detailsState is UiState.Loading) {
            UiState.Loading
        } else if (sessionState is UiState.Error) {
            UiState.Error(sessionState.message)
        } else if (sessionState is UiState.Success) {
            val data = sessionState.data
            val results = data.results.map { res ->
                val info = findDriverInfo(res.driverName, detailsState)
                ResultRowData(
                    position = res.position,
                    number = res.driverNumber,
                    driver = res.driverName,
                    team = res.team,
                    extra = res.timeOrRetired,
                    laps = res.laps,
                    points = res.points,
                    headshotUrl = info?.hero?.driverImage
                )
            }.toImmutableList()
            UiState.Success(
                SessionResultUiState(
                    ResultHeader(data.raceName, data.circuitName),
                    results
                )
            )
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    init {
        fetchRacesResultData()
    }

    fun fetchRacesResultData() {
        _racesFp1Result.value = UiState.Loading
        _racesFp2Result.value = UiState.Loading
        _racesFp3Result.value = UiState.Loading
        _racesSprintQualyResult.value = UiState.Loading
        _racesSprintRaceResult.value = UiState.Loading
        _racesQulyResult.value = UiState.Loading
        _racesResult.value = UiState.Loading
        _driversInfoData.value = UiState.Loading

        viewModelScope.launch {
            delay(1000.milliseconds)

            launch(Dispatchers.IO) {
                try {
                    val raceInfo = tarasDataService.getRaceInfoData()
                    val today = getTodayDate().toRemoveDateExtra()
                    val now = getCurrentMoment()

                    val nextRaceIndex =
                        raceInfo.races.indexOfFirst { it.schedule.race.date.toRemoveDateExtra() >= today }
                    val nextRace =
                        if (nextRaceIndex != -1) raceInfo.races[nextRaceIndex] else raceInfo.races.lastOrNull()

                    val fp1Start =
                        nextRace?.schedule?.fp1?.let { parseSessionTimeToInstant(it.date, it.time) }

                    val activeRace = if (fp1Start != null && fp1Start > now) {
                        if (nextRaceIndex > 0) raceInfo.races[nextRaceIndex - 1] else nextRace
                    } else {
                        nextRace
                    }

                    activeRace?.let { race ->
                        _activeSchedule.value = race.schedule
                        _isSprintWeekend.value =
                            race.schedule.fp2 == null && race.schedule.fp3 == null
                    }
                } catch (e: Exception) {
                    Log.e(lagtag, "Error fetching race info", e)
                }
            }

            launch(Dispatchers.IO) {
                _driversInfoData.value = safeApiCall { tarasDataService.getDriverInfoData() }
            }
            launch(Dispatchers.IO) {
                _racesFp1Result.value = safeApiCall { tarasDataService.getDriverFp1Standings() }
            }
            launch(Dispatchers.IO) {
                _racesFp2Result.value = safeApiCall { tarasDataService.getDriverFp2Standings() }
            }
            launch(Dispatchers.IO) {
                _racesFp3Result.value = safeApiCall { tarasDataService.getDriverFp3Standings() }
            }
            launch(Dispatchers.IO) {
                _racesSprintQualyResult.value =
                    safeApiCall { tarasDataService.getDriverSprintQualifyingStandings() }
            }
            launch(Dispatchers.IO) {
                _racesSprintRaceResult.value =
                    safeApiCall { tarasDataService.getDriverSprintResultStandings() }
            }
            launch(Dispatchers.IO) {
                _racesQulyResult.value =
                    safeApiCall { tarasDataService.getDriverRaceQualifyingStandings() }
            }
            launch(Dispatchers.IO) {
                _racesResult.value = safeApiCall { tarasDataService.getDriverRaceResultStandings() }
            }
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): UiState<T> {
        return try {
            UiState.Success(apiCall())
        } catch (e: Exception) {
            Log.e(lagtag, "API Error: ${e.message}", e)
            UiState.Error("Data unavailable")
        }
    }


    private fun findDriverInfo(
        driverName: String,
        details: UiState<List<F1DriversInfoResponse>>
    ): F1DriversInfoResponse? {
        val detailList = (details as? UiState.Success)?.data ?: return null
        return detailList.find { d ->
            val fullName = "${d.hero.firstName} ${d.hero.lastName}"
            fullName.contains(driverName, ignoreCase = true) ||
                    d.hero.lastName.contains(driverName, ignoreCase = true)
        }
    }
}