package com.example.taras.view.tcg.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.taras.core.tcg.model.RarityTier

@Composable
fun HoloShaderEffect(
    tier: RarityTier,
    teamColor: Color,
    teamSecondaryColor: Color = Color.White,
    tiltX: Float, // -25f to +25f
    tiltY: Float, // -25f to +25f
    modifier: Modifier = Modifier
) {
    // Normalize tilt to 0.0 .. 1.0 range
    val normX = ((tiltY + 25f) / 50f).coerceIn(0f, 1f)
    val normY = ((tiltX + 25f) / 50f).coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val startX = (normX - 0.5f) * w * 1.5f
        val startY = (normY - 0.5f) * h * 1.5f
        val endX = startX + w * 1.2f
        val endY = startY + h * 1.2f

        when (tier) {
            RarityTier.S_TIER -> {
                // Tier S: Refined Gold Foil Reflection + Team Accent - Softened & Elegant (decreased intensity)
                val goldFoilColors = listOf(
                    Color.Transparent,
                    Color(0x24FFD700), // Subtle Gold
                    teamColor.copy(alpha = 0.16f), // Soft Team highlight
                    Color(0x32FFF275), // Warm Gold
                    Color.White.copy(alpha = 0.35f), // Controlled specular glint
                    Color(0x24FFD700), // Gold
                    teamSecondaryColor.copy(alpha = 0.12f),
                    Color.Transparent
                )
                drawRect(
                    brush = Brush.linearGradient(
                        colors = goldFoilColors,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.42f
                )

                // Gentle secondary golden sheen sweep
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x1CFFF275),
                            Color.White.copy(alpha = 0.20f),
                            Color(0x14FFD700),
                            Color.Transparent
                        ),
                        start = Offset(startX * 0.8f, 0f),
                        end = Offset(startX * 0.8f + w * 0.45f, h)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.25f
                )
            }
            RarityTier.A_TIER -> {
                // Tier A: Silver / Chrome Reflector + Team Accent
                val silverFoilColors = listOf(
                    Color.Transparent,
                    Color(0x22E0E0E0), // Chrome Silver
                    teamColor.copy(alpha = 0.14f), // Team hint
                    Color.White.copy(alpha = 0.28f), // Controlled silver glare peak
                    Color(0x22C0C0C0), // Silver
                    Color.Transparent
                )
                drawRect(
                    brush = Brush.linearGradient(
                        colors = silverFoilColors,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.35f
                )

                // Secondary subtle silver specular streak
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        start = Offset(startX * 0.7f, 0f),
                        end = Offset(startX * 0.7f + w * 0.35f, h)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.22f
                )
            }
            RarityTier.B_TIER -> {
                // Tier B: Bronze / Metallic Sheen + Team Accent
                val bronzeFoilColors = listOf(
                    Color.Transparent,
                    Color(0x22CD7F32), // Warm Bronze
                    teamColor.copy(alpha = 0.10f), // Team hint
                    Color.White.copy(alpha = 0.20f), // Soft specular peak
                    Color(0x22E59866), // Copper
                    Color.Transparent
                )
                drawRect(
                    brush = Brush.linearGradient(
                        colors = bronzeFoilColors,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.26f
                )
            }
            RarityTier.C_TIER -> {
                // Tier C: Subtle Clear Gloss Coat
                val glossColors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.12f), // Soft subtle clear-coat reflection
                    teamColor.copy(alpha = 0.06f),
                    Color.Transparent
                )
                drawRect(
                    brush = Brush.linearGradient(
                        colors = glossColors,
                        start = Offset(startX, 0f),
                        end = Offset(startX + w * 0.45f, h)
                    ),
                    blendMode = BlendMode.Screen,
                    alpha = 0.15f
                )
            }
        }
    }
}
