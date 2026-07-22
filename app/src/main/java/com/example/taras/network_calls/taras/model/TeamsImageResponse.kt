package com.example.taras.network_calls.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamsImageResponse(
    @SerialName("team_name")
    val teamName: String,
    @SerialName("team_color")
    val teamColor: String,
    @SerialName("team_logo")
    val teamLogo: String,
    @SerialName("team_car")
    val teamCar: String,
)
