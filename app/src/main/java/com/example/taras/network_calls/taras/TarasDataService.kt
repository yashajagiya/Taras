package com.example.taras.network_calls.taras

import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.model.TeamsPerRaceResponse
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.DriverPerRaceResponce
import com.example.taras.network_calls.taras.model.RacesImageResponse
import com.example.taras.network_calls.taras.model.TeamsImageResponse
import retrofit2.http.GET

interface TarasDataService {

    @GET(ApiConstants.ENDPOINT_DRIVER_DETAILS)
    suspend fun getDriverDetailsANDImage(): List<DriverDetail>

    @GET(ApiConstants.ENDPOINT_TEAMS_IMAGE)
    suspend fun getTeamsImage(): List<TeamsImageResponse>

    @GET(ApiConstants.ENDPOINT_RACES_IMAGE)
    suspend fun getRacesImage(): List<RacesImageResponse>

    @GET(ApiConstants.ENDPOINT_TEAM_CHAMPIONSHIP)
    suspend fun getTeamStandings(): TeamsPerRaceResponse

    @GET(ApiConstants.ENDPOINT_DRIVER_CHAMPIONSHIP)
    suspend fun getDriverStandings(): DriverPerRaceResponce
}
