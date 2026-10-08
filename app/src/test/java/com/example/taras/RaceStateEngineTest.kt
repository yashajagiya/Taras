package com.example.taras

import com.example.taras.core.common.SessionType
import com.example.taras.core.engine.RaceStateEngine
import com.example.taras.core.engine.RaceWeekendState
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.CircuitV2
import com.example.taras.network_calls.taras.model.v2.ScheduleV2
import com.example.taras.network_calls.taras.model.v2.SessionTimeV2
import com.example.taras.network_calls.taras.model.v2.WinnerV2
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RaceStateEngineTest {

    private fun createCalendarEvent(
        round: Int = 1,
        raceName: String = "Bahrain Grand Prix",
        fp1Date: String = "2026-03-13",
        fp1Time: String = "11:30:00Z",
        fp2Date: String = "2026-03-13",
        fp2Time: String = "15:00:00Z",
        fp3Date: String = "2026-03-14",
        fp3Time: String = "12:30:00Z",
        qualyDate: String = "2026-03-14",
        qualyTime: String = "16:00:00Z",
        raceDate: String = "2026-03-15",
        raceTime: String = "15:00:00Z",
        winnerName: String? = null
    ): CalendarRaceEvent {
        return CalendarRaceEvent(
            round = round,
            id = "bahrain_2026",
            name = raceName,
            circuit = CircuitV2(
                id = "bahrain",
                name = "Bahrain International Circuit"
            ),
            schedule = ScheduleV2(
                fp1 = SessionTimeV2(date = fp1Date, time = fp1Time),
                fp2 = SessionTimeV2(date = fp2Date, time = fp2Time),
                fp3 = SessionTimeV2(date = fp3Date, time = fp3Time),
                qualifying = SessionTimeV2(date = qualyDate, time = qualyTime),
                race = SessionTimeV2(date = raceDate, time = raceTime)
            ),
            winner = winnerName?.let { WinnerV2(fullName = it) }
        )
    }

    @Test
    fun beforeFp1_stateIsUpcoming() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        // 1 hour before FP1 (2026-03-13 10:30:00Z)
        val now = Instant.parse("2026-03-13T10:30:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Upcoming)
        val upcoming = state as RaceWeekendState.Upcoming
        assertEquals(SessionType.FP1, upcoming.nextSession?.sessionType)
        assertEquals(1.hours, upcoming.timeUntilNextSession)
    }

    @Test
    fun betweenFp1AndFp2_stateIsActive() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        // Between FP1 and FP2 (2026-03-13 13:00:00Z)
        val now = Instant.parse("2026-03-13T13:00:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Active)
        val active = state as RaceWeekendState.Active
        assertEquals(SessionType.FP2, active.currentOrNextSession?.sessionType)
        assertEquals(false, active.isRaceDay)
    }

    @Test
    fun onRaceDayDuringRace_stateIsActiveAndRaceDayIsTrue() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        // Race day morning (2026-03-15 10:00:00Z)
        val now = Instant.parse("2026-03-15T10:00:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Active)
        val active = state as RaceWeekendState.Active
        assertEquals(SessionType.RACE, active.currentOrNextSession?.sessionType)
        assertTrue(active.isRaceDay)
    }

    @Test
    fun afterRaceAndBuffer_stateIsCompleted() {
        val race = createCalendarEvent(winnerName = "Charles Leclerc")
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        // 4 hours after race start (2026-03-15 19:00:00Z)
        val now = Instant.parse("2026-03-15T19:00:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Completed)
        val completed = state as RaceWeekendState.Completed
        assertEquals("Charles Leclerc", completed.winnerName)
    }

    @Test
    fun findNextSessionInfo_picksImmediateNextSession() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        val now = Instant.parse("2026-03-13T10:30:00Z") // 1 hr before FP1

        val sessionInfo = RaceStateEngine.findNextSessionInfo(listOf(mapped), now)

        assertNotNull(sessionInfo)
        assertEquals("FP1", sessionInfo?.sessionName)
        assertEquals(1, sessionInfo?.roundNumber)
        assertEquals("01:00:00", sessionInfo?.countdown)
    }

    @Test
    fun findSelectedRaceForResult_upcomingRace_returnsPreviousRace() {
        val race1 = createCalendarEvent(round = 1, raceDate = "2026-03-01", winnerName = "Max Verstappen")
        val race2 = createCalendarEvent(round = 2, fp1Date = "2026-03-13", fp1Time = "11:30:00Z")
        val races = listOf(race1, race2)

        // It is 2026-03-10 (Between Race 1 and Race 2 FP1)
        val now = Instant.parse("2026-03-10T12:00:00Z")

        val selected = RaceStateEngine.findSelectedRaceForResult(races, now)

        assertEquals(1, selected?.round)
        assertEquals("Max Verstappen", selected?.winner?.fullName)
    }

    @Test
    fun findSelectedRaceForResult_activeRace_returnsActiveRace() {
        val race1 = createCalendarEvent(round = 1, raceDate = "2026-03-01", winnerName = "Max Verstappen")
        val race2 = createCalendarEvent(round = 2, fp1Date = "2026-03-13", fp1Time = "11:30:00Z")
        val races = listOf(race1, race2)

        // It is 2026-03-13 12:00:00Z (FP1 is underway)
        val now = Instant.parse("2026-03-13T12:00:00Z")

        val selected = RaceStateEngine.findSelectedRaceForResult(races, now)

        assertEquals(2, selected?.round)
    }

    @Test
    fun v2CalendarEvent_beforeFp1_stateIsUpcoming() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        val now = Instant.parse("2026-03-13T10:30:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Upcoming)
        val upcoming = state as RaceWeekendState.Upcoming
        assertEquals(SessionType.FP1, upcoming.nextSession?.sessionType)
        assertEquals(1.hours, upcoming.timeUntilNextSession)
    }

    @Test
    fun v2CalendarEvent_betweenFp1AndFp2_stateIsActive() {
        val race = createCalendarEvent()
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        val now = Instant.parse("2026-03-13T13:00:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Active)
        val active = state as RaceWeekendState.Active
        assertEquals(SessionType.FP2, active.currentOrNextSession?.sessionType)
        assertEquals(false, active.isRaceDay)
    }

    @Test
    fun v2CalendarEvent_afterRaceAndBuffer_stateIsCompleted() {
        val race = createCalendarEvent(winnerName = "Charles Leclerc")
        val mapped = RaceStateEngine.mapToCurrentRace(race)
        val now = Instant.parse("2026-03-15T19:00:00Z")

        val state = RaceStateEngine.determineWeekendState(mapped, now)

        assertTrue(state is RaceWeekendState.Completed)
        val completed = state as RaceWeekendState.Completed
        assertEquals("Charles Leclerc", completed.winnerName)
    }

    @Test
    fun v2CalendarEvent_findCurrentOrUpcomingRaces_andNextSessionInfo() {
        val race1 = createCalendarEvent(round = 1, raceDate = "2026-03-01", winnerName = "Max Verstappen")
        val race2 = createCalendarEvent(round = 2, fp1Date = "2026-03-13", fp1Time = "11:30:00Z")
        val calendar = listOf(race1, race2)

        val now = Instant.parse("2026-03-10T12:00:00Z")
        val upcoming = RaceStateEngine.findCurrentOrUpcomingRaces(calendar, now)

        assertEquals(1, upcoming.size)
        assertEquals(2, upcoming[0].roundNumber)

        val nextSession = RaceStateEngine.findNextSessionInfo(upcoming, now)
        assertNotNull(nextSession)
        assertEquals("FP1", nextSession?.sessionName)
        assertEquals(2, nextSession?.roundNumber)
    }

    @Test
    fun v2CalendarEvent_findSelectedRaceForResult() {
        val race1 = createCalendarEvent(round = 1, raceDate = "2026-03-01", winnerName = "Max Verstappen")
        val race2 = createCalendarEvent(round = 2, fp1Date = "2026-03-13", fp1Time = "11:30:00Z")
        val calendar = listOf(race1, race2)

        val now = Instant.parse("2026-03-10T12:00:00Z")
        val selected = RaceStateEngine.findSelectedRaceForResult(calendar, now)

        assertEquals(1, selected?.round)
        assertEquals("Max Verstappen", selected?.winner?.fullName)
    }
}
