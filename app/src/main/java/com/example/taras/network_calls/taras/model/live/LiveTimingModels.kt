package com.example.taras.network_calls.taras.model.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TrackStatus {
    @SerialName("GREEN")
    GREEN,
    @SerialName("YELLOW")
    YELLOW,
    @SerialName("SAFETY_CAR")
    SAFETY_CAR,
    @SerialName("VIRTUAL_SAFETY_CAR")
    VIRTUAL_SAFETY_CAR,
    @SerialName("RED_FLAG")
    RED_FLAG,
    @SerialName("FINISHED")
    FINISHED
}

@Serializable
data class LiveDriverRow(
    @SerialName("position")
    val position: Int,
    @SerialName("tla")
    val tla: String,
    @SerialName("fullName")
    val fullName: String,
    @SerialName("team")
    val team: String,
    @SerialName("teamColor")
    val teamColor: String,
    @SerialName("gapOrTime")
    val gapOrTime: String,
    @SerialName("tyre")
    val tyre: String = "UNKNOWN",
    @SerialName("tyresUsed")
    val tyresUsed: Int = 0,
    @SerialName("status")
    val status: String = "ACTIVE",
    @SerialName("tyreImageUrl")
    val tyreImageUrl: String? = null
)

@Serializable
data class RaceSnapshot(
    @SerialName("type")
    val type: String = "SNAPSHOT",
    @SerialName("timestamp")
    val timestamp: Long = 0L,
    @SerialName("grandPrix")
    val grandPrix: String = "",
    @SerialName("session")
    val session: String = "",
    @SerialName("trackStatus")
    val trackStatus: TrackStatus = TrackStatus.GREEN,
    @SerialName("isSessionActive")
    val isSessionActive: Boolean = true,
    @SerialName("driverCount")
    val driverCount: Int = 22,
    @SerialName("drivers")
    val drivers: List<LiveDriverRow> = emptyList()
)
