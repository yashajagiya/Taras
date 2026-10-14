package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.TarasApplication
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.repository.F1InfoRepository
import com.example.taras.core.repository.F1LiveRepository
import com.example.taras.domain.repository.DriverRepository
import com.example.taras.domain.repository.LiveTimingRepository
import com.example.taras.domain.repository.RaceRepository
import com.example.taras.domain.repository.ResultsRepository
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TrackStatus
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
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
    val headshotUrl: String? = null,
    val positionDelta: Int = 0,
    val tyreCompound: String? = null,
    val tyresUsed: Int? = null,
    val driverStatus: String = "ACTIVE",
    val teamColorHex: String? = null
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
    val scheduledTime: String? = null,
    val isLive: Boolean = false,
    val trackStatus: TrackStatus = TrackStatus.GREEN
)

@Stable
class ResultViewModel(
    private val resultsRepository: ResultsRepository = TarasApplication.instance.appContainer.resultsRepository,
    private val driverRepository: DriverRepository = TarasApplication.instance.appContainer.driverRepository,
    private val liveTimingRepository: LiveTimingRepository = TarasApplication.instance.appContainer.liveTimingRepository,
    private val raceRepository: RaceRepository = TarasApplication.instance.appContainer.raceRepository
) : ViewModel() {

    // Backwards-compatible constructor
    constructor(f1InfoRepository: F1InfoRepository) : this(
        resultsRepository = TarasApplication.instance.appContainer.resultsRepository,
        driverRepository = TarasApplication.instance.appContainer.driverRepository,
        liveTimingRepository = TarasApplication.instance.appContainer.liveTimingRepository,
        raceRepository = TarasApplication.instance.appContainer.raceRepository
    )

    private val logTag = "ResultViewModel"

    private val _weekendResults =
        MutableStateFlow<UiState<WeekendResultsResponse>>(UiState.Loading)
    val weekendResults = _weekendResults.asStateFlow()

    private val _selectedRound = MutableStateFlow<Int?>(null)
    val selectedRound = _selectedRound.asStateFlow()

    private val _availableRounds = MutableStateFlow<List<Int>>(emptyList())
    val availableRounds = _availableRounds.asStateFlow()

    private val _latestRoundNumber = MutableStateFlow<Int?>(null)
    val latestRoundNumber = _latestRoundNumber.asStateFlow()

    private val _isSprintWeekend = MutableStateFlow(false)
    val isSprintWeekend = _isSprintWeekend.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _driversInfoData =
        MutableStateFlow<UiState<List<DriverDetailResponseV2>>>(UiState.Loading)

    val isLiveTimingActive = liveTimingRepository.isLiveTimingActive
    val isLiveSession = combine(
        liveTimingRepository.isLiveSession,
        liveTimingRepository.isLiveTimingActive,
        _selectedRound,
        liveTimingRepository.discoveryData,
        _latestRoundNumber
    ) { isLive, isTimingActive, selectedRound, discovery, latestRound ->
        if (!isLive || !isTimingActive) return@combine false
        val activeRound = discovery?.round ?: latestRound
        selectedRound == null || (activeRound != null && selectedRound == activeRound)
    }.stateIn(viewModelScope, WhileSubscribed(5000), false)
    val liveTrackStatus = liveTimingRepository.trackStatus
    val liveSnapshot = liveTimingRepository.timingSnapshot
    val liveDiscovery = liveTimingRepository.discoveryData

    private val previousPositionsMap = mutableMapOf<String, Int>()

    private fun mapSessionNameToTabIndex(sessionName: String?, isSprint: Boolean, hasLapFraction: Boolean = false): Int {
        if (sessionName.isNullOrBlank()) return if (hasLapFraction) 4 else -1
        val s = sessionName.lowercase()
        return when {
            s.contains("practice 1") || s.contains("fp1") -> 0
            s.contains("sprint qualifying") || s.contains("sprint qualy") || s.contains("sq") -> if (isSprint) 1 else -1
            s.contains("practice 2") || s.contains("fp2") -> if (isSprint) -1 else 1
            s.contains("sprint") && !s.contains("qualifying") -> if (isSprint) 2 else -1
            s.contains("practice 3") || s.contains("fp3") -> if (isSprint) -1 else 2
            s.contains("qualifying") || s.contains("qualy") || s.contains("q1") || s.contains("q2") || s.contains("q3") -> 3
            s.contains("race") || hasLapFraction -> 4
            else -> -1
        }
    }

    val liveActiveTabIndex = combine(liveSnapshot, liveDiscovery, _isSprintWeekend, liveTimingRepository.timelineItems) { snapshot, discovery, isSprint, items ->
        val session = snapshot?.session?.takeIf { it.isNotBlank() } ?: discovery?.session
        val hasLaps = items.firstOrNull()?.lap?.contains("/") == true
        mapSessionNameToTabIndex(session, isSprint, hasLaps)
    }.stateIn(viewModelScope, WhileSubscribed(5000), -1)

    private fun buildLiveSessionState(
        snapshot: RaceSnapshot,
        driversState: UiState<List<DriverDetailResponseV2>>
    ): UiState<SessionResultUiState> {
        val driversList = snapshot.drivers.map { driver ->
            val prev = previousPositionsMap[driver.tla]
            val delta = if (prev != null && prev != driver.position) prev - driver.position else 0
            previousPositionsMap[driver.tla] = driver.position

            ResultRowData(
                position = driver.position.toString(),
                number = driver.tla,
                driver = driver.fullName,
                team = driver.team,
                extra = driver.gapOrTime,
                laps = if (driver.tyresUsed > 0) "${driver.tyresUsed} sets" else "-",
                points = null,
                headshotUrl = findDriverHeadshot(driver.fullName, driversState),
                positionDelta = delta,
                tyreCompound = driver.tyre,
                tyresUsed = driver.tyresUsed,
                driverStatus = driver.status,
                teamColorHex = driver.teamColor
            )
        }.toImmutableList()

        return UiState.Success(
            SessionResultUiState(
                header = ResultHeader(snapshot.grandPrix, snapshot.session),
                results = driversList,
                isStarted = true,
                isLive = true,
                trackStatus = snapshot.trackStatus
            )
        )
    }

    data class LiveStateBundle(
        val isLive: Boolean = false,
        val isTimingActive: Boolean = false,
        val snapshot: RaceSnapshot? = null,
        val activeTabIndex: Int = -1,
        val activeRound: Int? = null
    )

    val liveStateBundle = combine(
        isLiveSession,
        isLiveTimingActive,
        liveSnapshot,
        liveActiveTabIndex,
        liveDiscovery
    ) { isLive, isTimingActive, snapshot, activeTab, discovery ->
        LiveStateBundle(
            isLive = isLive && isTimingActive,
            isTimingActive = isTimingActive,
            snapshot = snapshot,
            activeTabIndex = activeTab,
            activeRound = discovery?.round ?: _latestRoundNumber.value
        )
    }.stateIn(viewModelScope, WhileSubscribed(5000), LiveStateBundle())

    val fp1Results = combine(
        _weekendResults,
        _driversInfoData,
        liveStateBundle,
        _selectedRound
    ) { resultsState, driversState, liveBundle, selectedRound ->
        val isLatest = selectedRound == null || (liveBundle.activeRound != null && selectedRound == liveBundle.activeRound)
        if (liveBundle.isLive && isLatest && liveBundle.activeTabIndex == 0 && liveBundle.snapshot != null && liveBundle.snapshot.drivers.isNotEmpty()) {
            buildLiveSessionState(liveBundle.snapshot, driversState)
        } else {
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
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp2Results = combine(
        _weekendResults,
        _driversInfoData,
        _isSprintWeekend,
        liveStateBundle,
        _selectedRound
    ) { resultsState, driversState, isSprint, liveBundle, selectedRound ->
        val isLatest = selectedRound == null || (liveBundle.activeRound != null && selectedRound == liveBundle.activeRound)
        if (liveBundle.isLive && isLatest && liveBundle.activeTabIndex == 1 && liveBundle.snapshot != null && liveBundle.snapshot.drivers.isNotEmpty()) {
            buildLiveSessionState(liveBundle.snapshot, driversState)
        } else {
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
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val fp3Results = combine(
        _weekendResults,
        _driversInfoData,
        _isSprintWeekend,
        liveStateBundle,
        _selectedRound
    ) { resultsState, driversState, isSprint, liveBundle, selectedRound ->
        val isLatest = selectedRound == null || (liveBundle.activeRound != null && selectedRound == liveBundle.activeRound)
        if (liveBundle.isLive && isLatest && liveBundle.activeTabIndex == 2 && liveBundle.snapshot != null && liveBundle.snapshot.drivers.isNotEmpty()) {
            buildLiveSessionState(liveBundle.snapshot, driversState)
        } else {
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
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val qualifyResults = combine(
        _weekendResults,
        _driversInfoData,
        liveStateBundle,
        _selectedRound
    ) { resultsState, driversState, liveBundle, selectedRound ->
        val isLatest = selectedRound == null || (liveBundle.activeRound != null && selectedRound == liveBundle.activeRound)
        if (liveBundle.isLive && isLatest && liveBundle.activeTabIndex == 3 && liveBundle.snapshot != null && liveBundle.snapshot.drivers.isNotEmpty()) {
            buildLiveSessionState(liveBundle.snapshot, driversState)
        } else {
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
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    val raceResults = combine(
        _weekendResults,
        _driversInfoData,
        liveStateBundle,
        _selectedRound
    ) { resultsState, driversState, liveBundle, selectedRound ->
        val isLatest = selectedRound == null || (liveBundle.activeRound != null && selectedRound == liveBundle.activeRound)
        if (liveBundle.isLive && isLatest && liveBundle.activeTabIndex == 4 && liveBundle.snapshot != null && liveBundle.snapshot.drivers.isNotEmpty()) {
            buildLiveSessionState(liveBundle.snapshot, driversState)
        } else {
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
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000), UiState.Loading)

    init {
        viewModelScope.launch {
            var wasActive: Boolean? = null
            liveTimingRepository.isLiveTimingActive.collect { isActive ->
                if (wasActive == true && !isActive) {
                    // Transitioned from active to inactive (socket stopped or 5 min staleness) -> load github.io data
                    fetchRacesResultData(isRefresh = true)
                } else if (!isActive && _weekendResults.value !is UiState.Success) {
                    fetchRacesResultData(isRefresh = false)
                }
                wasActive = isActive
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val overview = resultsRepository.getOverview()
                if (overview.roundCurrent > 0) {
                    _latestRoundNumber.value = overview.roundCurrent
                    _availableRounds.value = (1..overview.roundCurrent).reversed().toList()
                }
            } catch (e: Exception) {
                Log.w(logTag, "Could not fetch overview for rounds list, falling back to calendar", e)
                try {
                    val calendar = raceRepository.getCalendar()
                    val completedRounds = calendar.filter { it.winner != null }.map { it.round }
                    if (completedRounds.isNotEmpty()) {
                        val maxRound = completedRounds.max()
                        _latestRoundNumber.value = maxRound
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
                    val latestRound = _latestRoundNumber.value
                    val response = if (roundToFetch == null || (latestRound != null && roundToFetch == latestRound)) {
                        resultsRepository.getLatestResults()
                    } else {
                        resultsRepository.getResultsByRound(roundToFetch)
                    }
                    _weekendResults.value = UiState.Success(response)
                    _isSprintWeekend.value = response.hasSprint

                    val currentRound = response.round
                    if (currentRound > 0) {
                        if (_latestRoundNumber.value == null || currentRound > (_latestRoundNumber.value ?: 0)) {
                            _latestRoundNumber.value = currentRound
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
                    val drivers = driverRepository.getDrivers(forceRefresh = isRefresh)
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