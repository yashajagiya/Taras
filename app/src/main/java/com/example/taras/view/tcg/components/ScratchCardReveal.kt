package com.example.taras.view.tcg.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScratchCardReveal(
    modifier: Modifier = Modifier,
    isAlreadyRevealed: Boolean = false,
    onRevealCompleted: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isRevealed by remember { mutableStateOf(isAlreadyRevealed) }
    var currentCoverage by remember { mutableFloatStateOf(if (isAlreadyRevealed) 1f else 0f) }
    var cardWidth by remember { mutableFloatStateOf(1f) }
    var cardHeight by remember { mutableFloatStateOf(1f) }

    val haptic = LocalHapticFeedback.current
    val scratchPath = remember { Path() }
    var pathUpdateTrigger by remember { mutableIntStateOf(0) }

    // 10x10 Node Grid Sampling (100 cells)
    val visitedCells = remember { BooleanArray(100) { isAlreadyRevealed } }
    var lastHapticTick by remember { mutableFloatStateOf(0f) }

    val foilAlpha by animateFloatAsState(
        targetValue = if (isRevealed) 0f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "foilAlpha"
    )

    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .clip(RoundedCornerShape(18.dp))
            .onSizeChanged { size ->
                cardWidth = size.width.toFloat().coerceAtLeast(1f)
                cardHeight = size.height.toFloat().coerceAtLeast(1f)
            }
    ) {
        // Base Content Layer (the card beneath)
        content()

        // Offscreen Scratch Layer
        if (foilAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = foilAlpha
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .pointerInput(isRevealed) {
                        if (isRevealed) return@pointerInput

                        detectDragGestures(
                            onDragStart = { offset ->
                                scratchPath.moveTo(offset.x, offset.y)
                                updateGridCoverage(
                                    x = offset.x,
                                    y = offset.y,
                                    width = cardWidth,
                                    height = cardHeight,
                                    visitedCells = visitedCells,
                                    onCoverageUpdated = { cov ->
                                        currentCoverage = cov
                                        if (cov >= 0.65f && !isRevealed) {
                                            isRevealed = true
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onRevealCompleted()
                                        }
                                    }
                                )
                                pathUpdateTrigger++
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val touchPos = change.position
                                scratchPath.lineTo(touchPos.x, touchPos.y)

                                updateGridCoverage(
                                    x = touchPos.x,
                                    y = touchPos.y,
                                    width = cardWidth,
                                    height = cardHeight,
                                    visitedCells = visitedCells,
                                    onCoverageUpdated = { cov ->
                                        currentCoverage = cov
                                        // Subtle haptic vibration tick while scratching
                                        if (cov - lastHapticTick >= 0.05f) {
                                            lastHapticTick = cov
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        if (cov >= 0.65f && !isRevealed) {
                                            isRevealed = true
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onRevealCompleted()
                                        }
                                    }
                                )
                                pathUpdateTrigger++
                            }
                        )
                    }
            ) {
                // Metallic Silver / Carbon Foil Mask
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 1. Base metallic foil gradient
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFC0C0C0),
                                Color(0xFFE8E8E8),
                                Color(0xFFA0A0A0),
                                Color(0xFFD3D3D3),
                                Color(0xFF888888)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        )
                    )

                    // 2. Diagonal security texture lines
                    var lineOffset = 0f
                    while (lineOffset < w + h) {
                        drawLine(
                            color = Color(0x22FFFFFF),
                            start = Offset(lineOffset, 0f),
                            end = Offset(0f, lineOffset),
                            strokeWidth = 2.5f
                        )
                        lineOffset += 18f
                    }

                    // 3. Clear path strokes where finger scratched (BlendMode.Clear)
                    if (pathUpdateTrigger >= 0) {
                        drawPath(
                            path = scratchPath,
                            color = Color.Transparent,
                            style = Stroke(
                                width = 84f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                // Decorative Center Foil Crest (smoothly fades out as scratched)
                val crestAlpha = (1f - (currentCoverage / 0.4f)).coerceIn(0f, 1f)
                if (crestAlpha > 0f) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer { alpha = crestAlpha }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFB8860B))
                                    )
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "F1 Pack",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "SCRATCH TO REVEAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 2.sp,
                            color = Color(0xFF222222),
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = "${(currentCoverage * 100).toInt()}% ERASED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF444444),
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
        }

        // Particle Burst Overlay
        ScratchParticles(
            isTriggered = isRevealed,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * 10x10 Node Grid Area Sampling Algorithm
 * Computes coverage in O(1) time without expensive GPU pixel readbacks.
 */
private fun updateGridCoverage(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    visitedCells: BooleanArray,
    onCoverageUpdated: (Float) -> Unit
) {
    val cellW = width / 10f
    val cellH = height / 10f
    val strokeRadius = 42f

    val minCol = ((x - strokeRadius) / cellW).toInt().coerceIn(0, 9)
    val maxCol = ((x + strokeRadius) / cellW).toInt().coerceIn(0, 9)
    val minRow = ((y - strokeRadius) / cellH).toInt().coerceIn(0, 9)
    val maxRow = ((y + strokeRadius) / cellH).toInt().coerceIn(0, 9)

    for (c in minCol..maxCol) {
        for (r in minRow..maxRow) {
            visitedCells[r * 10 + c] = true
        }
    }

    var count = 0
    for (i in 0 until 100) {
        if (visitedCells[i]) count++
    }

    val coverage = count / 100f
    onCoverageUpdated(coverage)
}
