package com.example.taras.network_calls.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverPerRaceResponce(
    val displayName: String,
    val season: String,
    val entries: List<DriverEntry>,
)

@Serializable
data class DriverEntry(
    val rank: Int,
    @SerialName("driver_number")
    val driverNumber: Int,
    val name: String,
    val shortName: String,
    val abbreviation: String,
    @SerialName("team_name")
    val teamName: String,
    val nationality: String,
    val championshipPts: ChampionshipPts,
    val races: List<DriverPerRace>,
)

@Serializable
data class ChampionshipPts(
    val value: Int,
    val displayValue: String,
)

@Serializable
data class DriverPerRace(
    val name: String,
    val displayName: String,
    val played: Boolean,
    val value: Int,
    val displayValue: String,
)
