package com.example.taras

import com.example.taras.core.common.SessionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionTypeTest {

    @Test
    fun fromString_matchesDisplayName() {
        assertEquals(SessionType.FP1, SessionType.fromString("FP1"))
        assertEquals(SessionType.FP2, SessionType.fromString("FP2"))
        assertEquals(SessionType.FP3, SessionType.fromString("FP3"))
        assertEquals(SessionType.QUALIFYING, SessionType.fromString("Qualifying"))
        assertEquals(SessionType.RACE, SessionType.fromString("Race"))
        assertEquals(SessionType.SPRINT_QUALIFYING, SessionType.fromString("Sprint Qualifying"))
        assertEquals(SessionType.SPRINT_RACE, SessionType.fromString("Sprint Race"))
    }

    @Test
    fun fromString_matchesShortName() {
        assertEquals(SessionType.SPRINT_QUALIFYING, SessionType.fromString("Sprint Q"))
    }

    @Test
    fun fromString_isCaseInsensitiveAndTrimsWhitespace() {
        assertEquals(SessionType.RACE, SessionType.fromString("  race  "))
        assertEquals(SessionType.QUALIFYING, SessionType.fromString("qualifying"))
        assertEquals(SessionType.SPRINT_QUALIFYING, SessionType.fromString("sprint qualifying"))
    }

    @Test
    fun fromString_returnsNullForInvalidOrNull() {
        assertNull(SessionType.fromString(null))
        assertNull(SessionType.fromString("Unknown Session"))
        assertNull(SessionType.fromString(""))
    }
}
