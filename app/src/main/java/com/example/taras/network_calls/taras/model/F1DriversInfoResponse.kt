package com.example.taras.network_calls.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class F1DriversInfoResponse(
    val slug: String,
    val url: String,
    val hero: DriverHero,
    val biography: Biography,
    @SerialName("season_2026")
    val seasonStats: DriverSeasonStats,
    @SerialName("career_stats")
    val careerStats: CareerStats,
)

@Serializable
data class DriverHero(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val country: String,
    val team: String,
    val number: String,
    @SerialName("team_color") val teamColor: String,
    @SerialName("accessible_color") val accessibleColor: String,
)

@Serializable
data class Biography(
    @SerialName("Date of Birth") val dateOfBirth: String,
    @SerialName("Place of Birth") val placeOfBirth: String,
    val text: List<String>,
    val quote: Quote? = null,
)

@Serializable
data class Quote(
    val text: String,
    val author: String,
)

@Serializable
data class DriverSeasonStats(
    @SerialName("Season Position") val seasonPosition: String,
    @SerialName("Season Points") val seasonPoints: String,
    @SerialName("Grand Prix Races") val grandPrixRaces: String,
    @SerialName("Grand Prix Points") val grandPrixPoints: String,
    @SerialName("Grand Prix Wins") val grandPrixWins: String,
    @SerialName("Grand Prix Podiums") val grandPrixPodiums: String,
    @SerialName("Grand Prix Poles") val grandPrixPoles: String,
    @SerialName("Grand Prix Top 10s") val grandPrixTop10s: String,
    @SerialName("DHL Fastest Laps") val dhlFastestLaps: String,
    @SerialName("DNFs") val dnfs: String,
    @SerialName("Sprint Races") val sprintRaces: String,
    @SerialName("Sprint Points") val sprintPoints: String,
    @SerialName("Sprint Wins") val sprintWins: String,
    @SerialName("Sprint Podiums") val sprintPodiums: String,
    @SerialName("Sprint Poles") val sprintPoles: String,
    @SerialName("Sprint Top 10s") val sprintTop10s: String,
)

@Serializable
data class CareerStats(
    @SerialName("Grands Prix Entered") val grandsPrixEntered: String,
    @SerialName("Career Points") val careerPoints: String,
    @SerialName("Highest Race Finish") val highestRaceFinish: String,
    @SerialName("Podiums") val podiums: String,
    @SerialName("Highest Grid Position") val highestGridPosition: String,
    @SerialName("Pole Positions") val polePositions: String,
    @SerialName("World Championships") val worldChampionships: String,
    @SerialName("DNFs") val dnfs: String,
)