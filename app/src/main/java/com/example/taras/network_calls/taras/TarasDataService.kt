package com.example.taras.network_calls.taras

import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.StandingsResponse
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse

interface TarasDataService {

    suspend fun getOverview(): OverviewResponse

    suspend fun getCalendar(): List<CalendarRaceEvent>

    suspend fun getStandings(): StandingsResponse

    suspend fun getDrivers(): List<DriverDetailResponseV2>

    suspend fun getTeams(): List<TeamDetailResponseV2>

    suspend fun getLatestResults(): WeekendResultsResponse

    suspend fun getResultsByRound(round: Int): WeekendResultsResponse

    suspend fun getLive(): LiveDiscoveryResponse
}
