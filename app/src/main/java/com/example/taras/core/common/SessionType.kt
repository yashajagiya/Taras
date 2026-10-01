package com.example.taras.core.common

enum class SessionType(val displayName: String, val shortName: String) {
    FP1("FP1", "FP1"),
    FP2("FP2", "FP2"),
    FP3("FP3", "FP3"),
    SPRINT_QUALIFYING("Sprint Qualifying", "Sprint Q"),
    SPRINT_RACE("Sprint Race", "Sprint Race"),
    QUALIFYING("Qualifying", "Qualifying"),
    RACE("Race", "Race");

    companion object {
        fun fromString(value: String?): SessionType? {
            if (value == null) return null
            val clean = value.trim()
            return entries.firstOrNull {
                it.displayName.equals(clean, ignoreCase = true) ||
                it.shortName.equals(clean, ignoreCase = true) ||
                it.name.equals(clean, ignoreCase = true)
            }
        }
    }
}
