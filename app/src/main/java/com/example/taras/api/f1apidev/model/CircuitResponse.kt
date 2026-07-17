package com.example.taras.api.f1apidev.model

import com.google.gson.annotations.SerializedName

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

data class Championship(
    val id: String,
    val name: String
)

data class CircuitRace(
    val championshipId: String,
    val circuit: Circuit,
    @SerializedName("fast_lap") val fastLap: FastLap,
    val laps: Int,
    val raceId: String,
    val raceName: String,
    val round: Int,
    val schedule: Schedule,
    val teamWinner: TeamWinner?,
    val url: String,
    val winner: Winner?
)

data class Circuit(
    val id: String,
    val name: String,
    val url: String
)

data class FastLap(
    val driver: String,
    val lap: String,
    val time: String
)

data class Schedule(
    val fp1: String,
    val fp2: String,
    val fp3: String?,
    val qualy: String,
    val race: String,
    val sprintQualy: String?,
    val sprintRace: String?
)

data class TeamWinner(
    val id: String,
    val name: String,
    val url: String
)

data class Winner(
    val id: String,
    val name: String,
    val url: String
)
