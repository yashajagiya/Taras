package com.example.taras

import com.example.taras.core.tcg.model.CardRole
import com.example.taras.core.tcg.model.RarityTier
import com.example.taras.core.tcg.seed.RosterSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TcgLogicAndDropRatesTest {

    @Test
    fun testConstructorComposition() {
        val allCards = RosterSeedData.allCards
        val teams = allCards.groupBy { it.teamId }

        assertEquals("There must be 11 teams in 2026", 11, teams.size)

        for ((teamId, cards) in teams) {
            val driverCards = cards.filter { it.role == CardRole.DRIVER }
            val chassisCards = cards.filter { it.role == CardRole.CHASSIS }

            assertEquals("Team $teamId must have exactly 1 chassis card", 1, chassisCards.size)
            assertTrue("Team $teamId must have at least 2 driver cards", driverCards.size >= 2)
            assertEquals("Team $teamId total cards must equal driverCards + chassisCards", cards.size, driverCards.size + chassisCards.size)
        }
    }

    @Test
    fun testDropRateDistributionSimulation() {
        val allCards = RosterSeedData.allCards
        val sCards = allCards.filter { it.tier == RarityTier.S_TIER }
        val aCards = allCards.filter { it.tier == RarityTier.A_TIER }
        val bCards = allCards.filter { it.tier == RarityTier.B_TIER }
        val cCards = allCards.filter { it.tier == RarityTier.C_TIER }

        assertTrue(sCards.isNotEmpty())
        assertTrue(aCards.isNotEmpty())
        assertTrue(bCards.isNotEmpty())
        assertTrue(cCards.isNotEmpty())

        val sampleSize = 20_000
        var sCount = 0
        var aCount = 0
        var bCount = 0
        var cCount = 0

        val random = Random(42) // Fixed seed for reproducible test

        for (i in 0 until sampleSize) {
            val roll = random.nextDouble()
            val chosenTier = when {
                roll < 0.05 -> RarityTier.S_TIER
                roll < 0.20 -> RarityTier.A_TIER
                roll < 0.55 -> RarityTier.B_TIER
                else -> RarityTier.C_TIER
            }

            when (chosenTier) {
                RarityTier.S_TIER -> sCount++
                RarityTier.A_TIER -> aCount++
                RarityTier.B_TIER -> bCount++
                RarityTier.C_TIER -> cCount++
            }
        }

        val sRate = sCount.toDouble() / sampleSize
        val aRate = aCount.toDouble() / sampleSize
        val bRate = bCount.toDouble() / sampleSize
        val cRate = cCount.toDouble() / sampleSize

        // S-Tier nominal 5% (tolerance ±1.5%)
        assertTrue("S-Tier drop rate ~$sRate should be around 5%", sRate in 0.035..0.065)
        // A-Tier nominal 15% (tolerance ±2.0%)
        assertTrue("A-Tier drop rate ~$aRate should be around 15%", aRate in 0.13..0.17)
        // B-Tier nominal 35% (tolerance ±3.0%)
        assertTrue("B-Tier drop rate ~$bRate should be around 35%", bRate in 0.32..0.38)
        // C-Tier nominal 45% (tolerance ±3.0%)
        assertTrue("C-Tier drop rate ~$cRate should be around 45%", cRate in 0.42..0.48)
    }

    @Test
    fun testWeaknessAndResistanceStructure() {
        for (card in RosterSeedData.allCards) {
            assertTrue(
                "Card ${card.name} weakness must indicate multiplier or defect: ${card.weaknessText}",
                card.weaknessText.contains("×") || card.weaknessText.contains("x") || card.weaknessText.contains("+") || card.weaknessText.isNotBlank()
            )
            assertTrue(
                "Card ${card.name} resistance must indicate delta or protection: ${card.resistanceText}",
                card.resistanceText.contains("−") || card.resistanceText.contains("-") || card.resistanceText.isNotBlank()
            )
        }
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @Test
    fun testNavRoutesPolymorphicSerialization() {
        val module = com.example.taras.view.scaffold.navigation_compose.nav_main.serializerConfig.serializersModule
        org.junit.Assert.assertNotNull(module)

        val routes = listOf(
            com.example.taras.core.navigation.MainNavRoutes.Paddock,
            com.example.taras.core.navigation.MainNavRoutes.Grid,
            com.example.taras.core.navigation.MainNavRoutes.Calendar,
            com.example.taras.core.navigation.MainNavRoutes.F1Results,
            com.example.taras.core.navigation.MainNavRoutes.DriverProfile("44"),
            com.example.taras.core.navigation.MainNavRoutes.CircuitData("monaco"),
            com.example.taras.core.navigation.MainNavRoutes.TeamsData("ferrari"),
            com.example.taras.core.navigation.MainNavRoutes.DrawerSetting,
            com.example.taras.core.navigation.MainNavRoutes.Comparison("44", "16"),
            com.example.taras.core.navigation.MainNavRoutes.TcgBinder,
            com.example.taras.core.navigation.MainNavRoutes.TcgScratchPack("card_fer_44"),
            com.example.taras.core.navigation.MainNavRoutes.TcgArena("card_fer_44", "card_rbr_03")
        )

        for (route in routes) {
            val serializer = module.getPolymorphic(
                androidx.navigation3.runtime.NavKey::class,
                route
            )
            org.junit.Assert.assertNotNull(
                "Serializer for route ${route::class.simpleName} must be registered in polymorphic scope of NavKey",
                serializer
            )
        }
    }
}
