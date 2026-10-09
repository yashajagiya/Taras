package com.example.taras.network_calls.taras

import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.StandingsResponse
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorTarasDataService(
    private val client: HttpClient
) : TarasDataService {

    override suspend fun getOverview(): OverviewResponse =
        client.get(ApiConstants.ENDPOINT_V2_OVERVIEW).body()

    override suspend fun getCalendar(): List<CalendarRaceEvent> =
        client.get(ApiConstants.ENDPOINT_V2_CALENDAR).body()

    override suspend fun getStandings(): StandingsResponse =
        client.get(ApiConstants.ENDPOINT_V2_STANDINGS).body()

    override suspend fun getDrivers(): List<DriverDetailResponseV2> =
        client.get(ApiConstants.ENDPOINT_V2_DRIVERS).body()

    override suspend fun getTeams(): List<TeamDetailResponseV2> =
        client.get(ApiConstants.ENDPOINT_V2_TEAMS).body()

    override suspend fun getLatestResults(): WeekendResultsResponse =
        client.get(ApiConstants.ENDPOINT_V2_RESULTS_LATEST).body()

    override suspend fun getResultsByRound(round: Int): WeekendResultsResponse {
        val endpoint = ApiConstants.ENDPOINT_V2_RESULTS_ROUND.replace("{round}", round.toString())
        return client.get(endpoint).body()
    }
}
