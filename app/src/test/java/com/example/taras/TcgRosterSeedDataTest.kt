package com.example.taras

import com.example.taras.core.db.tcg.TcgConverters
import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.seed.RosterSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TcgRosterSeedDataTest {

    @Test
    fun verifyTotalCardCountIs34() {
        val cards = RosterSeedData.allCards
        assertEquals("Total grid cards must equal exactly 34 (23 drivers + 11 chassis)", 34, cards.size)
    }

    @Test
    fun verifyDriversAndChassisCount() {
        val cards = RosterSeedData.allCards
        val drivers = cards.filter { it.role == CardRole.DRIVER }
        val chassis = cards.filter { it.role == CardRole.CHASSIS }

        assertEquals("Must have 23 driver cards", 23, drivers.size)
        assertEquals("Must have 11 constructor chassis cards", 11, chassis.size)
    }

    @Test
    fun verifyAll11TeamsRepresented() {
        val cards = RosterSeedData.allCards
        val teams = cards.map { it.teamId }.distinct()
        assertEquals("All 11 2026 constructors must be represented", 11, teams.size)
    }

    @Test
    fun verifyCardTiersCount() {
        val cards = RosterSeedData.allCards
        val sTier = cards.filter { it.tier == RarityTier.S_TIER }
        val aTier = cards.filter { it.tier == RarityTier.A_TIER }
        val bTier = cards.filter { it.tier == RarityTier.B_TIER }
        val cTier = cards.filter { it.tier == RarityTier.C_TIER }

        assertEquals(9, sTier.size)
        assertEquals(5, aTier.size)
        assertEquals(16, bTier.size)
        assertEquals(4, cTier.size)
    }

    @Test
    fun verifyCardMovesAndAttributes() {
        for (card in RosterSeedData.allCards) {
            assertTrue("Card ID must not be blank: ${card.id}", card.id.isNotBlank())
            assertTrue("Card name must not be blank: ${card.name}", card.name.isNotBlank())
            assertTrue("Move 1 title must not be blank for ${card.name}", card.move1.title.isNotBlank())
            assertTrue("Move 2 title must not be blank for ${card.name}", card.move2.title.isNotBlank())
            assertTrue("Move 1 power must be in range 70-99 for ${card.name}", card.move1.power in 70..99)
            assertTrue("Move 2 power must be in range 70-99 for ${card.name}", card.move2.power in 70..99)
            assertTrue("Weakness must not be blank for ${card.name}", card.weaknessText.isNotBlank())
            assertTrue("Resistance must not be blank for ${card.name}", card.resistanceText.isNotBlank())
            assertTrue("Vertical banner must not be blank for ${card.name}", card.verticalBannerText.isNotBlank())
            assertTrue("Primary color hex must be valid for ${card.name}", card.primaryColorHex.startsWith("#"))
            assertTrue("Secondary color hex must be valid for ${card.name}", card.secondaryColorHex.startsWith("#"))
        }
    }

    @Test
    fun testTcgConverters() {
        val converters = TcgConverters()
        val card = RosterSeedData.allCards.first()

        val move1Json = converters.fromCardMove(card.move1)
        assertNotNull(move1Json)
        val deserializedMove1 = converters.toCardMove(move1Json)
        assertEquals(card.move1.title, deserializedMove1?.title)
        assertEquals(card.move1.power, deserializedMove1?.power)

        val radarJson = converters.fromRadarStats(card.radarStats)
        assertNotNull(radarJson)
        val deserializedRadar = converters.toRadarStats(radarJson)
        assertEquals(card.radarStats.qualifyingPace, deserializedRadar?.qualifyingPace)
        assertEquals(card.radarStats.racecraft, deserializedRadar?.racecraft)
    }

    @Test
    fun verifyConstructorHistoricalDebutYears() {
        val chassisCards = RosterSeedData.allCards
            .filter { it.role == CardRole.CHASSIS }
            .associate { it.teamId to it.debutYear }

        assertEquals("1950", chassisCards["ferrari"])
        assertEquals("2005", chassisCards["redbull"])
        assertEquals("1966", chassisCards["mclaren"])
        assertEquals("1954", chassisCards["mercedes"])
        assertEquals("1959", chassisCards["astonmartin"])
        assertEquals("1978", chassisCards["williams"])
        assertEquals("2026", chassisCards["audi"])
        assertEquals("2021", chassisCards["alpine"])
        assertEquals("2026", chassisCards["cadillac"])
        assertEquals("2024", chassisCards["racingbulls"])
        assertEquals("2016", chassisCards["haas"])
    }
}
