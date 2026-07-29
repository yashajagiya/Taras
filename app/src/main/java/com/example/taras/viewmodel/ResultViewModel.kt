package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.UiState
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.DriverRaceQualifyingResponse
import com.example.taras.network_calls.taras.model.DriverRaceResultResponse
import com.example.taras.network_calls.taras.model.Fp1Response
import com.example.taras.network_calls.taras.model.Fp2Response
import com.example.taras.network_calls.taras.model.Fp3Response
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    val results: ImmutableList<ResultRowData> = kotlinx.collections.immutable.persistentListOf()
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
    private val _driverDetails = MutableStateFlow<UiState<List<DriverDetail>>>(UiState.Loading)

    val fp1Results = combine(_racesFp1Result, _driverDetails) { state, details ->
        mapSessionState(state) { data ->
            val results = data.results.map { res ->
                val detail = findDriverDetail(res.driver, details)
                ResultRowData(
                    position = res.position,
                    number = res.number,
                    driver = res.driver,
                    team = res.team,
                    extra = res.timeOrGap,
                    laps = res.laps,
                    headshotUrl = detail?.headshotUrl
                )
            }.toImmutableList()
            SessionResultUiState(ResultHeader(data.raceName, data.circuitName), results)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val fp2Results = combine(_racesFp2Result, _driverDetails) { state, details ->
        mapSessionState(state) { data ->
            val results = data.results.map { res ->
                val detail = findDriverDetail(res.driver, details)
                ResultRowData(
                    position = res.position,
                    number = res.number,
                    driver = res.driver,
                    team = res.team,
                    extra = res.timeOrGap,
                    laps = res.laps,
                    headshotUrl = detail?.headshotUrl
                )
            }.toImmutableList()
            SessionResultUiState(ResultHeader(data.raceName, data.circuitName), results)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val fp3Results = combine(_racesFp3Result, _driverDetails) { state, details ->
        mapSessionState(state) { data ->
            val results = data.results.map { res ->
                val detail = findDriverDetail(res.driver, details)
                ResultRowData(
                    position = res.position,
                    number = res.number,
                    driver = res.driver,
                    team = res.team,
                    extra = res.timeOrGap,
                    laps = res.laps,
                    headshotUrl = detail?.headshotUrl
                )
            }.toImmutableList()
            SessionResultUiState(ResultHeader(data.raceName, data.circuitName), results)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val qualifyResults = combine(_racesQulyResult, _driverDetails) { state, details ->
        mapSessionState(state) { data ->
            val results = data.results.map { res ->
                val detail = findDriverDetail(res.driverName, details)
                ResultRowData(
                    position = res.position,
                    number = res.driverNumber,
                    driver = res.driverName,
                    team = res.team,
                    extra = res.q3.ifBlank { res.q2.ifBlank { res.q1 } },
                    laps = res.laps,
                    headshotUrl = detail?.headshotUrl
                )
            }.toImmutableList()
            SessionResultUiState(ResultHeader(data.raceName, data.circuitName), results)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val raceResults = combine(_racesResult, _driverDetails) { state, details ->
        mapSessionState(state) { data ->
            val results = data.results.map { res ->
                val detail = findDriverDetail(res.driverName, details)
                ResultRowData(
                    position = res.position,
                    number = res.driverNumber,
                    driver = res.driverName,
                    team = res.team,
                    extra = res.timeOrRetired,
                    laps = res.laps,
                    points = res.points,
                    headshotUrl = detail?.headshotUrl
                )
            }.toImmutableList()
            SessionResultUiState(ResultHeader(data.raceName, data.circuitName), results)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        fetchRacesResultData()
    }

    fun fetchRacesResultData() {
        _racesFp1Result.value = UiState.Loading
        _racesFp2Result.value = UiState.Loading
        _racesFp3Result.value = UiState.Loading
        _racesQulyResult.value = UiState.Loading
        _racesResult.value = UiState.Loading
        _driverDetails.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            delay(1000.milliseconds)

            launch {
                _driverDetails.value = safeApiCall { tarasDataService.getDriverDetailsANDImage() }
            }
            launch {
                _racesFp1Result.value = safeApiCall { tarasDataService.getDriverFp1Standings() }
            }
            launch {
                _racesFp2Result.value = safeApiCall { tarasDataService.getDriverFp2Standings() }
            }
            launch {
                _racesFp3Result.value = safeApiCall { tarasDataService.getDriverFp3Standings() }
            }
            launch {
                _racesQulyResult.value =
                    safeApiCall { tarasDataService.getDriverRaceQualifyingStandings() }
            }
            launch {
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

    private fun <T> mapSessionState(
        state: UiState<T>,
        transform: (T) -> SessionResultUiState
    ): UiState<SessionResultUiState> {
        return when (state) {
            is UiState.Success -> UiState.Success(transform(state.data))
            is UiState.Error -> UiState.Error(state.message)
            is UiState.Loading -> UiState.Loading
        }
    }

    private fun findDriverDetail(
        driverName: String,
        details: UiState<List<DriverDetail>>
    ): DriverDetail? {
        val detailList = (details as? UiState.Success)?.data ?: return null
        return detailList.find { d ->
            d.broadcastName?.contains(driverName, ignoreCase = true) == true ||
                    d.fullName?.contains(driverName, ignoreCase = true) == true
        }
    }
}