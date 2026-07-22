package com.example.taras.network_calls.taras.model

import kotlinx.serialization.Serializable

@Serializable
data class TeamsPerRaceResponse(
    val displayName: String,
    val season: String,
    val entries: List<TeamsEntry>,
)

@Serializable
data class TeamsEntry(
    val rank: Int,
    val team: String,
    val points: Points,
    val races: List<Racedata>,
)

@Serializable
data class Points(
    val value: Int,
    val displayValue: String,
)

@Serializable
data class Racedata(
    val name: String,
    val displayName: String,
    val played: Boolean,
    val value: Int,
    val displayValue: String,
)
