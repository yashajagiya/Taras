package com.example.taras

import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.v2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class ApiV2DeserializationTest {

    private fun fetchJson(urlStr: String): String {
        val conn = URL(urlStr).openConnection()
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        return conn.getInputStream().bufferedReader().use { it.readText() }
    }

    @Test
    fun testOverviewDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/overview.json")
        val overview = NetworkModule.json.decodeFromString<OverviewResponse>(jsonStr)
        assertEquals(2026, overview.season)
        assertEquals(16, overview.roundCurrent)
        assertNotNull(overview.championshipLeader)
        assertNotNull(overview.championshipLeader?.driver)
        assertNotNull(overview.nextEvent)
        assertNotNull(overview.latestRace)
        assertTrue(overview.latestRace?.podium?.isNotEmpty() == true)
    }

    @Test
    fun testCalendarDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/calendar.json")
        val calendar = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(jsonStr)
        assertEquals(23, calendar.size)
        val round1 = calendar[0]
        assertEquals(1, round1.round)
        assertEquals("australian_2026", round1.id)
        assertNotNull(round1.circuit)
        assertNotNull(round1.schedule)
    }

    @Test
    fun testStandingsDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/standings.json")
        val standings = NetworkModule.json.decodeFromString<StandingsResponse>(jsonStr)
        assertEquals(2026, standings.season)
        assertTrue(standings.drivers.isNotEmpty())
        assertTrue(standings.teams.isNotEmpty())
        assertEquals(1, standings.drivers[0].rank)
        assertEquals(1, standings.teams[0].rank)
    }

    @Test
    fun testDriversDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/drivers.json")
        val drivers = NetworkModule.json.decodeFromString<List<DriverDetailResponseV2>>(jsonStr)
        assertTrue(drivers.isNotEmpty())
        val driver1 = drivers[0]
        assertNotNull(driver1.name)
        assertNotNull(driver1.team)
        assertNotNull(driver1.images)
        assertNotNull(driver1.biography)
        assertTrue(driver1.biography.paragraphs.isNotEmpty())
    }

    @Test
    fun testTeamsDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/teams.json")
        val teams = NetworkModule.json.decodeFromString<List<TeamDetailResponseV2>>(jsonStr)
        assertTrue(teams.isNotEmpty())
        val team1 = teams[0]
        assertNotNull(team1.name)
        assertNotNull(team1.colors)
        assertNotNull(team1.images)
        assertTrue(team1.drivers.isNotEmpty())
    }

    @Test
    fun testLatestResultsDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/results/latest.json")
        val result = NetworkModule.json.decodeFromString<WeekendResultsResponse>(jsonStr)
        assertEquals(16, result.round)
        assertNotNull(result.sessions.practice1)
        assertNotNull(result.sessions.qualifying)
        assertNotNull(result.sessions.race)
        assertTrue(result.sessions.race?.isNotEmpty() == true)
    }

    @Test
    fun testSprintResultsDeserialization() {
        val jsonStr = fetchJson("https://yashajagiya.github.io/tarasF1Data/v2/results/round_2.json")
        val result = NetworkModule.json.decodeFromString<WeekendResultsResponse>(jsonStr)
        assertEquals(2, result.round)
        assertTrue(result.hasSprint)
        assertNotNull(result.sessions.sprintQualifying)
        assertNotNull(result.sessions.sprintRace)
    }

    @Test
    fun testAllDataClassesNullSafetyWithEmptyJson() {
        val emptyJson = "{}"
        
        val overview = NetworkModule.json.decodeFromString<OverviewResponse>(emptyJson)
        assertEquals(0, overview.season)
        assertEquals(null, overview.championshipLeader)

        val calendarEvent = NetworkModule.json.decodeFromString<CalendarRaceEvent>(emptyJson)
        assertEquals(0, calendarEvent.round)
        assertEquals("", calendarEvent.name)
        assertEquals(null, calendarEvent.winner)

        val standings = NetworkModule.json.decodeFromString<StandingsResponse>(emptyJson)
        assertEquals(0, standings.season)
        assertTrue(standings.drivers.isEmpty())

        val driver = NetworkModule.json.decodeFromString<DriverDetailResponseV2>(emptyJson)
        assertEquals("", driver.name)
        assertEquals(null, driver.number)

        val team = NetworkModule.json.decodeFromString<TeamDetailResponseV2>(emptyJson)
        assertEquals("", team.name)
        assertTrue(team.drivers.isEmpty())

        val weekend = NetworkModule.json.decodeFromString<WeekendResultsResponse>(emptyJson)
        assertEquals(0, weekend.round)
        assertEquals(null, weekend.sessions.race)
    }

    @Test
    fun testAllDataClassesWithExplicitNulls() {
        val nullOverviewJson = """
            {
                "season": null,
                "round_current": null,
                "round_total": null,
                "championship_leader": null,
                "next_event": null,
                "latest_race": null
            }
        """.trimIndent()
        val overview = NetworkModule.json.decodeFromString<OverviewResponse>(nullOverviewJson)
        assertEquals(0, overview.season)
        assertEquals(null, overview.championshipLeader)

        val nullCalendarJson = """
            {
                "round": null,
                "id": null,
                "name": null,
                "has_sprint": null,
                "weekend_format": null,
                "laps": null,
                "circuit": null,
                "schedule": null,
                "winner": null
            }
        """.trimIndent()
        val calendarEvent = NetworkModule.json.decodeFromString<CalendarRaceEvent>(nullCalendarJson)
        assertEquals(0, calendarEvent.round)
        assertEquals("", calendarEvent.name)

        val nullStandingsJson = """
            {
                "season": null,
                "drivers": null,
                "teams": null
            }
        """.trimIndent()
        val standings = NetworkModule.json.decodeFromString<StandingsResponse>(nullStandingsJson)
        assertEquals(0, standings.season)
        assertTrue(standings.drivers.isEmpty())

        val nullResultsJson = """
            {
                "round": null,
                "race_name": null,
                "sessions": {
                    "practice_1": null,
                    "qualifying": [
                        {
                            "position": null,
                            "driver_number": null,
                            "driver_name": null,
                            "team": null,
                            "q1": null,
                            "q2": null,
                            "q3": null,
                            "laps": null
                        }
                    ],
                    "race": [
                        {
                            "position": "NC",
                            "driver_number": null,
                            "driver_name": null,
                            "team": null,
                            "laps": null,
                            "time_or_retired": null,
                            "points": null
                        }
                    ]
                }
            }
        """.trimIndent()
        val results = NetworkModule.json.decodeFromString<WeekendResultsResponse>(nullResultsJson)
        assertEquals(0, results.round)
        assertEquals("", results.raceName)
        val quali = results.sessions.qualifying
        assertNotNull(quali)
        assertEquals("0", quali!![0].position)
        assertEquals("", quali[0].q3)

        val race = results.sessions.race
        assertNotNull(race)
        assertEquals("NC", race!![0].position)
        assertEquals("", race[0].timeOrRetired)
    }
}
