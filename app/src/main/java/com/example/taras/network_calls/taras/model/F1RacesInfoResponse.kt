package com.example.taras.network_calls.taras.model

import kotlinx.serialization.Serializable

@Serializable
data class F1RacesInfoResponse(
    val season: Int = 0,
    val championship: Championship = Championship(),
    val totalRaces: Int = 0,
    val races: List<RaceEvent> = emptyList()
)

@Serializable
data class Championship(
    val championshipId: String = "",
    val championshipName: String = "",
    val year: Int = 0
)

@Serializable
data class RaceEvent(
    val raceId: String = "",
    val raceName: String = "",
    val round: Int = 0,
    val laps: Int? = null,
    val schedule: Schedule = Schedule(),
    val circuit: Circuit = Circuit(),
    val winner: Winner? = null
)

@Serializable
data class Schedule(
    val race: Session? = null,
    val qualy: Session? = null,
    val fp1: Session? = null,
    val fp2: Session? = null,
    val fp3: Session? = null,
    val sprintQualy: Session? = null,
    val sprintRace: Session? = null
)

// Reusable data class for all schedule sessions
@Serializable
data class Session(
    val date: String? = null,
    val time: String? = null
)

@Serializable
data class Circuit(
    val circuitId: String = "",
    val circuitName: String = "",
    val country: String = "",
    val city: String = "",
    val circuitLength: String = "",
    val lapRecord: String? = null,
    val firstParticipationYear: Int? = null,
    val corners: Int? = null,
    val fastestLapDriverId: String? = null,
    val fastestLapTeamId: String? = null,
    val fastestLapYear: Int? = null,
    val trackImage: String? = null,
    val gpName: String = ""
)

@Serializable
data class Winner(
    val drivernumber: Int? = null,
    val fullName: String? = null,
    val teamWinner: String? = null
)