package com.example.taras.core.tcg.model

import kotlinx.serialization.Serializable

@Serializable
enum class CardRole {
    DRIVER,
    CHASSIS
}

@Serializable
data class CardMove(
    val title: String,
    val energyCosts: List<EnergyType>,
    val power: Int,
    val description: String
)

@Serializable
data class TelemetryRadarStats(
    val qualifyingPace: Int,
    val racecraft: Int,
    val tyreManagement: Int,
    val wetSkill: Int,
    val consistency: Int
)

@Serializable
data class MatchupInfo(
    val weakness: String,
    val weaknessMultiplier: String = "×2",
    val resistance: String,
    val resistanceValue: String = "−20",
    val pitCost: Int = 2
)
