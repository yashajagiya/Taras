package com.example.taras.network_calls.f1apidev

import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.f1apidev.model.CircuitResponse
import retrofit2.http.GET

interface F1ApiDevService {

    @GET(ApiConstants.ENDPOINT_CIRCUITS_CURRENT)
    suspend fun getCurrentCircuits(): CircuitResponse
}
