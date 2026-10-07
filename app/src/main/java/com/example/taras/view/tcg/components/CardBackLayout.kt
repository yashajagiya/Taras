package com.example.taras.view.tcg.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardBackLayout(
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .shadow(12.dp, cardShape)
            .clip(cardShape)
            .background(Color(0xFF0F0F12))
            .border(4.dp, Color(0xFF222226), cardShape),
        contentAlignment = Alignment.Center
    ) {
        // Carbon Fiber & Kerb Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Carbon fiber weave background simulation
            val step = 14f
            var x = 0f
            while (x < w + h) {
                drawLine(
                    color = Color(0xFF1E1E22),
                    start = Offset(x, 0f),
                    end = Offset(0f, x),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF151518),
                    start = Offset(x - w, h),
                    end = Offset(x, 0f),
                    strokeWidth = 3f
                )
                x += step
            }

            // 2. Dashed Racetrack Kerb Border (Red & White alternating)
            val kerbInset = 16f
            val kerbPathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 24f), 0f)
            val kerbAltPathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 24f), 24f)

            // Red dashes
            drawRoundRect(
                color = Color(0xFFE10600),
                topLeft = Offset(kerbInset, kerbInset),
                size = androidx.compose.ui.geometry.Size(w - kerbInset * 2, h - kerbInset * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                style = Stroke(width = 6f, pathEffect = kerbPathEffect)
            )
            // White dashes
            drawRoundRect(
                color = Color(0xFFF5F5F5),
                topLeft = Offset(kerbInset, kerbInset),
                size = androidx.compose.ui.geometry.Size(w - kerbInset * 2, h - kerbInset * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                style = Stroke(width = 6f, pathEffect = kerbAltPathEffect)
            )

            // 3. Ambient Vortex Swirl Rings
            val center = Offset(w / 2f, h / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE10600).copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = w * 0.45f
                ),
                center = center,
                radius = w * 0.45f
            )

            drawCircle(
                color = Color(0xFFE10600).copy(alpha = 0.5f),
                center = center,
                radius = w * 0.35f,
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f)))
            )
        }

        // Central Glowing Vortex Disc (Modeled after the Pokémon ball back disc)
        Box(
            modifier = Modifier
                .size(136.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF1801),
                            Color(0xFF900000),
                            Color(0xFF260000)
                        )
                    )
                )
                .border(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFFFFFFFF), Color(0xFFFFD700))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "F1",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "COLLECTOR",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700),
                    letterSpacing = 3.sp
                )
            }
        }

        // Subtle bottom watermark text
        Text(
            text = "TARAS F1 • OFFICIAL TRADING CARD",
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF666666),
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}
