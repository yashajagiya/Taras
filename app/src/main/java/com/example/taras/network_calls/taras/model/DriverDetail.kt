package com.example.taras.network_calls.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverDetail(
    @SerialName("driver_number") var driverNumber: Int? = null,
    @SerialName("broadcast_name") var broadcastName: String? = null,
    @SerialName("full_name") var fullName: String? = null,
    @SerialName("name_acronym") var nameAcronym: String? = null,
    @SerialName("team_name") var teamName: String? = null,
    @SerialName("team_colour") var teamColour: String? = null,
    @SerialName("first_name") var firstName: String? = null,
    @SerialName("last_name") var lastName: String? = null,
    @SerialName("headshot_url") var headshotUrl: String? = null,
    @SerialName("racing_number_mask") var racingNumberMask: String? = null
)
