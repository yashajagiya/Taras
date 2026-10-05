package com.example.taras.network_calls.taras

import com.example.taras.network_calls.taras.model.SprintResultResponse
import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.model.TeamsPerRaceResponse
import com.example.taras.network_calls.taras.model.DriverPerRaceResponse
import com.example.taras.network_calls.taras.model.DriverRaceQualifyingResponse
import com.example.taras.network_calls.taras.model.DriverRaceResultResponse
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.network_calls.taras.model.F1RacesInfoResponse
import com.example.taras.network_calls.taras.model.F1TeamsInfoResponse
import com.example.taras.network_calls.taras.model.Fp1Response
import com.example.taras.network_calls.taras.model.Fp2Response
import com.example.taras.network_calls.taras.model.Fp3Response
import com.example.taras.network_calls.taras.model.SprintQulyResponse
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.StandingsResponse
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TarasDataService {

    // --- v2 API Endpoints ---
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

    // --- Legacy v1 API Endpoints (maintained during phase migration) ---

    @GET(ApiConstants.ENDPOINT_TEAM_CHAMPIONSHIP)
    suspend fun getTeamStandings(): TeamsPerRaceResponse

    @GET(ApiConstants.ENDPOINT_DRIVER_CHAMPIONSHIP)
    suspend fun getDriverStandings(): DriverPerRaceResponse

    @GET(ApiConstants.ENDPOINT_RACES_FP1)
    suspend fun getDriverFp1Standings(): Fp1Response

    @GET(ApiConstants.ENDPOINT_RACES_FP2)
    suspend fun getDriverFp2Standings(): Fp2Response

    @GET(ApiConstants.ENDPOINT_RACES_FP3)
    suspend fun getDriverFp3Standings(): Fp3Response

    @GET(ApiConstants.ENDPOINT_RACES_SPRINT_QUALIFYING)
    suspend fun getDriverSprintQualifyingStandings(): SprintQulyResponse

    @GET(ApiConstants.ENDPOINT_RACES_SPRINT_RESULT)
    suspend fun getDriverSprintResultStandings(): SprintResultResponse

    @GET(ApiConstants.ENDPOINT_RACES_QUALIFYING)
    suspend fun getDriverRaceQualifyingStandings(): DriverRaceQualifyingResponse

    @GET(ApiConstants.ENDPOINT_RACES_RESULT)
    suspend fun getDriverRaceResultStandings(): DriverRaceResultResponse

    @GET(ApiConstants.ENDPOINT_TEAMS_DATA)
    suspend fun getTeamsInfoData(): List<F1TeamsInfoResponse>

    @GET(ApiConstants.ENDPOINT_DRIVERS_DATA)
    suspend fun getDriverInfoData(): List<F1DriversInfoResponse>

    @GET(ApiConstants.ENDPOINT_RACES_DATA)
    suspend fun getRaceInfoData(): F1RacesInfoResponse

}
