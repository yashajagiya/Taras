package com.example.taras.api.openf1.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverStanding(
    @SerialName("driver_number") val driverNumber: Int,
    @SerialName("meeting_key") val meetingKey: Int,
    @SerialName("points_current") val pointsCurrent: Int,
    @SerialName("points_start") val pointsStart: Int,
    @SerialName("position_current") val positionCurrent: Int,
    @SerialName("position_start") val positionStart: Int,
    @SerialName("session_key") val sessionKey: Int
)
