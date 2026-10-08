package com.example.taras

import com.example.taras.core.repository.F1InfoRepository
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.StandingsResponse
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class F1InfoRepositoryTest {

    private lateinit var fakeService: FakeTarasDataService
    private lateinit var repository: F1InfoRepository

    @Before
    fun setUp() {
        fakeService = FakeTarasDataService()
        repository = F1InfoRepository(fakeService)
    }

    @Test
    fun getDriversV2_cachesResponse_secondCallDoesNotHitService() = runBlocking {
        assertEquals(0, fakeService.driversV2CallCount)

        val firstCall = repository.getDriversV2()
        assertEquals(1, fakeService.driversV2CallCount)
        assertEquals(1, firstCall.size)

        val secondCall = repository.getDriversV2()
        assertEquals(1, fakeService.driversV2CallCount)
        assertEquals(firstCall, secondCall)
    }

    @Test
    fun getDriversV2_forceRefresh_hitsServiceAgain() = runBlocking {
        repository.getDriversV2()
        assertEquals(1, fakeService.driversV2CallCount)

        repository.getDriversV2(forceRefresh = true)
        assertEquals(2, fakeService.driversV2CallCount)
    }

    @Test
    fun getTeamsV2_cachesResponse_secondCallDoesNotHitService() = runBlocking {
        assertEquals(0, fakeService.teamsV2CallCount)

        val firstCall = repository.getTeamsV2()
        assertEquals(1, fakeService.teamsV2CallCount)
        assertEquals(1, firstCall.size)

        val secondCall = repository.getTeamsV2()
        assertEquals(1, fakeService.teamsV2CallCount)
        assertEquals(firstCall, secondCall)
    }

    @Test
    fun clearCache_invalidatesV2Cache() = runBlocking {
        repository.getDriversV2()
        assertEquals(1, fakeService.driversV2CallCount)

        repository.clearCache()
        repository.getDriversV2()
        assertEquals(2, fakeService.driversV2CallCount)
    }

    private class FakeTarasDataService : TarasDataService {
        var driversV2CallCount = 0
        var teamsV2CallCount = 0

        override suspend fun getOverview(): OverviewResponse = throw UnsupportedOperationException()
        override suspend fun getCalendar(): List<CalendarRaceEvent> = throw UnsupportedOperationException()
        override suspend fun getStandings(): StandingsResponse = throw UnsupportedOperationException()

        override suspend fun getDrivers(): List<DriverDetailResponseV2> {
            driversV2CallCount++
            return listOf(DriverDetailResponseV2(id = "max_verstappen", name = "Max Verstappen"))
        }

        override suspend fun getTeams(): List<TeamDetailResponseV2> {
            teamsV2CallCount++
            return listOf(TeamDetailResponseV2(id = "red_bull_racing", name = "Red Bull Racing"))
        }

        override suspend fun getLatestResults(): WeekendResultsResponse = throw UnsupportedOperationException()
        override suspend fun getResultsByRound(round: Int): WeekendResultsResponse = throw UnsupportedOperationException()
    }
}
