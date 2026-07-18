package com.example.taras.api.openf1.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverInfo(
    @SerialName("broadcast_name") val broadcastName: String,
    @SerialName("country_code") val countryCode: String?,
    @SerialName("driver_number") val driverNumber: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("headshot_url") val headshotUrl: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("meeting_key") val meetingKey: Int,
    @SerialName("name_acronym") val nameAcronym: String,
    @SerialName("session_key") val sessionKey: Int,
    @SerialName("team_colour") val teamColor: String,
    @SerialName("team_name") val teamName: String
)
