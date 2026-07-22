package com.example.taras.network_calls.f1apidev.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CircuitResponse(
    val api: String,
    val championship: Championship,
    val limit: Int,
    val offset: Int,
    val races: List<CircuitRace>,
    val season: Int,
    val total: Int,
    val url: String
)

@Serializable
data class Championship(
    val id: String,
    val name: String
)

@Serializable
data class CircuitRace(
    val championshipId: String,
    val circuit: Circuit,
    @SerialName("fast_lap") val fastLap: FastLap,
    val laps: Int,
    val raceId: String,
    val raceName: String,
    val round: Int,
    val schedule: Schedule,
    val teamWinner: TeamWinner?,
    val url: String,
    val winner: Winner?
)

@Serializable
data class Circuit(
    val id: String,
    val name: String,
    val url: String
)

@Serializable
data class FastLap(
    val driver: String,
    val lap: String,
    val time: String
)

@Serializable
data class Schedule(
    val fp1: String,
    val fp2: String,
    val fp3: String?,
    val qualy: String,
    val race: String,
    val sprintQualy: String?,
    val sprintRace: String?
)

@Serializable
data class TeamWinner(
    val id: String,
    val name: String,
    val url: String
)

@Serializable
data class Winner(
    val id: String,
    val name: String,
    val url: String
)
