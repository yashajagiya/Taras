package com.example.taras.network_calls.taras.model

import kotlinx.serialization.Serializable

@Serializable
data class F1RacesInfoResponse(
    val season: Int,
    val championship: Championship,
    val totalRaces: Int,
    val races: List<RaceEvent>
)

@Serializable
data class Championship(
    val championshipId: String,
    val championshipName: String,
    val year: Int
)

@Serializable
data class RaceEvent(
    val raceId: String,
    val raceName: String,
    val round: Int,
    val laps: Int? = null,
    val schedule: Schedule,
    val circuit: Circuit,
    val winner: Winner? = null
)

@Serializable
data class Schedule(
    val race: Session,
    val qualy: Session,
    val fp1: Session,
    val fp2: Session? = null,
    val fp3: Session? = null,
    val sprintQualy: Session? = null,
    val sprintRace: Session? = null
)

// Reusable data class for all schedule sessions
@Serializable
data class Session(
    val date: String,
    val time: String
)

@Serializable
data class Circuit(
    val circuitId: String,
    val circuitName: String,
    val country: String,
    val city: String,
    val circuitLength: String,
    val lapRecord: String? = null,
    val firstParticipationYear: Int,
    val corners: Int,
    val fastestLapDriverId: String? = null,
    val fastestLapTeamId: String? = null,
    val fastestLapYear: Int? = null,
    val trackImage: String,
    val gpName: String
)

@Serializable
data class Winner(
    val drivernumber: Int,
    val fullName: String,
    val teamWinner: String
)