package com.example.taras.network_calls.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class F1TeamsInfoResponse(
    val slug: String,
    val url: String,
    val hero: Hero,
    val biography: String,
    @SerialName("season_2026")
    val seasonStats: SeasonStats,
    @SerialName("team_summary")
    val teamSummary: TeamSummary,
    @SerialName("team_profile")
    val teamProfile: TeamProfile,
)

@Serializable
data class Hero(
    val name: String,
    @SerialName("team_color")
    val teamColor: String,
    @SerialName("accessible_color")
    val accessibleColor: String,
    @SerialName("team_car")
    val teamCar: String,
    @SerialName("team_logo")
    val teamLogo: String
)

@Serializable
data class SeasonStats(
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
data class TeamSummary(
    @SerialName("Grands Prix Entered") val grandsPrixEntered: String,
    @SerialName("Team Points") val teamPoints: String,
    @SerialName("Highest Race Finish") val highestRaceFinish: String,
    @SerialName("Podiums") val podiums: String,
    @SerialName("Highest Grid Position") val highestGridPosition: String,
    @SerialName("Pole Positions") val polePositions: String,
    @SerialName("World Championships") val worldChampionships: String,
)

@Serializable
data class TeamProfile(
    @SerialName("Full Team Name") val fullTeamName: String,
    @SerialName("Base") val base: String,
    @SerialName("Team Chief") val teamChief: String,
    @SerialName("Technical Chief") val technicalChief: String,
    @SerialName("Chassis") val chassis: String,
    @SerialName("Power Unit") val powerUnit: String,
    @SerialName("Reserve Driver") val reserveDriver: String,
    @SerialName("First Team Entry") val firstTeamEntry: String,
)