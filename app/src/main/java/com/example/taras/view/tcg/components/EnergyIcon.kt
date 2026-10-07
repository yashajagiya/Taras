package com.example.taras.view.tcg.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.taras.core.tcg.model.EnergyType

@Composable
fun EnergyIcon(
    energyType: EnergyType,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp
) {
    val bgColor = energyType.color

    Box(
        modifier = modifier
            .size(size)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        bgColor.copy(alpha = 0.95f),
                        bgColor.copy(alpha = 0.7f),
                        Color(0xFF101010)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = energyType.icon,
            contentDescription = energyType.displayName,
            tint = Color.White,
            modifier = Modifier.size(size * 0.65f)
        )
    }
}
