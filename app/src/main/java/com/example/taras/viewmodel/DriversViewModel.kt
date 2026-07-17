package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.api.NetworkModule
//import com.example.taras.api.openf1.OpenF1Service
//import com.example.taras.api.openf1.model.DriverStanding
import com.example.taras.api.taras.TarasDataService
import com.example.taras.api.taras.model.DriverDetail
import com.example.taras.api.taras.model.DriverPerRaceResponce
import com.example.taras.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class DriversViewModel : ViewModel() {
    private val logTag = "DriversViewModel"

    // "//" is old code for openf1 api
    // private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)

    private val tarasDataService =
        NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)


    //private val _drivers = MutableStateFlow<UiState<List<DriverStanding>>>(UiState.Loading)


    private val _drivers = MutableStateFlow<UiState<DriverPerRaceResponce>>(UiState.Loading)
    val drivers = _drivers.asStateFlow()


//    private val _driverDetails = MutableStateFlow<UiState<List<DriverDetail>>>(UiState.Loading)
//    val driverDetails = _driverDetails.asStateFlow()


    private val _driverDetails = MutableStateFlow<UiState<List<DriverDetail>>>(UiState.Loading)
    val driverDetails = _driverDetails.asStateFlow()

    init {
        fetchDriverData()
    }

    private fun fetchDriverData() {
        viewModelScope.launch {
            _drivers.value = UiState.Loading


            //  _driverDetails.value = UiState.Loading


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
                        _driverDetails.value = UiState.Success(detailsDeferred.await())
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
