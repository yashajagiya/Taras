package com.example.taras.api.taras.model

import com.google.gson.annotations.SerializedName

data class DriverDetail(
    @SerializedName("driver_number") var driverNumber: Int? = null,
    @SerializedName("broadcast_name") var broadcastName: String? = null,
    @SerializedName("full_name") var fullName: String? = null,
    @SerializedName("name_acronym") var nameAcronym: String? = null,
    @SerializedName("team_name") var teamName: String? = null,
    @SerializedName("team_colour") var teamColour: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("headshot_url") var headshotUrl: String? = null,
    @SerializedName("racing_number_mask") var racingNumberMask: String? = null

)
