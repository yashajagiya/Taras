package com.example.taras

import androidx.compose.ui.graphics.Color
import com.example.taras.core.widgets.CountdownMatrixWidgetUtils
import com.example.taras.core.widgets.SeasonRoundItem
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class CountdownMatrixWidgetTest {

    @Test
    fun computeCountdownDhm_withFutureTime_returnsPaddedDhm() {
        val now = Instant.parse("2026-05-20T10:00:00Z")
        val target = now + 5.days + 19.hours + 31.minutes

        val (days, hours, minutes) = CountdownMatrixWidgetUtils.computeCountdownDhm(target, now)

        assertEquals("05", days)
        assertEquals("19", hours)
        assertEquals("31", minutes)
    }

    @Test
    fun computeCountdownDhm_withPastTime_returnsZeroes() {
        val now = Instant.parse("2026-05-20T10:00:00Z")
        val target = now - 1.hours

        val (days, hours, minutes) = CountdownMatrixWidgetUtils.computeCountdownDhm(target, now)

        assertEquals("00", days)
        assertEquals("00", hours)
        assertEquals("00", minutes)
    }

    @Test
    fun computeCountdownDhm_withNullTarget_returnsDashes() {
        val (days, hours, minutes) = CountdownMatrixWidgetUtils.computeCountdownDhm(null)

        assertEquals("--", days)
        assertEquals("--", hours)
        assertEquals("--", minutes)
    }

    @Test
    fun formatLocalSessionDate_and_Time_withSpecificTimeZone_formatsCorrectly() {
        val instant = Instant.parse("2026-05-24T15:00:00Z")
        val utcTz = TimeZone.UTC

        val dateStr = CountdownMatrixWidgetUtils.formatLocalSessionDate(instant, utcTz)
        val timeStr = CountdownMatrixWidgetUtils.formatLocalSessionTime(instant, utcTz)

        assertEquals("SUN, 24 MAY", dateStr)
        assertEquals("15:00", timeStr)
    }

    @Test
    fun formatLocalSessionDate_and_Time_withNullInstant_returnsFallback() {
        val dateStr = CountdownMatrixWidgetUtils.formatLocalSessionDate(null)
        val timeStr = CountdownMatrixWidgetUtils.formatLocalSessionTime(null)

        assertEquals("TBD", dateStr)
        assertEquals("--:--", timeStr)
    }

    @Test
    fun bundledSeedCalendar_existsAndDeserializesProperly() {
        val assetFile = File("src/main/assets/default_season_calendar.json")
        assertTrue("Asset file must exist", assetFile.exists())

        val jsonStr = assetFile.readText()
        val calendar = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(jsonStr)

        assertTrue("Calendar must have at least 20 rounds", calendar.size >= 20)
        val firstRace = calendar[0]
        assertEquals(1, firstRace.round)
        assertNotNull(firstRace.circuit)
        assertNotNull(firstRace.schedule)
    }

    @Test
    fun seasonRoundItem_mapping_isConsistent() {
        val rounds = listOf(
            SeasonRoundItem(roundNumber = 1, winnerNumber = "63", teamColor = Color(0xFF27F4D2)),
            SeasonRoundItem(roundNumber = 2, winnerNumber = "1", teamColor = Color(0xFF3671C6)),
            SeasonRoundItem(roundNumber = 3, winnerNumber = null, teamColor = Color.Gray, isCurrentOrNext = true)
        )

        assertEquals(3, rounds.size)
        assertEquals("63", rounds[0].winnerNumber)
        assertEquals(1, rounds[0].roundNumber)
        assertTrue(rounds[2].isCurrentOrNext)
    }

    @Test
    fun memoryLruCache_handlesPutGetAndEviction() {
        val cache = CountdownMatrixWidgetUtils.MemoryLruCache<String, String>(
            maxByteSize = 3,
            sizeOf = { _, _ -> 1 }
        )

        cache.put("k1", "v1")
        cache.put("k2", "v2")
        cache.put("k3", "v3")

        assertEquals("v1", cache.get("k1"))
        assertEquals(3, cache.size())

        // Adding 4th element should evict the oldest
        cache.put("k4", "v4")
        assertEquals(3, cache.size())
        assertEquals("v4", cache.get("k4"))

        cache.clear()
        assertEquals(0, cache.size())
    }
}

