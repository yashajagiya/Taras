package com.example.taras.network_calls.taras.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class RaceQualifyingResults(
    val position: String,
    val driverNumber: String,
    val driverName: String,
    val team: String,
    val q1: String,
    val q2: String,
    val q3: String,
    val laps: String
)
@Immutable
@Serializable
data class DriverRaceQualifyingResponse(
    val country: String,
    val session: String,
    val raceName: String,
    val date: String,
    val circuitName: String,
    val circuitId: String,
    val results: List<RaceQualifyingResults>
)