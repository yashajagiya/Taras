package com.example.taras.core.tcg.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class EnergyType(
    val symbol: String,
    val displayName: String,
    val colorHex: String
) {
    PACE("SPD", "Pace", "#FFD700"),
    RACECRAFT("RC", "Racecraft", "#E10600"),
    QUALIFYING("Q", "Qualifying", "#00D2BE"),
    DEFENSE("DEF", "Defense", "#1E41FF"),
    AERO("AERO", "Active Aero", "#00A19B"),
    ENGINE("PU", "Hybrid PU", "#FF8000"),
    RAIN("WET", "Wet Weather", "#3498DB");

    val color: Color
        get() = when (this) {
            PACE -> Color(0xFFFFD700)
            RACECRAFT -> Color(0xFFE10600)
            QUALIFYING -> Color(0xFF00D2BE)
            DEFENSE -> Color(0xFF1E41FF)
            AERO -> Color(0xFF00A19B)
            ENGINE -> Color(0xFFFF8000)
            RAIN -> Color(0xFF3498DB)
        }

    val icon: ImageVector
        get() = when (this) {
            PACE -> Icons.Filled.Bolt
            RACECRAFT -> Icons.Filled.Flag
            QUALIFYING -> Icons.Filled.Timer
            DEFENSE -> Icons.Filled.Shield
            AERO -> Icons.Filled.Air
            ENGINE -> Icons.Filled.DirectionsCar
            RAIN -> Icons.Filled.WaterDrop
        }
}
