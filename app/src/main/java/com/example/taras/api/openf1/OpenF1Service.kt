package com.example.taras.api.openf1

import com.example.taras.api.ApiConstants
import com.example.taras.api.openf1.model.DriverInfo
import com.example.taras.api.openf1.model.DriverStanding
import com.example.taras.api.openf1.model.TeamStanding
import retrofit2.http.GET

interface OpenF1Service {

    @GET(ApiConstants.ENDPOINT_DRIVER_CHAMPIONSHIP)
    suspend fun getDriverStandings(): List<DriverStanding>

    @GET(ApiConstants.ENDPOINT_TEAM_CHAMPIONSHIP)
    suspend fun getTeamStandings(): List<TeamStanding>

//    @GET("drivers?session_key=latest")
//    suspend fun getDriverInfo(): List<DriverInfo>
}
