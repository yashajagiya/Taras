package com.example.taras

import com.example.taras.core.common.UserPreferences
import com.example.taras.view.onboarding.ONBOARDING_DRIVERS
import com.example.taras.view.onboarding.ONBOARDING_TEAMS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTest {

    @Test
    fun testOnboardingDriversListIsValid() {
        assertTrue("Drivers list should have at least 10 drivers", ONBOARDING_DRIVERS.size >= 10)

        // Check key drivers exist
        val numbers = ONBOARDING_DRIVERS.map { it.number }.toSet()
        assertTrue("Max Verstappen #1 should be present", numbers.contains("1"))
        assertTrue("Lewis Hamilton #44 should be present", numbers.contains("44"))
        assertTrue("Lando Norris #4 should be present", numbers.contains("4"))
        assertTrue("Charles Leclerc #16 should be present", numbers.contains("16"))

        // Check data integrity
        for (driver in ONBOARDING_DRIVERS) {
            assertTrue("Driver name cannot be blank", driver.name.isNotBlank())
            assertTrue("Driver number cannot be blank", driver.number.isNotBlank())
            assertTrue("Driver team cannot be blank", driver.team.isNotBlank())
            assertNotNull("Driver teamColor cannot be null", driver.teamColor)
        }
    }

    @Test
    fun testOnboardingTeamsListContainsAllTenConstructors() {
        assertEquals("There should be exactly 10 F1 constructors", 10, ONBOARDING_TEAMS.size)

        val teamShortNames = ONBOARDING_TEAMS.map { it.shortName }.toSet()
        val expectedTeams = setOf(
            "Red Bull",
            "Ferrari",
            "McLaren",
            "Mercedes",
            "Aston Martin",
            "Alpine",
            "Williams",
            "Racing Bulls",
            "Kick Sauber",
            "Haas"
        )

        assertEquals(expectedTeams, teamShortNames)

        for (team in ONBOARDING_TEAMS) {
            assertTrue("Team name cannot be blank", team.name.isNotBlank())
            assertTrue("Short name cannot be blank", team.shortName.isNotBlank())
            assertNotNull("Team color cannot be null", team.color)
        }
    }

    @Test
    fun testUserPreferencesKeysExist() {
        assertNotNull(UserPreferences.USERNAME_KEY)
        assertNotNull(UserPreferences.FAVORITE_DRIVER_NUMBER_KEY)
        assertNotNull(UserPreferences.FAVORITE_DRIVER_NAME_KEY)
        assertNotNull(UserPreferences.FAVORITE_TEAM_KEY)
        assertNotNull(UserPreferences.HAS_SEEN_WELCOME_KEY)
        assertNotNull(UserPreferences.NOTIFICATIONS_ENABLED_KEY)

        assertEquals("username", UserPreferences.USERNAME_KEY.name)
    }

    @Test
    fun testAppearanceKeysAndWidgetDefaults() {
        assertNotNull(com.example.taras.core.common.OfflineDataStoreAppearance.APPEARANCE)
        assertNotNull(com.example.taras.core.common.OfflineDataStoreAppearance.WIDGET_THEME)

        assertEquals("appearance", com.example.taras.core.common.OfflineDataStoreAppearance.APPEARANCE.name)
        assertEquals("widget_theme", com.example.taras.core.common.OfflineDataStoreAppearance.WIDGET_THEME.name)

        val ferrariColor = com.example.taras.core.widgets.getTeamColor("Ferrari")
        assertNotNull(ferrariColor)
        val defaultColor = com.example.taras.core.widgets.getTeamColor(null)
        assertNotNull(defaultColor)
    }
}
