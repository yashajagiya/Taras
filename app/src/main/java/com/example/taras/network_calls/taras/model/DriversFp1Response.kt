package com.example.taras.network_calls.taras.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class Fp1Results(
    val position: String,
    val number: String,
    val driver: String,
    val shortName: String,
    val team: String,
    val timeOrGap: String,
    val laps: String
)
@Immutable
@Serializable
data class Fp1Response(
    val raceName: String,
    val raceDate: String,
    val circuitName: String,
    val circuitId: String,
    val results: List<Fp1Results>
)