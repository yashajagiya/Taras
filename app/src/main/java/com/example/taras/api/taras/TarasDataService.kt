package com.example.taras.api.taras

import com.example.taras.api.ApiConstants
import com.example.taras.api.taras.model.DriverDetail
import retrofit2.http.GET

interface TarasDataService {

    @GET(ApiConstants.ENDPOINT_DRIVER_DETAILS)
    suspend fun getDriverDetails(): List<DriverDetail>
}
