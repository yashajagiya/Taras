package com.example.taras.api.taras.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RacesImageResponse(

    @SerialName("race_name")
    val raceName: String,
    @SerialName("circuit_id")
    val circuitId: String,
    val country: String,
    val city: String,
    @SerialName("track_image")
    val trackImage: String,
)
