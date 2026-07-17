package com.example.taras.api.taras.model

data class TeamsPerRaceResponse(
    val displayName: String,
    val season: String,
    val entries: List<TeamsEntry>,
)

data class TeamsEntry(
    val rank: Int,
    val team: String,
    val points: Points,
    val races: List<Racedata>,
)

data class Points(
    val value: Int,
    val displayValue: String,
)

data class Racedata(
    val name: String,
    val displayName: String,
    val played: Boolean,
    val value: Int,
    val displayValue: String,
)
