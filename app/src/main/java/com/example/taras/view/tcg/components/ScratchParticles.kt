package com.example.taras.view.tcg.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun ScratchParticles(
    isTriggered: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isTriggered) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFF3D00), // Fiery Orange
            Color(0xFFE10600), // Ferrari / F1 Red
            Color(0xFF00E5FF), // Electric Cyan
            Color(0xFFFFFFFF), // White spark
            Color(0x88AAAAAA)  // Tyre smoke puff
        )
        List(60) {
            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val speed = Random.nextFloat() * 320f + 120f
            val radius = Random.nextFloat() * 6f + 3f
            val color = colors.random()
            Particle(angle, speed, radius, color)
        }
    }

    LaunchedEffect(isTriggered) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutLinearInEasing)
        )
    }

    val p = progress.value
    if (p >= 1f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        for (pt in particles) {
            val distance = pt.speed * p
            val x = centerX + (cos(pt.angle) * distance).toFloat()
            val y = centerY + (sin(pt.angle) * distance).toFloat()
            val alpha = (1f - p).coerceIn(0f, 1f)
            val currentRadius = pt.radius * (1f + p * 0.8f)

            drawCircle(
                color = pt.color.copy(alpha = alpha),
                radius = currentRadius,
                center = Offset(x, y)
            )
        }
    }
}
