package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.repository.F1InfoRepository
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
class ResultViewModel(
    private val f1InfoRepository: F1InfoRepository = F1InfoRepository.instance
) : ViewModel() {

    private val logTag = "ResultViewModel"

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _weekendResults =
        MutableStateFlow<UiState<WeekendResultsResponse>>(UiState.Loading)
    val weekendResults = _weekendResults.asStateFlow()

    private val _selectedRound = MutableStateFlow<Int?>(null)
    val selectedRound = _selectedRound.asStateFlow()

    private val _availableRounds = MutableStateFlow<List<Int>>(emptyList())
    val availableRounds = _availableRounds.asStateFlow()

    private var latestRoundNumber: Int? = null

    private val _isSprintWeekend = MutableStateFlow(false)
    val isSprintWeekend = _isSprintWeekend.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _driversInfoData =
        MutableStateFlow<UiState<List<DriverDetailResponseV2>>>(UiState.Loading)

    val fp1Results = combine(_weekendResults, _driversInfoData) { resultsState, driversState ->
        mapSessionResults(resultsState, driversState) { it.sessions.practice1?.map { res ->
            ResultRowData(
                position = res.position,
                number = res.driverNumber?.toString() ?: "",
                driver = res.driverName,
                team = res.team,
                extra = res.timeOrGap,
                laps = res.laps.toString(),
                headshotUrl = findDriverHeadshot(res.driverName, driversState)
            )
        } }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp2Results = combine(_weekendResults, _driversInfoData, isSprintWeekend) { resultsState, driversState, isSprint ->
        mapSessionResults(resultsState, driversState) { data ->
            if (isSprint) {
                data.sessions.sprintQualifying?.map { res ->
                    ResultRowData(
                        position = res.position,
                        number = res.driverNumber?.toString() ?: "",
                        driver = res.driverName,
                        team = res.team,
                        extra = res.sq3.ifBlank { res.sq2.ifBlank { res.sq1 } },
                        laps = res.laps.toString(),
                        headshotUrl = findDriverHeadshot(res.driverName, driversState)
                    )
                }
            } else {
                data.sessions.practice2?.map { res ->
                    ResultRowData(
                        position = res.position,
                        number = res.driverNumber?.toString() ?: "",
                        driver = res.driverName,
                        team = res.team,
                        extra = res.timeOrGap,
                        laps = res.laps.toString(),
                        headshotUrl = findDriverHeadshot(res.driverName, driversState)
                    )
                }
            }
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp3Results = combine(_weekendResults, _driversInfoData, isSprintWeekend) { resultsState, driversState, isSprint ->
        mapSessionResults(resultsState, driversState) { data ->
            if (isSprint) {
                data.sessions.sprintRace?.map { res ->
                    ResultRowData(
                        position = res.position,
                        number = res.driverNumber?.toString() ?: "",
                        driver = res.driverName,
                        team = res.team,
                        extra = res.timeOrRetired,
                        laps = res.laps.toString(),
                        points = res.points.toString(),
                        headshotUrl = findDriverHeadshot(res.driverName, driversState)
                    )
                }
            } else {
                data.sessions.practice3?.map { res ->
                    ResultRowData(
                        position = res.position,
                        number = res.driverNumber?.toString() ?: "",
                        driver = res.driverName,
                        team = res.team,
                        extra = res.timeOrGap,
                        laps = res.laps.toString(),
                        headshotUrl = findDriverHeadshot(res.driverName, driversState)
                    )
                }
            }
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val qualifyResults = combine(_weekendResults, _driversInfoData) { resultsState, driversState ->
        mapSessionResults(resultsState, driversState) { it.sessions.qualifying?.map { res ->
            ResultRowData(
                position = res.position,
                number = res.driverNumber?.toString() ?: "",
                driver = res.driverName,
                team = res.team,
                extra = res.q3.ifBlank { res.q2.ifBlank { res.q1 } },
                laps = res.laps.toString(),
                headshotUrl = findDriverHeadshot(res.driverName, driversState)
            )
        } }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val raceResults = combine(_weekendResults, _driversInfoData) { resultsState, driversState ->
        mapSessionResults(resultsState, driversState) { it.sessions.race?.map { res ->
            ResultRowData(
                position = res.position,
                number = res.driverNumber?.toString() ?: "",
                driver = res.driverName,
                team = res.team,
                extra = res.timeOrRetired,
                laps = res.laps.toString(),
                points = res.points.toString(),
                headshotUrl = findDriverHeadshot(res.driverName, driversState)
            )
        } }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val overview = tarasDataService.getOverview()
                if (overview.roundCurrent > 0) {
                    latestRoundNumber = overview.roundCurrent
                    _availableRounds.value = (1..overview.roundCurrent).reversed().toList()
                }
            } catch (e: Exception) {
                Log.w(logTag, "Could not fetch overview for rounds list, falling back to calendar", e)
                try {
                    val calendar = tarasDataService.getCalendar()
                    val completedRounds = calendar.filter { it.winner != null }.map { it.round }
                    if (completedRounds.isNotEmpty()) {
                        val maxRound = completedRounds.max()
                        latestRoundNumber = maxRound
                        _availableRounds.value = (1..maxRound).reversed().toList()
                    }
                } catch (calError: Exception) {
                    Log.w(logTag, "Could not fetch calendar for rounds list", calError)
                }
            }
        }
        fetchRacesResultData(isRefresh = false)
    }

    fun selectRound(round: Int?) {
        if (_selectedRound.value == round) return
        _selectedRound.value = round
        _weekendResults.value = UiState.Loading
        fetchRacesResultData(isRefresh = false)
    }

    fun fetchRacesResultData(isRefresh: Boolean = false) {
        if (isRefresh) {
            _isRefreshing.value = true
        } else if (_weekendResults.value !is UiState.Success) {
            _weekendResults.value = UiState.Loading
            _driversInfoData.value = UiState.Loading
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch weekend results for selected round or latest
                val roundToFetch = _selectedRound.value
                try {
                    val response = if (roundToFetch == null || (latestRoundNumber != null && roundToFetch == latestRoundNumber)) {
                        tarasDataService.getLatestResults()
                    } else {
                        tarasDataService.getResultsByRound(roundToFetch)
                    }
                    _weekendResults.value = UiState.Success(response)
                    _isSprintWeekend.value = response.hasSprint

                    val currentRound = response.round
                    if (currentRound > 0) {
                        if (latestRoundNumber == null || currentRound > (latestRoundNumber ?: 0)) {
                            latestRoundNumber = currentRound
                        }
                        if (_availableRounds.value.isEmpty() || currentRound > (_availableRounds.value.firstOrNull() ?: 0)) {
                            _availableRounds.value = (1..currentRound).reversed().toList()
                        }
                    }

                    if (_selectedRound.value == null) {
                        _selectedRound.value = response.round
                    }
                } catch (e: Exception) {
                    Log.e(logTag, "Error fetching race results for round $roundToFetch", e)
                    _weekendResults.value = UiState.Error(e.toAppError())
                }

                // Fetch drivers info for headshot mapping
                try {
                    val drivers = f1InfoRepository.getDriversV2(forceRefresh = isRefresh)
                    _driversInfoData.value = UiState.Success(drivers)
                } catch (e: Exception) {
                    Log.e(logTag, "Error fetching drivers info", e)
                    _driversInfoData.value = UiState.Error(e.toAppError())
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun mapSessionResults(
        resultsState: UiState<WeekendResultsResponse>,
        driversState: UiState<List<DriverDetailResponseV2>>,
        sessionExtractor: (WeekendResultsResponse) -> List<ResultRowData>?
    ): UiState<SessionResultUiState> {
        return when (resultsState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(resultsState.message)
            is UiState.Success -> {
                val data = resultsState.data
                val sessionRows = sessionExtractor(data)
                if (sessionRows == null) {
                    UiState.Success(SessionResultUiState(isStarted = false))
                } else {
                    UiState.Success(
                        SessionResultUiState(
                            header = ResultHeader(data.raceName, data.circuitName),
                            results = sessionRows.toImmutableList(),
                            isStarted = true
                        )
                    )
                }
            }
        }
    }

    private var cachedDriverList: List<DriverDetailResponseV2>? = null
    private var cachedDriverNameMap: Map<String, DriverDetailResponseV2> = emptyMap()

    private fun findDriverHeadshot(
        driverName: String,
        details: UiState<List<DriverDetailResponseV2>>
    ): String? {
        val detailList = (details as? UiState.Success)?.data ?: return null
        if (cachedDriverList !== detailList) {
            cachedDriverList = detailList
            val map = HashMap<String, DriverDetailResponseV2>()
            for (d in detailList) {
                val fullName = "${d.firstName} ${d.lastName}".trim().lowercase()
                val lastName = d.lastName.trim().lowercase()
                val shortName = d.firstName.trim().lowercase()
                val name = d.name.trim().lowercase()
                if (fullName.isNotEmpty()) map[fullName] = d
                if (name.isNotEmpty()) map[name] = d
                if (lastName.isNotEmpty()) map[lastName] = d
                if (shortName.isNotEmpty()) map.putIfAbsent(shortName, d)
            }
            cachedDriverNameMap = map
        }
        val query = driverName.trim().lowercase()
        val found = cachedDriverNameMap[query] ?: detailList.find { d ->
            val fullName = "${d.firstName} ${d.lastName}"
            fullName.contains(driverName, ignoreCase = true) ||
                    d.lastName.contains(driverName, ignoreCase = true) ||
                    d.name.contains(driverName, ignoreCase = true)
        }
        return found?.images?.portrait
    }
}