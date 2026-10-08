package com.example.taras.view.tcg.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.taras.core.db.tcg.TcgCardEntity

@Composable
fun Interactive3DCard(
    card: TcgCardEntity,
    modifier: Modifier = Modifier,
    isHoloVariant: Boolean = true,
    enableFlip: Boolean = true,
    onCardClick: (() -> Unit)? = null
) {
    var isFlipped by remember { mutableStateOf(false) }
    var targetTiltX by remember { mutableFloatStateOf(0f) }
    var targetTiltY by remember { mutableFloatStateOf(0f) }
    var cardWidth by remember { mutableFloatStateOf(1f) }
    var cardHeight by remember { mutableFloatStateOf(1f) }

    val density = LocalDensity.current.density

    // Smooth spring physics for tilt return
    val animatedTiltX by animateFloatAsState(
        targetValue = targetTiltX,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animatedTiltX"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = targetTiltY,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animatedTiltY"
    )

    // Smooth 180° Flip animation
    val flipAngleY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "flipAngleY"
    )

    val isBackVisible = flipAngleY > 90f
    val totalRotationY = animatedTiltY + flipAngleY

    val teamPrimaryColor = remember(card.primaryColorHex) {
        parseHexColor(card.primaryColorHex, Color(0xFFE10600))
    }
    val teamSecondaryColor = remember(card.secondaryColorHex) {
        parseHexColor(card.secondaryColorHex, Color.White)
    }

    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .onSizeChanged { size ->
                cardWidth = size.width.toFloat().coerceAtLeast(1f)
                cardHeight = size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (enableFlip) isFlipped = !isFlipped
                    },
                    onTap = {
                        if (onCardClick != null) {
                            onCardClick()
                        } else if (enableFlip) {
                            isFlipped = !isFlipped
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        targetTiltX = 0f
                        targetTiltY = 0f
                    },
                    onDragCancel = {
                        targetTiltX = 0f
                        targetTiltY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Sensitivity calculation
                        val sensitivity = 0.15f
                        val newTiltY = (targetTiltY + (dragAmount.x * sensitivity)).coerceIn(-25f, 25f)
                        val newTiltX = (targetTiltX - (dragAmount.y * sensitivity)).coerceIn(-25f, 25f)
                        targetTiltX = newTiltX
                        targetTiltY = newTiltY
                    }
                )
            }
            .graphicsLayer {
                rotationX = animatedTiltX
                rotationY = totalRotationY
                cameraDistance = 16f * density // Preserves 3D depth without polygon distortion
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
    ) {
        if (!isBackVisible) {
            // Front Face
            Box(modifier = Modifier.fillMaxSize()) {
                CardFrontLayout(
                    card = card,
                    tiltX = animatedTiltX,
                    tiltY = animatedTiltY,
                    modifier = Modifier.fillMaxSize()
                )
                // Holographic Sheen Layer (Team Color & Specular Reflective)
                if (isHoloVariant) {
                    HoloShaderEffect(
                        tier = card.tier,
                        teamColor = teamPrimaryColor,
                        teamSecondaryColor = teamSecondaryColor,
                        tiltX = animatedTiltX,
                        tiltY = animatedTiltY,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Back Face (counter-rotated 180° so back graphics aren't mirrored)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                CardBackLayout(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
