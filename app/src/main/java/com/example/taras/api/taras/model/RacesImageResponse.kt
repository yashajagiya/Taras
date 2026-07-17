package com.example.taras.api.taras.model

import com.google.gson.annotations.SerializedName

data class RacesImageResponse(

    @SerializedName("race_name")
    val raceName: String,
    @SerializedName("circuit_id")
    val circuitId: String,
    val country: String,
    val city: String,
    @SerializedName("track_image")
    val trackImage: String,
)
