package com.example.taras.view.tutorial

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taras.core.helpercore.RefreshHapticFeedback
import com.example.taras.core.navigation.MainNavRoutes
import com.example.taras.core.navigation.Navigator
import com.example.taras.viewmodel.TcgViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 6 Dedicated, screen-specific tutorial steps covering the entire real Taras Formula 1 app:
 * 1. Paddock Dashboard (Live Race Countdown)
 * 2. Grid & Telemetry (Driver Standings & Radar Comparison)
 * 3. Calendar (24 Race Circuits & Track Schedules)
 * 4. Results (Grand Prix Podium Finishers & Official Points)
 * 5. F1 Grid TCG Binder (Card Collection & "NEW PACK" Button)
 * 6. TCG Scratch Pack (Real Random Card Scratch & Database Persistence)
 */
enum class AppTutorialStep(
    val stepIndex: Int,
    val totalSteps: Int = 6,
    val categoryBadge: String,
    val categoryIcon: ImageVector,
    val title: String,
    val description: String,
    val actionText: String,
    val accentColor: Color
) {
    PADDOCK(
        stepIndex = 1,
        totalSteps = 6,
        categoryBadge = "PADDOCK DASHBOARD",
        categoryIcon = Icons.Default.SportsMotorsports,
        title = "Live Grand Prix Command Center",
        description = "Monitor live countdowns to practice, qualifying, and race sessions with track weather and session timetables.",
        actionText = "Next: Driver Grid",
        accentColor = Color(0xFF00E5FF) // Electric Cyan
    ),
    GRID(
        stepIndex = 2,
        totalSteps = 6,
        categoryBadge = "DRIVER STANDINGS",
        categoryIcon = Icons.AutoMirrored.Filled.CompareArrows,
        title = "Official Standings & Telemetry",
        description = "Inspect all 20 drivers and 10 constructor teams. View points standings or launch Head-to-Head to compare telemetry and ratings.",
        actionText = "Next: Race Calendar",
        accentColor = Color(0xFFFF8000) // Papaya Orange
    ),
    CALENDAR(
        stepIndex = 3,
        totalSteps = 6,
        categoryBadge = "RACE CALENDAR",
        categoryIcon = Icons.Default.CalendarMonth,
        title = "24 Circuits Around the World",
        description = "Explore all 24 Grand Prix tracks on the 2026 calendar. Tap any circuit to view track maps, corners, DRS zones, and race dates.",
        actionText = "Next: Race Results",
        accentColor = Color(0xFFFFD700) // Championship Gold
    ),
    RESULTS(
        stepIndex = 4,
        totalSteps = 6,
        categoryBadge = "RACE RESULTS",
        categoryIcon = Icons.Default.EmojiEvents,
        title = "Podium Finishers & Points",
        description = "Official race classification from every weekend: Practice times, Qualifying poles, Grand Prix podium winners, and fastest laps.",
        actionText = "Next: F1 Grid TCG",
        accentColor = Color(0xFFE040FB) // Neon Purple
    ),
    TCG_BINDER(
        stepIndex = 5,
        totalSteps = 6,
        categoryBadge = "F1 GRID TCG",
        categoryIcon = Icons.Default.AutoAwesome,
        title = "Draw Your First Mystery Pack",
        description = "Collect official 2026 driver and chassis cards across S, A, B, and C tiers. Tap 'NEW PACK' to draw a real random card into your binder!",
        actionText = "Open Mystery Pack",
        accentColor = Color(0xFFFF1744) // Racing Crimson
    ),
    TCG_SCRATCH(
        stepIndex = 6,
        totalSteps = 6,
        categoryBadge = "SCRATCH TO UNLOCK",
        categoryIcon = Icons.Default.TouchApp,
        title = "Scratch Foil to Reveal & Keep",
        description = "Rub your finger across the metallic silver coating to uncover your mystery driver. The card is permanently saved to your collection!",
        actionText = "Finish Tour",
        accentColor = Color(0xFF00E676) // Podium Emerald
    )
}

/**
 * Professional, fully animated in-app walkthrough overlay with interactive scratch pass-through.
 *
 * Implements high-end Mobbin mobile UX patterns:
 * - Edge-to-edge dark scrim covering status bar, top app bar, content, and bottom navigation.
 * - Precision spotlight cutout over real UI elements on each screen.
 * - Marching-ants dashed border and concentric expanding radar pulses.
 * - Smooth quadratic bezier directional arrows pointing cleanly at targets.
 * - Interactive touch pass-through inside the spotlight: scratches work 100% natively!
 * - Zero emojis, zero simulated currencies.
 * - Real random card roll via TcgRepository saved directly to Room SQLite.
 */
@Composable
fun RealAppTutorialOverlay(
    currentStep: AppTutorialStep,
    navigator: Navigator<MainNavRoutes>,
    tcgViewModel: TcgViewModel,
    onStepChange: (AppTutorialStep) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "tutorial_anim")

    // Marching-ants dashed offset animation
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    // Breathing pulse for spotlight border glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Expanding concentric radar pulse ("TAP HERE")
    val radarPulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarPulseProgress"
    )

    // Arrow flowing pulse
    val arrowFlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowFlowAlpha"
    )

    // Side-to-side swipe animation for scratch card step
    val swipeOffsetAnim by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swipeOffsetAnim"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // Responsive spotlight coordinates on each screen (accounting for top bar insets ~96dp)
        val (targetLeftPx, targetTopPx, targetRightPx, targetBottomPx, cornerRadiusPx) = when (currentStep) {
            AppTutorialStep.PADDOCK -> {
                // Focus on the top race countdown card in Paddock
                val left = with(density) { 16.dp.toPx() }
                val top = with(density) { 98.dp.toPx() }
                val right = screenWidthPx - with(density) { 16.dp.toPx() }
                val bottom = with(density) { 295.dp.toPx() }
                val radius = with(density) { 24.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
            AppTutorialStep.GRID -> {
                // Focus on the Driver Standings header & compare button in Grid
                val left = with(density) { 14.dp.toPx() }
                val top = with(density) { 98.dp.toPx() }
                val right = screenWidthPx - with(density) { 14.dp.toPx() }
                val bottom = with(density) { 290.dp.toPx() }
                val radius = with(density) { 22.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
            AppTutorialStep.CALENDAR -> {
                // Focus on the 2026 Grand Prix Circuit Card in Calendar
                val left = with(density) { 16.dp.toPx() }
                val top = with(density) { 98.dp.toPx() }
                val right = screenWidthPx - with(density) { 16.dp.toPx() }
                val bottom = with(density) { 275.dp.toPx() }
                val radius = with(density) { 22.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
            AppTutorialStep.RESULTS -> {
                // Focus on the Grand Prix Results & Podium card in Results
                val left = with(density) { 16.dp.toPx() }
                val top = with(density) { 98.dp.toPx() }
                val right = screenWidthPx - with(density) { 16.dp.toPx() }
                val bottom = with(density) { 285.dp.toPx() }
                val radius = with(density) { 22.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
            AppTutorialStep.TCG_BINDER -> {
                // Focus on the "NEW PACK" button in top-right of BinderGridScreen
                val left = screenWidthPx - with(density) { 148.dp.toPx() }
                val top = with(density) { 98.dp.toPx() }
                val right = screenWidthPx - with(density) { 14.dp.toPx() }
                val bottom = with(density) { 152.dp.toPx() }
                val radius = with(density) { 12.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
            AppTutorialStep.TCG_SCRATCH -> {
                // Focus precisely on the center 3D scratch card in PackOpeningScreen
                val cardWidthPx = with(density) { 280.dp.toPx() }
                val cardHeightPx = with(density) { 392.dp.toPx() }
                val left = (screenWidthPx - cardWidthPx) / 2f
                val top = (screenHeightPx - cardHeightPx) / 2f - with(density) { 14.dp.toPx() }
                val right = left + cardWidthPx
                val bottom = top + cardHeightPx
                val radius = with(density) { 20.dp.toPx() }
                listOf(left, top, right, bottom, radius)
            }
        }

        val targetWidthPx = targetRightPx - targetLeftPx
        val targetHeightPx = targetBottomPx - targetTopPx
        val targetCenterXPx = (targetLeftPx + targetRightPx) / 2f
        val targetCenterYPx = (targetTopPx + targetBottomPx) / 2f

        // Coachmark card placement
        val cardTopOffsetPx = if (currentStep == AppTutorialStep.TCG_SCRATCH) {
            // Position coachmark card at the bottom below the scratch card
            (targetBottomPx + with(density) { 14.dp.toPx() }).coerceAtMost(screenHeightPx - with(density) { 225.dp.toPx() })
        } else {
            targetBottomPx + with(density) { 24.dp.toPx() }
        }

        // Curved Arrow start and end points
        val arrowStart = Offset(
            if (currentStep == AppTutorialStep.TCG_BINDER) screenWidthPx - with(density) { 80.dp.toPx() } else screenWidthPx / 2f,
            cardTopOffsetPx
        )

        val arrowEnd = Offset(
            if (currentStep == AppTutorialStep.TCG_BINDER) targetCenterXPx else screenWidthPx / 2f,
            targetBottomPx + 2f
        )

        val curveBow = when (currentStep) {
            AppTutorialStep.TCG_BINDER -> 35f // Arc sweeping up-right to the New Pack button
            else -> 0f
        }

        // Function to advance the tour to next step
        val advanceTour: () -> Unit = {
            RefreshHapticFeedback.triggerStartHaptic(context, view)
            when (currentStep) {
                AppTutorialStep.PADDOCK -> {
                    navigator.navigate(MainNavRoutes.Grid)
                    onStepChange(AppTutorialStep.GRID)
                }
                AppTutorialStep.GRID -> {
                    navigator.navigate(MainNavRoutes.Calendar)
                    onStepChange(AppTutorialStep.CALENDAR)
                }
                AppTutorialStep.CALENDAR -> {
                    navigator.navigate(MainNavRoutes.F1Results)
                    onStepChange(AppTutorialStep.RESULTS)
                }
                AppTutorialStep.RESULTS -> {
                    navigator.navigate(MainNavRoutes.TcgBinder)
                    onStepChange(AppTutorialStep.TCG_BINDER)
                }
                AppTutorialStep.TCG_BINDER -> {
                    tcgViewModel.openMysteryPack { drawnCard ->
                        navigator.navigate(MainNavRoutes.TcgScratchPack(cardId = drawnCard.id))
                        onStepChange(AppTutorialStep.TCG_SCRATCH)
                    }
                }
                AppTutorialStep.TCG_SCRATCH -> {
                    onDismiss()
                }
            }
        }

        // 1. Full-Screen Dim Scrim Canvas with BlendMode.Clear Cutout
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            // Dark scrim overlay covering the entire screen (78% opacity)
            drawRect(color = Color.Black.copy(alpha = 0.78f))

            // Cut out the spotlight window to let the real app screen shine through
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(targetLeftPx, targetTopPx),
                size = Size(targetWidthPx, targetHeightPx),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                blendMode = BlendMode.Clear
            )
        }

        // 2. Animated Spotlight Graphics (Dashed Marching-Ants Border, Radar Pulse & Curved Arrow)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Concentric expanding radar wave ("TAP HERE")
            val pulseExpansion = with(density) { (22.dp * radarPulseProgress).toPx() }
            val pulseAlpha = (1f - radarPulseProgress) * 0.5f
            drawRoundRect(
                color = currentStep.accentColor.copy(alpha = pulseAlpha),
                topLeft = Offset(targetLeftPx - pulseExpansion, targetTopPx - pulseExpansion),
                size = Size(targetWidthPx + pulseExpansion * 2, targetHeightPx + pulseExpansion * 2),
                cornerRadius = CornerRadius(cornerRadiusPx + pulseExpansion, cornerRadiusPx + pulseExpansion),
                style = Stroke(width = 2.dp.toPx())
            )

            // High-contrast marching-ants dashed border around the cutout
            drawRoundRect(
                color = Color.White.copy(alpha = glowAlpha),
                topLeft = Offset(targetLeftPx, targetTopPx),
                size = Size(targetWidthPx, targetHeightPx),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(
                    width = 2.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), dashPhase)
                )
            )

            // Outer soft accent glow
            drawRoundRect(
                color = currentStep.accentColor.copy(alpha = 0.4f),
                topLeft = Offset(targetLeftPx - 2.5.dp.toPx(), targetTopPx - 2.5.dp.toPx()),
                size = Size(targetWidthPx + 5.dp.toPx(), targetHeightPx + 5.dp.toPx()),
                cornerRadius = CornerRadius(cornerRadiusPx + 2.5.dp.toPx(), cornerRadiusPx + 2.5.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Draw curved arrow pointing from card to target (except scratch step which has swipe guide)
            if (currentStep != AppTutorialStep.TCG_SCRATCH) {
                drawCurvedArrow(
                    start = arrowStart,
                    end = arrowEnd,
                    color = currentStep.accentColor.copy(alpha = arrowFlowAlpha),
                    strokeWidth = 3.dp.toPx(),
                    curveOffset = curveBow
                )
            }
        }

        // 3. Pointer Blocker & Selective Pass-Through
        // Touches OUTSIDE the cutout are consumed (so background isn't accidentally clicked).
        // Touches INSIDE the cutout pass unhindered so the user can interact (especially SCRATCH THE CARD!).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentStep, targetLeftPx, targetTopPx, targetRightPx, targetBottomPx) {
                    if (currentStep == AppTutorialStep.TCG_SCRATCH) {
                        // On scratch step, allow all drags & touches within the card to pass straight to ScratchCardReveal!
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val isOutsideTarget = event.changes.any { change ->
                                    change.position.x !in targetLeftPx..targetRightPx ||
                                            change.position.y !in targetTopPx..targetBottomPx
                                }
                                if (isOutsideTarget) {
                                    // Only consume touches that land strictly outside the scratch card
                                    event.changes.forEach { change ->
                                        if (change.position.x !in targetLeftPx..targetRightPx ||
                                            change.position.y !in targetTopPx..targetBottomPx
                                        ) {
                                            change.consume()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        detectTapGestures { offset ->
                            val isInsideTarget = offset.x in targetLeftPx..targetRightPx &&
                                    offset.y in targetTopPx..targetBottomPx

                            if (isInsideTarget) {
                                advanceTour()
                            } else {
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                            }
                        }
                    }
                }
        )

        // 4. Scratch Step Interactive Swipe Gesture Indicator
        if (currentStep == AppTutorialStep.TCG_SCRATCH) {
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { targetCenterXPx.toDp() - 75.dp } + with(density) { swipeOffsetAnim.toDp() },
                        y = with(density) { targetCenterYPx.toDp() - 30.dp }
                    )
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 10.dp,
                    border = BorderStroke(1.5.dp, Color(0xFF00E676))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Rub Finger to Scratch Foil",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 5. Top-Right Screen Close Button (X) (Swiggy & Plant Care pattern)
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 16.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xCC131722))
                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Guide",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // 6. Sleek Coachmark Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = with(density) { cardTopOffsetPx.toDp() })
                .padding(horizontal = 18.dp)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (slideInVertically { height -> height / 3 } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height / 3 } + fadeOut()
                    )
                },
                label = "coachmark_card_anim"
            ) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121622)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                    border = BorderStroke(1.5.dp, step.accentColor.copy(alpha = 0.65f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Top Header: Step Indicator Dots & Category Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category Badge Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = step.accentColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, step.accentColor.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = step.categoryIcon,
                                        contentDescription = null,
                                        tint = step.accentColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = step.categoryBadge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = step.accentColor
                                    )
                                }
                            }

                            // 6 Animated Step Indicator Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 1..step.totalSteps) {
                                    val isCurrent = i == step.stepIndex
                                    Box(
                                        modifier = Modifier
                                            .height(5.dp)
                                            .width(if (isCurrent) 18.dp else 5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCurrent) step.accentColor else Color.White.copy(alpha = 0.25f)
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Title
                        Text(
                            text = step.title,
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Description
                        Text(
                            text = step.description,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.88f),
                            lineHeight = 19.5.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (step.stepIndex > 1) {
                                TextButton(
                                    onClick = {
                                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                                        val previousStep = AppTutorialStep.entries[step.stepIndex - 2]
                                        when (previousStep) {
                                            AppTutorialStep.PADDOCK -> navigator.navigate(MainNavRoutes.Paddock)
                                            AppTutorialStep.GRID -> navigator.navigate(MainNavRoutes.Grid)
                                            AppTutorialStep.CALENDAR -> navigator.navigate(MainNavRoutes.Calendar)
                                            AppTutorialStep.RESULTS -> navigator.navigate(MainNavRoutes.F1Results)
                                            AppTutorialStep.TCG_BINDER -> navigator.navigate(MainNavRoutes.TcgBinder)
                                            AppTutorialStep.TCG_SCRATCH -> navigator.navigate(MainNavRoutes.TcgScratchPack())
                                        }
                                        onStepChange(previousStep)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Back", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                }
                            } else {
                                TextButton(onClick = onDismiss) {
                                    Text("Skip Tour", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                            }

                            Button(
                                onClick = advanceTour,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = step.accentColor,
                                    contentColor = Color.Black
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 18.dp,
                                    vertical = 10.dp
                                )
                            ) {
                                Text(
                                    text = step.actionText,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = when (step) {
                                        AppTutorialStep.TCG_SCRATCH -> Icons.Default.Check
                                        AppTutorialStep.TCG_BINDER -> Icons.Default.AutoAwesome
                                        else -> Icons.AutoMirrored.Filled.ArrowForward
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws a smooth curved quadratic bezier arrow with a directional arrowhead (Panera Bread & Swiggy inspired).
 */
private fun DrawScope.drawCurvedArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float,
    curveOffset: Float
) {
    val path = Path()
    path.moveTo(start.x, start.y)

    val midX = (start.x + end.x) / 2f
    val midY = (start.y + end.y) / 2f

    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val normX = -dy / len
    val normY = dx / len

    val ctrlX = midX + normX * curveOffset
    val ctrlY = midY + normY * curveOffset

    path.quadraticTo(ctrlX, ctrlY, end.x, end.y)

    // Draw main arrow curve
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )

    // Calculate tangent angle at arrowhead end
    val tangentDx = end.x - ctrlX
    val tangentDy = end.y - ctrlY
    val angle = atan2(tangentDy.toDouble(), tangentDx.toDouble()).toFloat()

    val arrowHeadLen = strokeWidth * 4f
    val arrowAngle = (Math.PI / 5.5).toFloat() // ~32 degrees

    val leftWingX = end.x - arrowHeadLen * cos(angle - arrowAngle)
    val leftWingY = end.y - arrowHeadLen * sin(angle - arrowAngle)

    val rightWingX = end.x - arrowHeadLen * cos(angle + arrowAngle)
    val rightWingY = end.y - arrowHeadLen * sin(angle + arrowAngle)

    val arrowHeadPath = Path().apply {
        moveTo(end.x, end.y)
        lineTo(leftWingX, leftWingY)
        lineTo(rightWingX, rightWingY)
        close()
    }

    // Fill arrowhead cleanly
    drawPath(
        path = arrowHeadPath,
        color = color,
        style = Fill
    )
}
