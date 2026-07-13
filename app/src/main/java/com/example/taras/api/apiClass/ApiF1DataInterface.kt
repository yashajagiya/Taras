package com.example.taras.api.apiClass

import com.example.taras.api.apiClass.apiLink.drievrDetailSubUrl
import com.example.taras.api.apiClass.apiLink.driverSubUrl
import com.example.taras.api.apiClass.apiLink.teamSubUrl
import com.example.taras.api.dataclass.championship_Driver.DriverschampionshipDataClass
import com.example.taras.api.dataclass.championship_Team.TeamsChampionshipDataClass
import com.example.taras.api.dataclass.driverData.DriverDetailDataClass

import retrofit2.http.GET


interface ApiF1DataInterface {
    //https://f1api.dev/api/2026/drivers-championship
    //https://api.openf1.org/v1/championship_drivers?session_key=latest
    @GET(driverSubUrl)
    suspend fun getDriverStanding (): DriverschampionshipDataClass

    @GET(teamSubUrl)
    suspend fun getTeamStanding (): TeamsChampionshipDataClass

    @GET(drievrDetailSubUrl)
    suspend fun getDriverDetails () : DriverDetailDataClass
}