package com.example.taras.network_calls.taras

import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.StandingsResponse
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TarasDataService {

    @GET(ApiConstants.ENDPOINT_V2_OVERVIEW)
    suspend fun getOverview(): OverviewResponse

    @GET(ApiConstants.ENDPOINT_V2_CALENDAR)
    suspend fun getCalendar(): List<CalendarRaceEvent>

    @GET(ApiConstants.ENDPOINT_V2_STANDINGS)
    suspend fun getStandings(): StandingsResponse

    @GET(ApiConstants.ENDPOINT_V2_DRIVERS)
    suspend fun getDrivers(): List<DriverDetailResponseV2>

    @GET(ApiConstants.ENDPOINT_V2_TEAMS)
    suspend fun getTeams(): List<TeamDetailResponseV2>

    @GET(ApiConstants.ENDPOINT_V2_RESULTS_LATEST)
    suspend fun getLatestResults(): WeekendResultsResponse

    @GET(ApiConstants.ENDPOINT_V2_RESULTS_ROUND)
    suspend fun getResultsByRound(@Path("round") round: Int): WeekendResultsResponse
}
