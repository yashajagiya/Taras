package com.example.taras

import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse
import org.junit.Assert.*
import org.junit.Test

class CacheMigrationTest {

    @Test
    fun testCorruptCacheDoesNotCrashApp() {
        val corruptJson = "{ invalid json content }"
        val result = try {
            NetworkModule.json.decodeFromString<OverviewResponse>(corruptJson)
        } catch (_: Exception) {
            null
        }
        assertNull(result)
    }

    @Test
    fun testLegacyV1JsonAgainstV2ModelHandledGracefully() {
        // Legacy v1 races JSON was an object, whereas v2 is an array
        val legacyV1RacesJson = """{"season":2026,"totalRaces":24,"races":[]}"""
        val result = try {
            NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(legacyV1RacesJson)
        } catch (_: Exception) {
            null
        }
        assertNull("Legacy JSON should fail safely and return null", result)
    }

    @Test
    fun testValidV2CacheDecodesSuccessfully() {
        val validV2Json = """
            [
              {
                "round": 1,
                "id": "australian_2026",
                "name": "Australian Grand Prix",
                "has_sprint": false,
                "weekend_format": "conventional"
              }
            ]
        """.trimIndent()
        val calendar = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(validV2Json)
        assertEquals(1, calendar.size)
        assertEquals(1, calendar[0].round)
        assertEquals("australian_2026", calendar[0].id)
        assertFalse(calendar[0].hasSprint)
    }

    @Test
    fun testValidV2OverviewCacheDecodesSuccessfully() {
        val validOverviewJson = """
            {
              "season": 2026,
              "round_current": 16,
              "round_total": 23
            }
        """.trimIndent()
        val overview = NetworkModule.json.decodeFromString<OverviewResponse>(validOverviewJson)
        assertEquals(2026, overview.season)
        assertEquals(16, overview.roundCurrent)
        assertEquals(23, overview.roundTotal)
    }
}
