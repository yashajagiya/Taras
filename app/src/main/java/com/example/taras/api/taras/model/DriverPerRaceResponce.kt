package com.example.taras.api.taras.model

import com.google.gson.annotations.SerializedName

data class DriverPerRaceResponce(
    val displayName: String,
    val season: String,
    val entries: List<DriverEntry>,
)

data class DriverEntry(
    val rank: Int,
    @SerializedName("driver_number")
    val driverNumber: Int,
    val name: String,
    val shortName: String,
    val abbreviation: String,
    @SerializedName("team_name")
    val teamName: String,
    val nationality: String,
    val championshipPts: ChampionshipPts,
    val races: List<DriverPerRace>,
)

data class ChampionshipPts(
    val value: Int,
    val displayValue: String,
)

data class DriverPerRace(
    val name: String,
    val displayName: String,
    val played: Boolean,
    val value: Int,
    val displayValue: String,
)
