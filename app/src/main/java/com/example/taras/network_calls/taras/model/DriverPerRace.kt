package com.example.taras.network_calls.taras.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DriverPerRace(
    val name: String,
    val displayName: String,
    val played: Boolean,
    val value: Int,
    val displayValue: String,
)
