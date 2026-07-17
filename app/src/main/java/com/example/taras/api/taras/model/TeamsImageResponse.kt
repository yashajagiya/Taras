package com.example.taras.api.taras.model

import com.google.gson.annotations.SerializedName

data class TeamsImageResponse(
    @SerializedName("team_name")
    val teamName: String,
    @SerializedName("team_color")
    val teamColor: String,
    @SerializedName("team_logo")
    val teamLogo: String,
    @SerializedName("team_car")
    val teamCar: String,
)
