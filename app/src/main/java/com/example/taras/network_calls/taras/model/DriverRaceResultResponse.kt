package com.example.taras.network_calls.taras.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class RaceResult(
    val position: String,
    val driverNumber: String,
    val driverName: String,
    val team: String,
    val laps: String,
    val timeOrRetired: String,
    val points: String
)
@Immutable
@Serializable
data class DriverRaceResultResponse(
    val country: String,
    val session: String,
    val raceName: String,
    val date: String,
    val circuitName: String,
    val circuitId: String,
    val results: List<RaceResult>
)