package com.example.taras.api.openf1.model

import com.google.gson.annotations.SerializedName

data class DriverInfo(
    @SerializedName("broadcast_name") val broadcastName: String,
    @SerializedName("country_code") val countryCode: Any?,
    @SerializedName("driver_number") val driverNumber: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("headshot_url") val headshotUrl: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("meeting_key") val meetingKey: Int,
    @SerializedName("name_acronym") val nameAcronym: String,
    @SerializedName("session_key") val sessionKey: Int,
    @SerializedName("team_colour") val teamColor: String,
    @SerializedName("team_name") val teamName: String
)
