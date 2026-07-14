package com.example.taras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.api.NetworkModule
import com.example.taras.api.openf1.OpenF1Service
import com.example.taras.api.openf1.model.DriverStanding
import com.example.taras.api.taras.TarasDataService
import com.example.taras.api.taras.model.DriverDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriversViewModel : ViewModel() {
    private val logTag = "DriversViewModel"
    
    private val openF1Service = NetworkModule.openF1Retrofit.create(OpenF1Service::class.java)
    private val tarasDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)

    private val _drivers = MutableStateFlow<List<DriverStanding>>(emptyList())
    val drivers = _drivers.asStateFlow()
    
    private val _driverDetails = MutableStateFlow<List<DriverDetail>>(emptyList())
    val driverDetails = _driverDetails.asStateFlow()

    init {
        fetchDriverData()
    }

    private fun fetchDriverData() {
        viewModelScope.launch {
            try {
                val driversDeferred = async { openF1Service.getDriverStandings() }
                val detailsDeferred = async { tarasDataService.getDriverDetails() }
                
                _drivers.value = driversDeferred.await()
                _driverDetails.value = detailsDeferred.await()
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching driver data", e)
            }
        }
    }
}
