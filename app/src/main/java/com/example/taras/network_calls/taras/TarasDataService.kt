package com.example.taras.network_calls.taras

import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.model.TeamsPerRaceResponse
import com.example.taras.network_calls.taras.model.DriverPerRaceResponce
import com.example.taras.network_calls.taras.model.DriverRaceQualifyingResponse
import com.example.taras.network_calls.taras.model.DriverRaceResultResponse
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.network_calls.taras.model.F1TeamsInfoResponse
import com.example.taras.network_calls.taras.model.Fp1Response
import com.example.taras.network_calls.taras.model.Fp2Response
import com.example.taras.network_calls.taras.model.Fp3Response
import com.example.taras.network_calls.taras.model.RacesImageResponse
import retrofit2.http.GET

interface TarasDataService {

//    @GET(ApiConstants.ENDPOINT_DRIVER_DETAILS)
//    suspend fun getDriverDetailsANDImage(): List<DriverDetail>

//    @GET(ApiConstants.ENDPOINT_TEAMS_IMAGE)
//    suspend fun getTeamsImage(): List<TeamsImageResponse>

    @GET(ApiConstants.ENDPOINT_RACES_IMAGE)
    suspend fun getRacesImage(): List<RacesImageResponse>

    @GET(ApiConstants.ENDPOINT_TEAM_CHAMPIONSHIP)
    suspend fun getTeamStandings(): TeamsPerRaceResponse

    @GET(ApiConstants.ENDPOINT_DRIVER_CHAMPIONSHIP)
    suspend fun getDriverStandings(): DriverPerRaceResponce

    @GET(ApiConstants.ENDPOINT_RACES_FP1)
    suspend fun getDriverFp1Standings(): Fp1Response

    @GET(ApiConstants.ENDPOINT_RACES_FP2)
    suspend fun getDriverFp2Standings(): Fp2Response

    @GET(ApiConstants.ENDPOINT_RACES_FP3)
    suspend fun getDriverFp3Standings(): Fp3Response

    @GET(ApiConstants.ENDPOINT_RACES_QUALIFYING)
    suspend fun getDriverRaceQualifyingStandings(): DriverRaceQualifyingResponse

    @GET(ApiConstants.ENDPOINT_RACES_RESULT)
    suspend fun getDriverRaceResultStandings(): DriverRaceResultResponse

    @GET(ApiConstants.ENDPOINT_TEAMS_DATA)
    suspend fun getTeamsInfoData(): List<F1TeamsInfoResponse>

    @GET(ApiConstants.ENDPOINT_DRIVERS_DATA)
    suspend fun getDriverInfoData(): List<F1DriversInfoResponse>

   
}
