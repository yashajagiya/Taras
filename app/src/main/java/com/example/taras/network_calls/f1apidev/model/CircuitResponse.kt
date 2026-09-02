//package com.example.taras.network_calls.f1apidev.model
//
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class RaceData( //mot intrasted data
//    val api: String,
//    val url: String,
//    val limit: Int,
//    val offset: Int,
//    val total: Int,
//    val season: Int,
//    val championship: Championship,
//    val races: List<Race>,
//)
//
//@Serializable
//data class Championship(
//    val championshipId: String,
//    val championshipName: String,
//    val url: String,
//    val year: Int,
//)
//
//@Serializable
//data class Race(
//    val raceId: String,
//    val championshipId: String,
//    val raceName: String,
//    val schedule: Schedule,
//    val laps: Int?,
//    val round: Int,
//    val url: String,
//    @SerialName("fast_lap")
//    val fastLap: FastLap,
//    val circuit: Circuit,
//    val winner: Winner?,
//    val teamWinner: TeamWinner?,
//)
//
//@Serializable
//data class Schedule(
//    val race: Race2,
//    val qualy: Qualy,
//    val fp1: Fp1,
//    val fp2: Fp2,
//    val fp3: Fp3,
//    val sprintQualy: SprintQualy,
//    val sprintRace: SprintRace,
//)
//
//@Serializable
//data class Race2(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class Qualy(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class Fp1(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class Fp2(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class Fp3(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class SprintQualy(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class SprintRace(
//    val date: String?,
//    val time: String?,
//)
//
//@Serializable
//data class FastLap(
//    @SerialName("fast_lap")
//    val fastLap: String?,
//    @SerialName("fast_lap_driver_id")
//    val fastLapDriverId: String?,
//    @SerialName("fast_lap_team_id")
//    val fastLapTeamId: String?,
//)
//
//@Serializable
//data class Circuit(
//    val circuitId: String,
//    val circuitName: String,
//    val country: String,
//    val city: String,
//    val circuitLength: String,
//    val lapRecord: String?,
//    val firstParticipationYear: Int,
//    val corners: Int,
//    val fastestLapDriverId: String?,
//    val fastestLapTeamId: String?,
//    val fastestLapYear: Int?,
//    val url: String,
//)
//
//@Serializable
//data class Winner(
//    val driverId: String,
//    val name: String,
//    val surname: String,
//    val country: String,
//    val birthday: String,
//    val number: Int,
//    val shortName: String,
//    val url: String,
//)
//
//@Serializable
//data class TeamWinner(
//    val teamId: String,
//    val teamName: String,
//    val country: String,
//    val firstAppearance: Int,
//    val constructorsChampionships: Int,
//    val driversChampionships: Int,
//    val url: String,
//)
