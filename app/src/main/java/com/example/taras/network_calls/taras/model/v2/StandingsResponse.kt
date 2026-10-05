package com.example.taras.network_calls.taras.model.v2

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class StandingsResponse(
    val season: Int = 0,
    val drivers: List<DriverStandingEntry> = emptyList(),
    val teams: List<TeamStandingEntry> = emptyList()
)

@Immutable
@Serializable
data class DriverStandingEntry(
    val rank: Int = 0,
    @SerialName("driver_id") val driverId: String = "",
    @SerialName("driver_number") val driverNumber: Int? = null,
    val name: String = "",
    @SerialName("short_name") val shortName: String = "",
    val code: String = "",
    @SerialName("team_name") val teamName: String = "",
    val nationality: String = "",
    val points: Int = 0,
    val races: List<RaceStandingBreakdown> = emptyList()
)

@Immutable
@Serializable
data class TeamStandingEntry(
    val rank: Int = 0,
    @SerialName("team_id") val teamId: String = "",
    @SerialName("team_name") val teamName: String = "",
    val points: Int = 0,
    val races: List<RaceStandingBreakdown> = emptyList()
)

@Immutable
@Serializable
data class RaceStandingBreakdown(
    val round: Int = 0,
    @SerialName("race_code") val raceCode: String = "",
    @SerialName("race_name") val raceName: String = "",
    val played: Boolean = false,
    val points: Int = 0,
    @SerialName("display_value") val displayValue: String = "0"
)
