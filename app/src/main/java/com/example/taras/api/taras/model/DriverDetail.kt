package com.example.taras.api.taras.model

import com.google.gson.annotations.SerializedName

data class DriverDetail(
    @SerializedName("broadcast_name") val broadcastName: String,
    @SerializedName("driver_number") val driverNumber: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("headshot_url") val headshotUrl: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("name_acronym") val nameAcronym: String,
    @SerializedName("team_colour") val teamColor: String,
    @SerializedName("team_name") val teamName: String
)
