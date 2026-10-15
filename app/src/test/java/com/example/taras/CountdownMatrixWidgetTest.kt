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

    @Test
    fun computeCountdownDh_removesMinutesAndReturnsDaysAndHours() {
        val now = Instant.parse("2026-05-20T10:00:00Z")
        val target = now + 5.days + 19.hours + 31.minutes

        val (days, hours) = CountdownMatrixWidgetUtils.computeCountdownDh(target, now)

        assertEquals("05", days)
        assertEquals("19", hours)
    }

    @Test
    fun getShortGpTitle_formatsProperly() {
        assertEquals("BELGIAN", CountdownMatrixWidgetUtils.getShortGpTitle("Formula 1 Rolex Belgian Grand Prix 2026", "Belgium"))
        assertEquals("UNITED STATES", CountdownMatrixWidgetUtils.getShortGpTitle("Formula 1 MSC Cruises United States Grand Prix", "United States"))
        assertEquals("ITALIAN", CountdownMatrixWidgetUtils.getShortGpTitle("Formula 1 Pirelli Gran Premio d'Italia", "Italy"))
        assertEquals("MONACO", CountdownMatrixWidgetUtils.getShortGpTitle("Formula 1 Grand Prix de Monaco", "Monaco"))
    }

    @Test
    fun getShortCircuitName_returnsConciseNames() {
        assertEquals("Spa", CountdownMatrixWidgetUtils.getShortCircuitName("Circuit de Spa-Francorchamps"))
        assertEquals("COTA", CountdownMatrixWidgetUtils.getShortCircuitName("Circuit of The Americas"))
        assertEquals("Monza", CountdownMatrixWidgetUtils.getShortCircuitName("Autodromo Nazionale Monza"))
        assertEquals("Silverstone", CountdownMatrixWidgetUtils.getShortCircuitName("Silverstone Circuit"))
    }

    @Test
    fun formatShortSessionDate_formatsMonthAndDay() {
        val instant = Instant.parse("2026-07-17T11:30:00Z")
        val formatted = CountdownMatrixWidgetUtils.formatShortSessionDate(instant, TimeZone.UTC)
        assertEquals("Jul 17", formatted)
    }

    @Test
    fun dynamicCalendarRoundCount_and_SprintAndLaps_areAccurate() {
        val assetFile = File("src/main/assets/default_season_calendar.json")
        val jsonStr = assetFile.readText()
        val calendar = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(jsonStr)

        val totalRounds = calendar.maxOfOrNull { it.round }?.coerceAtLeast(1)
            ?: calendar.size.takeIf { it > 0 }
            ?: 24

        // 2026 calendar in assets has 23 dynamic rounds
        assertEquals(23, totalRounds)

        val round1 = calendar.find { it.round == 1 }
        assertNotNull(round1)
        assertEquals(58, round1?.laps)
        assertEquals(false, round1?.hasSprint)

        val round2 = calendar.find { it.round == 2 }
        assertNotNull(round2)
        assertEquals(true, round2?.hasSprint)
    }

    @Test
    fun circuitPathVector_and_normalizedKey_arePresentAndValid() {
        val spaKey = CountdownMatrixWidgetUtils.getNormalizedCircuitKey("Circuit de Spa-Francorchamps")
        assertEquals("spa", spaKey)
        val spaPath = CountdownMatrixWidgetUtils.circuitPaths[spaKey]
        assertNotNull(spaPath)
        assertTrue(spaPath!!.startsWith("M "))

        val monzaKey = CountdownMatrixWidgetUtils.getNormalizedCircuitKey("Autodromo Nazionale Monza")
        assertEquals("monza", monzaKey)

        val cotaKey = CountdownMatrixWidgetUtils.getNormalizedCircuitKey("Circuit of The Americas")
        assertEquals("cota", cotaKey)
    }

    @Test
    fun computeGantryLights_reflectsProximityAndLiveStatus() {
        val now = Instant.parse("2026-05-20T10:00:00Z")

        // 1. Live status should always illuminate all 5 lights
        assertEquals(5, CountdownMatrixWidgetUtils.computeGantryLights(now + 10.hours, isLive = true, now = now))

        // 2. More than 72 hours away -> 1 light
        assertEquals(1, CountdownMatrixWidgetUtils.computeGantryLights(now + 80.hours, isLive = false, now = now))

        // 3. Between 24h and 72h away -> 2 lights
        assertEquals(2, CountdownMatrixWidgetUtils.computeGantryLights(now + 48.hours, isLive = false, now = now))

        // 4. Between 6h and 24h away -> 3 lights
        assertEquals(3, CountdownMatrixWidgetUtils.computeGantryLights(now + 12.hours, isLive = false, now = now))

        // 5. Between 1h and 6h away -> 4 lights
        assertEquals(4, CountdownMatrixWidgetUtils.computeGantryLights(now + 3.hours, isLive = false, now = now))

        // 6. Within 1 hour -> 5 lights (Pre-session buildup)
        assertEquals(5, CountdownMatrixWidgetUtils.computeGantryLights(now + 30.minutes, isLive = false, now = now))
    }

    @Test
    fun computeWeekendTimeline_formatsConventionalAndSprintAccurately() {
        val now = Instant.parse("2026-05-20T10:00:00Z")

        // Conventional weekend with Practice 3 current
        val conventional = CountdownMatrixWidgetUtils.computeWeekendTimeline(
            sessions = null,
            currentSessionName = "Practice 3",
            targetInstant = now + 4.hours,
            isLive = false,
            hasSprint = false,
            now = now
        )

        assertEquals(5, conventional.size)
        assertEquals("FP1", conventional[0].shortCode)
        assertTrue(conventional[0].isCompleted)
        assertEquals("FP2", conventional[1].shortCode)
        assertTrue(conventional[1].isCompleted)
        assertEquals("FP3", conventional[2].shortCode)
        assertTrue(conventional[2].isCurrent)
        assertEquals("Q", conventional[3].shortCode)
        assertTrue(conventional[3].isUpcoming)
        assertEquals("GP", conventional[4].shortCode)
        assertTrue(conventional[4].isUpcoming)

        // Sprint weekend with Sprint Race current
        val sprint = CountdownMatrixWidgetUtils.computeWeekendTimeline(
            sessions = null,
            currentSessionName = "Sprint",
            targetInstant = now + 2.hours,
            isLive = false,
            hasSprint = true,
            now = now
        )

        assertEquals(5, sprint.size)
        assertEquals("FP1", sprint[0].shortCode)
        assertTrue(sprint[0].isCompleted)
        assertEquals("SQ", sprint[1].shortCode)
        assertTrue(sprint[1].isCompleted)
        assertEquals("SPR", sprint[2].shortCode)
        assertTrue(sprint[2].isCurrent)
        assertEquals("Q", sprint[3].shortCode)
        assertTrue(sprint[3].isUpcoming)
        assertEquals("GP", sprint[4].shortCode)
        assertTrue(sprint[4].isUpcoming)
    }

    @Test
    fun getDisplayCountryName_formatsCountryAndFallbackAccurately() {
        assertEquals("BELGIUM", CountdownMatrixWidgetUtils.getDisplayCountryName("Belgium", "Belgian GP"))
        assertEquals("ABU DHABI", CountdownMatrixWidgetUtils.getDisplayCountryName("United Arab Emirates", "Abu Dhabi GP"))
        assertEquals("ABU DHABI", CountdownMatrixWidgetUtils.getDisplayCountryName("UAE", "Abu Dhabi GP"))
        assertEquals("ITALY", CountdownMatrixWidgetUtils.getDisplayCountryName("Italy", "Italian GP"))
        assertEquals("AUSTRALIA", CountdownMatrixWidgetUtils.getDisplayCountryName("Australia", "Australian GP"))
        assertEquals("BELGIAN", CountdownMatrixWidgetUtils.getDisplayCountryName(null, "Belgian GP"))
        assertEquals("MONACO", CountdownMatrixWidgetUtils.getDisplayCountryName("", "Monaco Grand Prix"))
        assertEquals("GRAND PRIX", CountdownMatrixWidgetUtils.getDisplayCountryName(null, null))
    }
}



