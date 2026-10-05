package com.example.taras.network_calls.taras.model.v2

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class CalendarRaceEvent(
    val round: Int = 0,
    val id: String = "",
    val name: String = "",
    @SerialName("has_sprint") val hasSprint: Boolean = false,
    @SerialName("weekend_format") val weekendFormat: String = "conventional",
    val laps: Int? = null,
    val circuit: CircuitV2 = CircuitV2(),
    val schedule: ScheduleV2 = ScheduleV2(),
    val winner: WinnerV2? = null
)

@Immutable
@Serializable
data class CircuitV2(
    val id: String = "",
    val name: String = "",
    @SerialName("gp_name") val gpName: String = "",
    val country: String = "",
    val city: String = "",
    val length: String = "",
    @SerialName("lap_record") val lapRecord: String? = null,
    val corners: Int? = null,
    @SerialName("first_participation_year") val firstParticipationYear: Int? = null,
    @SerialName("fastest_lap_driver") val fastestLapDriver: String? = null,
    @SerialName("fastest_lap_team") val fastestLapTeam: String? = null,
    @SerialName("fastest_lap_year") val fastestLapYear: Int? = null,
    @SerialName("track_image") val trackImage: String? = null
)

@Immutable
@Serializable
data class ScheduleV2(
    val race: SessionTimeV2? = null,
    val qualy: SessionTimeV2? = null,
    val qualifying: SessionTimeV2? = null,
    val fp1: SessionTimeV2? = null,
    val fp2: SessionTimeV2? = null,
    val fp3: SessionTimeV2? = null,
    @SerialName("sprint_qualifying") val sprintQualifying: SessionTimeV2? = null,
    @SerialName("sprint_race") val sprintRace: SessionTimeV2? = null
)

@Immutable
@Serializable
data class SessionTimeV2(
    val date: String? = null,
    val time: String? = null
)

@Immutable
@Serializable
data class WinnerV2(
    val drivernumber: Int? = null,
    val fullName: String? = null,
    val teamWinner: String? = null
)
