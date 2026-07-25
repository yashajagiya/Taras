package com.example.taras.network_calls.f1apidev

import com.example.taras.network_calls.f1apidev.model.RaceData
import com.example.taras.network_calls.ApiConstants
import retrofit2.http.GET

interface F1ApiDevService {

    @GET(ApiConstants.ENDPOINT_CIRCUITS_CURRENT)
    suspend fun getCurrentCircuits(): RaceData

//    @GET(ApiConstants.ENDPOINT_CIRCUITS_CURRENT)
//    suspend fun getRaceData(): CircuitResponse
}
