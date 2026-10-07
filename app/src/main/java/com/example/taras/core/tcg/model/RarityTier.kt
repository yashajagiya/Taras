package com.example.taras.core.tcg.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class RarityTier(
    val badgeLabel: String,
    val minOvr: Int,
    val maxOvr: Int,
    val borderStyle: String,
    val holoEffectName: String,
    val dropRate: Float,
    val primaryGoldHex: String,
    val accentHex: String
) {
    S_TIER(
        badgeLabel = "S",
        minOvr = 95,
        maxOvr = 99,
        borderStyle = "Gold Foil",
        holoEffectName = "Team Ultra Reflector",
        dropRate = 0.05f,
        primaryGoldHex = "#FFD700",
        accentHex = "#FFF275"
    ),
    A_TIER(
        badgeLabel = "A",
        minOvr = 88,
        maxOvr = 94,
        borderStyle = "Silver Chrome",
        holoEffectName = "Star Holo",
        dropRate = 0.15f,
        primaryGoldHex = "#E0E0E0",
        accentHex = "#FFFFFF"
    ),
    B_TIER(
        badgeLabel = "B",
        minOvr = 80,
        maxOvr = 87,
        borderStyle = "Bronze Metal",
        holoEffectName = "Neon Gloss",
        dropRate = 0.35f,
        primaryGoldHex = "#CD7F32",
        accentHex = "#E59866"
    ),
    C_TIER(
        badgeLabel = "C",
        minOvr = 70,
        maxOvr = 79,
        borderStyle = "Carbon Fiber",
        holoEffectName = "Matte Steel",
        dropRate = 0.45f,
        primaryGoldHex = "#888888",
        accentHex = "#555555"
    );

    val borderColor: Color
        get() = when (this) {
            S_TIER -> Color(0xFFFFD700)
            A_TIER -> Color(0xFFC0C0C0)
            B_TIER -> Color(0xFFCD7F32)
            C_TIER -> Color(0xFF4A4A4A)
        }
}
