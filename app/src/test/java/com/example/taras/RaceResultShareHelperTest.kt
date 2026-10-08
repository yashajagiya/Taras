package com.example.taras

import com.example.taras.core.helpercore.RaceResultShareHelper
import com.example.taras.viewmodel.ResultRowData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RaceResultShareHelperTest {

    @Test
    fun testCleanRaceTitle() {
        val title1 = RaceResultShareHelper.cleanRaceTitle("British Grand Prix")
        assertTrue(title1.contains("British GP"))
        assertTrue(title1.contains("2026"))

        val title2 = RaceResultShareHelper.cleanRaceTitle("2026 British GP")
        assertEquals("2026 British GP", title2)
    }

    @Test
    fun testCleanSessionTitle() {
        assertEquals("Race Result", RaceResultShareHelper.cleanSessionTitle("Results"))
        assertEquals("Race Result", RaceResultShareHelper.cleanSessionTitle("Race"))
        assertEquals("Qualifying Result", RaceResultShareHelper.cleanSessionTitle("Qualifying"))
        assertEquals("FP1 Result", RaceResultShareHelper.cleanSessionTitle("FP1"))
        assertEquals("Sprint Race Result", RaceResultShareHelper.cleanSessionTitle("Sprint Race"))
        assertEquals("Sprint Qualifying Result", RaceResultShareHelper.cleanSessionTitle("Sprint Q"))
    }

    @Test
    fun testFormatDriverSurname() {
        assertEquals("Verstappen", RaceResultShareHelper.formatDriverSurname("Max Verstappen"))
        assertEquals("Hamilton", RaceResultShareHelper.formatDriverSurname("Lewis Hamilton"))
        assertEquals("Norris", RaceResultShareHelper.formatDriverSurname("Lando Norris"))
        assertEquals("Sainz Jr.", RaceResultShareHelper.formatDriverSurname("Carlos Sainz Jr."))
        assertEquals("de Vries", RaceResultShareHelper.formatDriverSurname("Nyck de Vries"))
        assertEquals("Verstappen", RaceResultShareHelper.formatDriverSurname("Verstappen"))
    }

    @Test
    fun testCleanTeamName() {
        assertEquals("Red Bull", RaceResultShareHelper.cleanTeamName("Oracle Red Bull Racing"))
        assertEquals("Ferrari", RaceResultShareHelper.cleanTeamName("Scuderia Ferrari"))
        assertEquals("McLaren", RaceResultShareHelper.cleanTeamName("McLaren F1 Team"))
        assertEquals("Mercedes", RaceResultShareHelper.cleanTeamName("Mercedes-AMG PETRONAS F1 Team"))
        assertEquals("Racing Bulls", RaceResultShareHelper.cleanTeamName("Visa Cash App RB"))
    }

    @Test
    fun testGenerateRaceResultSummaryMatchesUserTemplate() {
        val results = listOf(
            ResultRowData(
                position = "1",
                number = "1",
                driver = "Max Verstappen",
                team = "Oracle Red Bull Racing",
                extra = "1:28:45.123",
                laps = "52"
            ),
            ResultRowData(
                position = "2",
                number = "44",
                driver = "Lewis Hamilton",
                team = "Scuderia Ferrari",
                extra = "+3.456s",
                laps = "52"
            ),
            ResultRowData(
                position = "3",
                number = "4",
                driver = "Lando Norris",
                team = "McLaren F1 Team",
                extra = "+8.789s",
                laps = "52"
            )
        )

        val summary = RaceResultShareHelper.generateRaceResultSummary(
            raceName = "2026 British GP",
            sessionName = "Results",
            results = results,
            limit = 3
        )

        val expected = """
2026 British GP — Race Result
1. Verstappen (Red Bull) — 1:28:45.123
2. Hamilton (Ferrari) — +3.456s
3. Norris (McLaren) — +8.789s
via Taras app
""".trimIndent()

        assertEquals(expected, summary.trim())
    }
}
