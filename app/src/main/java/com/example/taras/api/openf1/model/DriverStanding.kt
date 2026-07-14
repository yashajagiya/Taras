package com.example.taras.api.openf1.model

import com.google.gson.annotations.SerializedName

data class DriverStanding(
    @SerializedName("driver_number") val driverNumber: Int,
    @SerializedName("meeting_key") val meetingKey: Int,
    @SerializedName("points_current") val pointsCurrent: Int,
    @SerializedName("points_start") val pointsStart: Int,
    @SerializedName("position_current") val positionCurrent: Int,
    @SerializedName("position_start") val positionStart: Int,
    @SerializedName("session_key") val sessionKey: Int
)
