package com.example.taras.api.f1apidev

import com.example.taras.api.ApiConstants
import com.example.taras.api.f1apidev.model.CircuitResponse
import retrofit2.http.GET

interface F1ApiDevService {

    @GET(ApiConstants.ENDPOINT_CIRCUITS_CURRENT)
    suspend fun getCurrentCircuits(): CircuitResponse
}
