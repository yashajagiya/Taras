package com.example.taras

import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.domain.repository.DriverRepository
import com.example.taras.domain.repository.RaceRepository
import com.example.taras.domain.repository.TeamRepository
import com.example.taras.domain.usecase.GetDriversUseCase
import com.example.taras.domain.usecase.GetRaceCalendarUseCase
import com.example.taras.domain.usecase.GetTeamsUseCase
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CleanArchitectureAndMvvmTest {

    @Test
    fun getDriversUseCase_returnsDataFromRepository() = runBlocking {
        val fakeRepo = object : DriverRepository {
            override suspend fun getDrivers(forceRefresh: Boolean): List<DriverDetailResponseV2> {
                return listOf(DriverDetailResponseV2(id = "norris", name = "Lando Norris"))
            }

            override fun getTopThreeDrivers(): Flow<List<TopThreeDriversEntity>> {
                return flowOf(listOf(TopThreeDriversEntity(1, 1, "Lando Norris", 100f, "McLaren")))
            }

            override suspend fun saveTopThreeDrivers(drivers: List<TopThreeDriversEntity>) {}
            override fun clearCache() {}
        }

        val useCase = GetDriversUseCase(fakeRepo)
        val drivers = useCase.getDrivers()

        assertEquals(1, drivers.size)
        assertEquals("Lando Norris", drivers.first().name)
    }

    @Test
    fun getTeamsUseCase_returnsDataFromRepository() = runBlocking {
        val fakeRepo = object : TeamRepository {
            override suspend fun getTeams(forceRefresh: Boolean): List<TeamDetailResponseV2> {
                return listOf(TeamDetailResponseV2(id = "ferrari", name = "Ferrari"))
            }

            override fun clearCache() {}
        }

        val useCase = GetTeamsUseCase(fakeRepo)
        val teams = useCase()

        assertEquals(1, teams.size)
        assertEquals("Ferrari", teams.first().name)
    }

    @Test
    fun getRaceCalendarUseCase_returnsDataFromRepository() = runBlocking {
        val fakeRepo = object : RaceRepository {
            override suspend fun getCalendar(forceRefresh: Boolean): List<CalendarRaceEvent> {
                return emptyList()
            }

            override fun getCachedCalendar(): Flow<List<CalendarRaceEvent>?> {
                return flowOf(emptyList())
            }

            override suspend fun saveCurrentSessionStatus(sessionName: String, sessionTime: String) {}
        }

        val useCase = GetRaceCalendarUseCase(fakeRepo)
        val calendar = useCase.getCalendar()

        assertEquals(0, calendar.size)
    }
}
