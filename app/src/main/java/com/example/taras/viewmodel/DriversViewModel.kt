package com.example.taras.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.DriverPerRaceResponce
import com.example.taras.core.common.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.milliseconds

@Stable
class DriversViewModel : ViewModel() {
    private val logTag = "DriversViewModel"

    // code for openf1 api
    // private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)


    //private val _drivers = MutableStateFlow<UiState<List<DriverStanding>>>(UiState.Loading)


    private val _drivers = MutableStateFlow<UiState<DriverPerRaceResponce>>(UiState.Loading)
    val drivers = _drivers.asStateFlow()


//    private val _driverDetails = MutableStateFlow<UiState<List<DriverDetail>>>(UiState.Loading)
//    val driverDetails = _driverDetails.asStateFlow()


    private val _driverDetails = MutableStateFlow<UiState<ImmutableList<DriverDetail>>>(UiState.Loading)
    val driverDetails = _driverDetails.asStateFlow()

    val combinedDrivers = combine(_drivers, _driverDetails) { driversState, detailsState ->
        if (driversState is UiState.Success && detailsState is UiState.Success) {
            val drivers = driversState.data.entries
            val details = detailsState.data
            val combined = drivers.map { driver ->
                val detail = details.find { it.driverNumber == driver.driverNumber }
                DriverUiModel(
                    driverNumber = driver.driverNumber,
                    rank = driver.rank,
                    name = driver.name,
                    teamName = detail?.teamName ?: driver.teamName,
                    points = driver.championshipPts.displayValue,
                    teamColor = detail?.teamColour,
                    headshotUrl = detail?.headshotUrl,
                    carNumberImage = detail?.racingNumberMask,
                    fullName = detail?.fullName,
                    nationality = driver.nationality
                )
            }.toImmutableList()
            UiState.Success(combined)
        } else if (driversState is UiState.Error || detailsState is UiState.Error) {
            UiState.Error("Something went wrong")
        } else {
            UiState.Loading
        }
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading)

    val topThree = combinedDrivers.map { state ->
        if (state is UiState.Success) {
            UiState.Success(state.data.take(3).toImmutableList())
        } else {
            state
        }
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading)

    init {
        fetchDriverData()
    }

    fun fetchDriverData() {
        viewModelScope.launch {
            _drivers.value = UiState.Loading
            delay(1000.milliseconds)

            try {
                supervisorScope {

                    //  val driversDeferred = async(Dispatchers.IO) { openF1Service.getDriverStandings() }
                    // val detailsDeferred = async(Dispatchers.IO) { tarasDataService.getDriverDetails() }

                    val driversDeferred =
                        async(Dispatchers.IO) { tarasDataService.getDriverStandings() }

                    val detailsDeferred =
                        async(Dispatchers.IO) { tarasDataService.getDriverDetailsANDImage() }

                    try {
                        _drivers.value = UiState.Success(driversDeferred.await())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching drivers API", e)
                        _drivers.value = UiState.Error("Something went wrong")
                    }

//                    try {
//                        _driverDetails.value = UiState.Success(detailsDeferred.await())
//                    } catch (e: Exception) {
//                        Log.e(logTag, "Error fetching driver details API", e)
//                        _driverDetails.value = UiState.Error("Something went wrong")
//                    }

                    try {
                        _driverDetails.value = UiState.Success(detailsDeferred.await().toImmutableList())
                    } catch (e: Exception) {
                        Log.e(logTag, "Error fetching driver details API", e)
                        _driverDetails.value = UiState.Error("Something went wrong")
                    }
                }
            } catch (e: Exception) {

                Log.e(logTag, "Error in fetchDriverData", e)
                _drivers.value = UiState.Error("Something went wrong")

                // _driverDetails.value = UiState.Error("Something went wrong")

                _driverDetails.value = UiState.Error("Something went wrong")
            }
        }

    }
}


@Immutable
data class DriverUiModel(
    val driverNumber: Int,
    val rank: Int,
    val name: String,
    val teamName: String,
    val points: String,
    val teamColor: String?,
    val headshotUrl: String?,
    val carNumberImage: String?,
    val fullName: String?,
    val nationality: String
)
