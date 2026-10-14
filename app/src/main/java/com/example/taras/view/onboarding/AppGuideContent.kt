package com.example.taras.view.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taras.core.db.tcg.TcgCardEntity
import com.example.taras.core.helpercore.RefreshHapticFeedback
import com.example.taras.core.navigation.MainNavRoutes
import com.example.taras.core.tcg.seed.RosterSeedData
import com.example.taras.view.tcg.components.CardFrontLayout
import com.example.taras.view.tcg.components.ScratchCardReveal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Chapters of the interactive beginner's visual tutorial.
 */
enum class TutorialChapter(
    val stepNumber: Int,
    val title: String,
    val shortBadge: String,
    val icon: ImageVector,
    val themeColor: Color
) {
    APP_TOUR(
        stepNumber = 1,
        title = "App Tour & Real World F1",
        shortBadge = "App Tour",
        icon = Icons.Default.Home,
        themeColor = Color(0xFF00E5FF)
    ),
    TCG_SCRATCH(
        stepNumber = 2,
        title = "Step 1: Scratch to Reveal Packs",
        shortBadge = "1. Scratch",
        icon = Icons.Default.Style,
        themeColor = Color(0xFFFF8000)
    ),
    CARD_ANATOMY(
        stepNumber = 3,
        title = "Step 2: Card Stats & Rarity Tiers",
        shortBadge = "2. Card Stats",
        icon = Icons.Default.AutoAwesome,
        themeColor = Color(0xFFFFD700)
    ),
    TEAM_BUILDER(
        stepNumber = 4,
        title = "Step 3: Build 3-Card Battle Squad",
        shortBadge = "3. Build Squad",
        icon = Icons.Default.SportsMotorsports,
        themeColor = Color(0xFF00E676)
    ),
    BATTLE_ARENA(
        stepNumber = 5,
        title = "Step 4: Battle & Duel in Arena",
        shortBadge = "4. Battle Duel",
        icon = Icons.Default.FlashOn,
        themeColor = Color(0xFFFF1744)
    ),
    REWARDS_PLAY(
        stepNumber = 6,
        title = "Step 5: Daily Coins & Start Racing",
        shortBadge = "5. Let's Race",
        icon = Icons.Default.EmojiEvents,
        themeColor = Color(0xFFE040FB)
    )
}

/**
 * Animated finger pointer showing learners/beginners/kids where to tap or swipe.
 */
@Composable
fun AnimatedFingerPointer(
    text: String,
    modifier: Modifier = Modifier,
    swipeMode: Boolean = false,
    color: Color = Color(0xFFFFD700)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pointer_anim")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (swipeMode) 0f else -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceY"
    )
    val swipeX by infiniteTransition.animateFloat(
        initialValue = -42f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swipeX"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                translationY = bounceY
                if (swipeMode) translationX = swipeX
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = color,
            shadowElevation = 8.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
        Text(
            text = if (swipeMode) "👈 👆 👉" else "👆",
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

/**
 * Interactive Visual Beginner's Guide & Animated Tutorial for Taras & F1 Grid TCG.
 * Fully interactive with live scratch card simulation, visual 5-tab app tour,
 * card anatomy hotspot breakdown, 3-slot lineup builder, and duel arena demo.
 */
@Composable
fun AppGuideContent(
    onNavigateToRoute: (MainNavRoutes) -> Unit = {},
    onSwitchToSetup: () -> Unit = {},
    onDismissGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    var currentChapter by remember { mutableStateOf(TutorialChapter.APP_TOUR) }
    val chapters = TutorialChapter.entries

    val norrisCard = remember {
        RosterSeedData.allCards.find { it.id == "card_mcl_01" } ?: RosterSeedData.allCards.first()
    }
    val alonsoCard = remember {
        RosterSeedData.allCards.find { it.id == "card_amr_14" } ?: RosterSeedData.allCards.first()
    }
    val piastriCard = remember {
        RosterSeedData.allCards.find { it.id == "card_mcl_81" } ?: RosterSeedData.allCards.first()
    }
    val mclarenCar = remember {
        RosterSeedData.allCards.find { it.id == "card_car_mcl_40" } ?: RosterSeedData.allCards.first()
    }
    val ferrariCar = remember {
        RosterSeedData.allCards.find { it.id == "card_car_fer_26" } ?: RosterSeedData.allCards.first()
    }
    val mercedesCar = remember {
        RosterSeedData.allCards.find { it.id == "card_car_mer_17" } ?: RosterSeedData.allCards.first()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // --- TOP BANNER WITH TITLE & PROGRESS ---
        TutorialTopBanner(
            currentChapter = currentChapter,
            totalChapters = chapters.size,
            onQuickClose = onDismissGuide
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- CHAPTER SELECTOR CHIPS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chapters.forEach { chapter ->
                val isSelected = currentChapter == chapter
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) chapter.themeColor else MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable {
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                        currentChapter = chapter
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = chapter.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = chapter.shortBadge,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ACTIVE CHAPTER ANIMATED CONTENT ---
        AnimatedContent(
            targetState = currentChapter,
            transitionSpec = {
                if (targetState.stepNumber > initialState.stepNumber) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "chapter_content"
        ) { chapter ->
            when (chapter) {
                TutorialChapter.APP_TOUR -> {
                    InteractiveAppTourChapter(
                        onNavigateToRoute = onNavigateToRoute
                    )
                }
                TutorialChapter.TCG_SCRATCH -> {
                    InteractiveScratchChapter(
                        norrisCard = norrisCard,
                        onOpenRealPacks = { onNavigateToRoute(MainNavRoutes.TcgScratchPack()) }
                    )
                }
                TutorialChapter.CARD_ANATOMY -> {
                    InteractiveCardAnatomyChapter(
                        card = norrisCard,
                        altCard = alonsoCard
                    )
                }
                TutorialChapter.TEAM_BUILDER -> {
                    InteractiveTeamBuilderChapter(
                        norrisCard = norrisCard,
                        piastriCard = piastriCard,
                        mclarenCar = mclarenCar,
                        alonsoCard = alonsoCard,
                        ferrariCar = ferrariCar
                    )
                }
                TutorialChapter.BATTLE_ARENA -> {
                    InteractiveBattleArenaChapter(
                        userCard = norrisCard,
                        rivalCard = ferrariCar,
                        onGoToRealArena = { onNavigateToRoute(MainNavRoutes.TcgArena()) }
                    )
                }
                TutorialChapter.REWARDS_PLAY -> {
                    InteractiveRewardsPlayChapter(
                        onOpenPackOpening = { onNavigateToRoute(MainNavRoutes.TcgScratchPack()) },
                        onOpenArena = { onNavigateToRoute(MainNavRoutes.TcgArena()) },
                        onOpenBinder = { onNavigateToRoute(MainNavRoutes.TcgBinder) },
                        onGoPaddock = {
                            onNavigateToRoute(MainNavRoutes.Paddock)
                            onDismissGuide()
                        },
                        onOpenSetup = onSwitchToSetup
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- BOTTOM CHAPTER NAVIGATION CONTROLS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentIndex = chapters.indexOf(currentChapter)
            if (currentIndex > 0) {
                OutlinedButton(
                    onClick = {
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                        currentChapter = chapters[currentIndex - 1]
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (currentIndex < chapters.size - 1) {
                Button(
                    onClick = {
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                        currentChapter = chapters[currentIndex + 1]
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = chapters[currentIndex + 1].themeColor,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Next: ${chapters[currentIndex + 1].shortBadge}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Button(
                    onClick = {
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                        onDismissGuide()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Start Racing!", fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ======================================================================================
// TOP BANNER
// ======================================================================================

@Composable
private fun TutorialTopBanner(
    currentChapter: TutorialChapter,
    totalChapters: Int,
    onQuickClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(currentChapter.themeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentChapter.icon,
                            contentDescription = null,
                            tint = currentChapter.themeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "BEGINNER VISUAL GUIDE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = currentChapter.themeColor
                        )
                        Text(
                            text = currentChapter.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${currentChapter.stepNumber}/$totalChapters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { currentChapter.stepNumber.toFloat() / totalChapters },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = currentChapter.themeColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// ======================================================================================
// CHAPTER 1: APP TOUR & REAL-WORLD 5 TABS
// ======================================================================================

enum class AppTourTab(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: MainNavRoutes,
    val highlightColor: Color,
    val summaryText: String,
    val featureBullets: List<String>
) {
    PADDOCK(
        title = "Paddock",
        subtitle = "Live Race Weekend Hub",
        icon = Icons.Default.Home,
        route = MainNavRoutes.Paddock,
        highlightColor = Color(0xFF00E5FF),
        summaryText = "Your home screen! Tracks upcoming Grand Prix sessions, live countdowns, local circuit weather, and driver leaderboards.",
        featureBullets = listOf(
            "⏱️ Live Race Countdown Clock",
            "⛅ Track Weather & Temperature",
            "📅 FP1, FP2, Qualifying & Race Schedule"
        )
    ),
    GRID(
        title = "Grid",
        subtitle = "Drivers & Telemetry",
        icon = Icons.Default.GridView,
        route = MainNavRoutes.Grid,
        highlightColor = Color(0xFFFF8000),
        summaryText = "Inspect all 20 drivers and 10 teams. Compare any two drivers side-by-side using real telemetry radar charts.",
        featureBullets = listOf(
            "🏎️ 20 Official 2026 Driver Profiles",
            "📊 Head-to-Head Telemetry Comparison",
            "🏆 Constructor Points Standings"
        )
    ),
    CALENDAR(
        title = "Calendar",
        subtitle = "24 Grand Prix Rounds",
        icon = Icons.Default.CalendarMonth,
        route = MainNavRoutes.Calendar,
        highlightColor = Color(0xFFFFD700),
        summaryText = "Browse all 24 global circuits with interactive track maps, lap records, weekend schedules, and sprint formats.",
        featureBullets = listOf(
            "🗺️ Circuit Maps & Sector Speeds",
            "⚡ Sprint Weekend Badges",
            "🏁 Past Winners & Pole Records"
        )
    ),
    RESULTS(
        title = "Results",
        subtitle = "Podiums & Telemetry",
        icon = Icons.Default.DirectionsCar,
        route = MainNavRoutes.F1Results,
        highlightColor = Color(0xFF00E676),
        summaryText = "Official race outcomes! See race winners, podium trophies, fastest lap bonuses (+1 pt), and lap-by-lap timings.",
        featureBullets = listOf(
            "🥇 P1, P2, P3 Podium Finishers",
            "🟣 Fastest Lap Award Tracker",
            "📈 Official FIA Championship Points"
        )
    ),
    TCG(
        title = "F1 TCG",
        subtitle = "Card Packs & Arena Battles",
        icon = Icons.Default.Style,
        route = MainNavRoutes.TcgBinder,
        highlightColor = Color(0xFFFF1744),
        summaryText = "The 3D Formula 1 Trading Card Game! Scratch open packs, collect holographic drivers, assemble squads, and battle!",
        featureBullets = listOf(
            "🎴 Real Scratch-to-Reveal Card Packs",
            "⭐ 32 Collectible 3D Holographic Cards",
            "⚔️ 3v3 Driver & Car Arena Duels"
        )
    )
}

@Composable
private fun InteractiveAppTourChapter(
    onNavigateToRoute: (MainNavRoutes) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var selectedTab by remember { mutableStateOf(AppTourTab.PADDOCK) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Explanatory instruction
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E232E)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Welcome to Taras! 🏎️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The app has 5 main tabs at the bottom bar. Tap any tab below to see what it does!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mini Interactive Phone Screen Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(2.dp, selectedTab.highlightColor.copy(alpha = 0.6f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1218))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header of preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(selectedTab.highlightColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedTab.icon,
                                contentDescription = null,
                                tint = selectedTab.highlightColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = selectedTab.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedTab.subtitle,
                                fontSize = 11.sp,
                                color = selectedTab.highlightColor
                            )
                        }
                    }

                    Button(
                        onClick = {
                            RefreshHapticFeedback.triggerStartHaptic(context, view)
                            onNavigateToRoute(selectedTab.route)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedTab.highlightColor,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Open Screen", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary text
                Text(
                    text = selectedTab.summaryText,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Feature bullet points
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    selectedTab.featureBullets.forEach { bullet ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = bullet,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Bottom Navigation Bar with Pointer!
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1B202B),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppTourTab.entries.forEach { tab ->
                                    val isTabActive = selectedTab == tab
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isTabActive) tab.highlightColor.copy(
                                                    alpha = 0.2f
                                                ) else Color.Transparent
                                            )
                                            .clickable {
                                                RefreshHapticFeedback.triggerStartHaptic(
                                                    context,
                                                    view
                                                )
                                                selectedTab = tab
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = if (isTabActive) tab.highlightColor else Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = tab.title,
                                            fontSize = 9.sp,
                                            fontWeight = if (isTabActive) FontWeight.Black else FontWeight.Normal,
                                            color = if (isTabActive) tab.highlightColor else Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        AnimatedFingerPointer(
                            text = "Tap any tab here to test!",
                            swipeMode = false,
                            color = selectedTab.highlightColor
                        )
                    }
                }
            }
        }
    }
}

// ======================================================================================
// CHAPTER 2: STEP 1 - HOW TO SCRATCH PACKS (MAIN FOCUS)
// ======================================================================================

@Composable
private fun InteractiveScratchChapter(
    norrisCard: TcgCardEntity,
    onOpenRealPacks: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var scratchResetTrigger by remember { mutableIntStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Instruction Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎴 Step 1: Scratch to Reveal Packs!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rub your finger back and forth across the silver foil below — just like real scratch cards!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // THE LIVE SCRATCHABLE CARD DEMO
        Box(
            modifier = Modifier
                .width(260.dp)
                .aspectRatio(5f / 7f)
                .shadow(12.dp, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            key(scratchResetTrigger) {
                ScratchCardReveal(
                    modifier = Modifier.fillMaxSize(),
                    isAlreadyRevealed = isRevealed,
                    onRevealCompleted = {
                        isRevealed = true
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                    }
                ) {
                    CardFrontLayout(
                        card = norrisCard,
                        modifier = Modifier.fillMaxSize(),
                        tiltX = 0f,
                        tiltY = 0f
                    )
                }
            }

            // Animated Hand/Finger Indicator showing kids how to swipe back and forth!
            if (!isRevealed) {
                AnimatedFingerPointer(
                    text = "Swipe finger back & forth here!",
                    swipeMode = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Celebration banner when revealed
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xDD000000),
                    border = BorderStroke(2.dp, Color(0xFFFFD700)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ CARD REVEALED! ✨",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "Lando Norris #1 (96 OVR - S-Tier)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Controls to reset or auto-demonstrate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    isRevealed = false
                    scratchResetTrigger++
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scratch Again", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    isRevealed = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8000),
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Auto-Reveal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Kid-Friendly Visual Steps
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "How Pack Opening Works in Real App:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    Column {
                        Text("Spend Pit Coins", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Earn coins from daily login or winning duels in the arena.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    Column {
                        Text("Scratch the Silver Foil", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Swipe your finger until 50%+ is uncovered to pop the card!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    Column {
                        Text("Collect & Upgrade", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Cards stay permanently in your 3D binder for team building.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onOpenRealPacks,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8000),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Go to Real Pack Opening", fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ======================================================================================
// CHAPTER 3: STEP 2 - CARD ANATOMY & STATS
// ======================================================================================

enum class CardHotspot(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val explanation: String
) {
    TIER(
        title = "Rarity Tier",
        icon = Icons.Default.AutoAwesome,
        color = Color(0xFFFFD700),
        explanation = "🏆 Rarity Tier:\n• S-Tier (95+): Gold border! Legendary champions (Norris 96, Ferrari 98, Mercedes 97).\n• A-Tier (88-94): Cyan border! Elite race winners (Alonso 94, Piastri 91, Bottas 89, Gasly 89).\n• B-Tier (82-87): Violet border! Midfield contenders (Albon 86, Stroll 82, Alpine 84).\n• C-Tier (<=81): Rookie talent (Franco 81, Cadillac 80)."
    ),
    OVR(
        title = "Overall Rating",
        icon = Icons.Default.Speed,
        color = Color(0xFF00E5FF),
        explanation = "⚡ OVR Rating (e.g. 96):\nThe driver or chassis raw speed power! In head-to-head duels, a higher OVR gives your card attack priority and higher critical strike chance."
    ),
    MOVES(
        title = "Battle Attacks",
        icon = Icons.Default.FlashOn,
        color = Color(0xFFFF1744),
        explanation = "💥 Attack Moves:\nEach card has 2 unique moves (e.g. 'Papaya Low-Fuel Sprint'). Look at the power score (96 PWR) and Energy costs (Pace, Racecraft) to calculate your attack!"
    ),
    RADAR(
        title = "Radar Telemetry",
        icon = Icons.Default.SportsMotorsports,
        color = Color(0xFF00E676),
        explanation = "🎯 5 Radar Telemetry Stats:\n• Qualifying Pace\n• Racecraft Defense\n• Tyre Management\n• Wet Weather Skill\n• Lap Consistency"
    )
}

@Composable
private fun InteractiveCardAnatomyChapter(
    card: TcgCardEntity,
    altCard: TcgCardEntity
) {
    val context = LocalContext.current
    val view = LocalView.current
    var selectedHotspot by remember { mutableStateOf(CardHotspot.TIER) }
    var useAltCard by remember { mutableStateOf(false) }
    val displayCard = if (useAltCard) altCard else card

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⭐ Step 2: Know Your Card Stats!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap the glowing badges below to inspect how every stat works in duels!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hotspot selection chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardHotspot.entries.forEach { hotspot ->
                val isHotspotActive = selectedHotspot == hotspot
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isHotspotActive) hotspot.color else MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(1.dp, if (isHotspotActive) Color.White else Color.Transparent),
                    modifier = Modifier.clickable {
                        RefreshHapticFeedback.triggerStartHaptic(context, view)
                        selectedHotspot = hotspot
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = hotspot.icon,
                            contentDescription = null,
                            tint = if (isHotspotActive) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = hotspot.title,
                            fontSize = 12.sp,
                            fontWeight = if (isHotspotActive) FontWeight.Black else FontWeight.Bold,
                            color = if (isHotspotActive) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Display Card with Hotspot Frame
        Box(
            modifier = Modifier
                .width(250.dp)
                .aspectRatio(5f / 7f)
                .shadow(16.dp, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            CardFrontLayout(
                card = displayCard,
                modifier = Modifier.fillMaxSize(),
                tiltX = 0f,
                tiltY = 0f
            )

            // Animated callout pointer overlay on the card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when (selectedHotspot) {
                    CardHotspot.TIER -> {
                        AnimatedFingerPointer(
                            text = "Tier: ${displayCard.tier.name.replace("_TIER", "")}",
                            swipeMode = false,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                    CardHotspot.OVR -> {
                        AnimatedFingerPointer(
                            text = "OVR: ${displayCard.ovr}",
                            swipeMode = false,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                    }
                    CardHotspot.MOVES -> {
                        AnimatedFingerPointer(
                            text = "Moves: ${displayCard.move1.title}",
                            swipeMode = false,
                            color = Color(0xFFFF1744),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    CardHotspot.RADAR -> {
                        AnimatedFingerPointer(
                            text = "5 Radar Stats",
                            swipeMode = false,
                            color = Color(0xFF00E676),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Explanation text card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = selectedHotspot.color.copy(alpha = 0.12f)
            ),
            border = BorderStroke(1.dp, selectedHotspot.color.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = selectedHotspot.icon,
                        contentDescription = null,
                        tint = selectedHotspot.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = selectedHotspot.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedHotspot.explanation,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Switch Card Button to test another driver
        OutlinedButton(
            onClick = {
                RefreshHapticFeedback.triggerStartHaptic(context, view)
                useAltCard = !useAltCard
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(if (useAltCard) "Switch to Lando Norris (96 S-Tier)" else "Switch to Fernando Alonso (94 A-Tier)")
        }
    }
}

// ======================================================================================
// CHAPTER 4: STEP 3 - BUILD YOUR 3-CARD TEAM SQUAD
// ======================================================================================

@Composable
private fun InteractiveTeamBuilderChapter(
    norrisCard: TcgCardEntity,
    piastriCard: TcgCardEntity,
    mclarenCar: TcgCardEntity,
    alonsoCard: TcgCardEntity,
    ferrariCar: TcgCardEntity
) {
    val context = LocalContext.current
    val view = LocalView.current
    var slot1 by remember { mutableStateOf(norrisCard) }
    var slot2 by remember { mutableStateOf(piastriCard) }
    var slot3 by remember { mutableStateOf(mclarenCar) }

    val totalPower = slot1.ovr + slot2.ovr + slot3.ovr
    val hasSynergy = slot1.teamId == slot2.teamId && slot2.teamId == slot3.teamId

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🛡️ Step 3: Build Your 3-Card Lineup!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Every Grand Prix team needs 2 Drivers and 1 Chassis Car. Tap cards below to swap!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SQUAD TEAM POWER GAUGE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1218)),
            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL TEAM SQUAD POWER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E676),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$totalPower PWR",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    if (hasSynergy) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF8000).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFFF8000))
                        ) {
                            Text(
                                text = "🔥 +10% WORKS SYNERGY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF8000),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (totalPower - 240f) / 60f.coerceAtLeast(1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00E676),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 SLOTS DISPLAY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Slot 1
            MiniSlotItem(
                label = "Driver 1",
                card = slot1,
                modifier = Modifier.weight(1f)
            )
            // Slot 2
            MiniSlotItem(
                label = "Driver 2",
                card = slot2,
                modifier = Modifier.weight(1f)
            )
            // Slot 3
            MiniSlotItem(
                label = "Chassis",
                card = slot3,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bench cards to tap & swap
        Text(
            text = "Tap a bench card below to swap into Slot 2 or Chassis:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    slot2 = if (slot2.id == alonsoCard.id) piastriCard else alonsoCard
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF229971),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (slot2.id == alonsoCard.id) "Use Piastri (91)" else "Swap Alonso (94)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    slot3 = if (slot3.id == ferrariCar.id) mclarenCar else ferrariCar
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8002D),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (slot3.id == ferrariCar.id) "Use McLaren (96)" else "Swap Ferrari (98)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        AnimatedFingerPointer(text = "Tap above to see Team Power change!")
    }
}

@Composable
private fun MiniSlotItem(
    label: String,
    card: TcgCardEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B202B)),
        border = BorderStroke(1.5.dp, Color(0xFF00E676).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f / 7f)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                CardFrontLayout(
                    card = card,
                    modifier = Modifier.fillMaxSize(),
                    tiltX = 0f,
                    tiltY = 0f
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${card.ovr} OVR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700)
            )
        }
    }
}

// ======================================================================================
// CHAPTER 5: STEP 4 - BATTLE ARENA DUEL SIMULATOR
// ======================================================================================

@Composable
private fun InteractiveBattleArenaChapter(
    userCard: TcgCardEntity,
    rivalCard: TcgCardEntity,
    onGoToRealArena: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var rivalHp by remember { mutableIntStateOf(100) }
    var isAttacking by remember { mutableStateOf(false) }
    var hasWon by remember { mutableStateOf(false) }
    var lastDamageDealt by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    val animatedRivalHp by animateFloatAsState(
        targetValue = rivalHp.toFloat(),
        animationSpec = tween(500),
        label = "rival_hp"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚔️ Step 4: Battle & Duel in Arena!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pick an attack move below to hit the rival car and deplete their HP bar!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MINI BATTLE DUEL ARENA STAGE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1218)),
            border = BorderStroke(1.5.dp, Color(0xFFFF1744).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HP STATUS BARS (YOU VS RIVAL)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User status
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOU: ${userCard.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                        Text(
                            text = "100 / 100 HP",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00E676),
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "VS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF1744)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Rival status
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RIVAL: ${rivalCard.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252)
                        )
                        Text(
                            text = "$rivalHp / 100 HP",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { (animatedRivalHp / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (rivalHp < 40) Color(0xFFFF1744) else Color(0xFFFF9100),
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CARDS VISUALS FACING OFF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Card
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(5f / 7f)
                            .graphicsLayer {
                                if (isAttacking) {
                                    translationX = 30f
                                    scaleX = 1.05f
                                    scaleY = 1.05f
                                }
                            }
                    ) {
                        CardFrontLayout(
                            card = userCard,
                            modifier = Modifier.fillMaxSize(),
                            tiltX = 0f,
                            tiltY = 0f
                        )
                    }

                    // Impact FX in middle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isAttacking) {
                            Text(
                                text = "💥 BLAST! 💥",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD700)
                            )
                            Text(
                                text = "-$lastDamageDealt HP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF1744)
                            )
                        } else {
                            Text(
                                text = "⚡",
                                fontSize = 28.sp
                            )
                        }
                    }

                    // Rival Card
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(5f / 7f)
                            .graphicsLayer {
                                if (isAttacking) {
                                    translationX = 15f
                                    alpha = 0.85f
                                }
                            }
                    ) {
                        CardFrontLayout(
                            card = rivalCard,
                            modifier = Modifier.fillMaxSize(),
                            tiltX = 0f,
                            tiltY = 0f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ATTACK BUTTON OR VICTORY BANNER
                if (!hasWon) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (isAttacking) return@Button
                                RefreshHapticFeedback.triggerStartHaptic(context, view)
                                coroutineScope.launch {
                                    isAttacking = true
                                    lastDamageDealt = 45
                                    delay(400.milliseconds)
                                    rivalHp = (rivalHp - 45).coerceAtLeast(0)
                                    if (rivalHp <= 0) {
                                        hasWon = true
                                    }
                                    delay(400.milliseconds)
                                    isAttacking = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF1744),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tap Move: ${userCard.move1.title} (${userCard.move1.power} PWR)",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        AnimatedFingerPointer(
                            text = "Tap button above to attack rival!",
                            swipeMode = false
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF00E676).copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆 DUEL VICTORY! +50 PIT COINS! 🏆",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E676)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You knocked out the opponent's card! Knock out all 3 cards to win the race duel.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    rivalHp = 100
                                    hasWon = false
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reset Duel", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onGoToRealArena,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF1744),
                contentColor = Color.White
            )
        ) {
            Text("Open Live Battle Arena", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ======================================================================================
// CHAPTER 6: STEP 5 - DAILY REWARDS & QUICK START
// ======================================================================================

@Composable
private fun InteractiveRewardsPlayChapter(
    onOpenPackOpening: () -> Unit,
    onOpenArena: () -> Unit,
    onOpenBinder: () -> Unit,
    onGoPaddock: () -> Unit,
    onOpenSetup: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var isChestOpened by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏆 Step 5: Claim Rewards & Start Racing!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You're all set! Claim your daily bonus below, then jump right into the game!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // INTERACTIVE DAILY CHEST REWARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    RefreshHapticFeedback.triggerStartHaptic(context, view)
                    isChestOpened = !isChestOpened
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1218)),
            border = BorderStroke(2.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isChestOpened) "🎉 CHEST UNLOCKED!" else "🎁 TAP TO OPEN STARTER CHEST!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isChestOpened) "🪙" else "📦",
                        fontSize = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isChestOpened) {
                    Text(
                        text = "+150 Free Starter Pit Coins Awarded!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                    Text(
                        text = "Use them to open your first F1 Grid card pack!",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else {
                    AnimatedFingerPointer(text = "Tap chest to claim bonus!")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DIRECT LAUNCH ACTION TILES
        Text(
            text = "Where would you like to go first?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionLauncherTile(
                title = "🎴 Open Card Packs",
                description = "Scratch silver foil and collect holographic cards",
                color = Color(0xFFFF8000),
                onClick = onOpenPackOpening
            )

            ActionLauncherTile(
                title = "⚔️ Enter Battle Arena",
                description = "Duel rival teams and win Pit Coins & trophies",
                color = Color(0xFFFF1744),
                onClick = onOpenArena
            )

            ActionLauncherTile(
                title = "📖 View Card Binder",
                description = "Browse, inspect, and sort your 3D card collection",
                color = Color(0xFFFFD700),
                onClick = onOpenBinder
            )

            ActionLauncherTile(
                title = "🏎️ Go to Taras Paddock",
                description = "Live Grand Prix race countdown, weather & schedule",
                color = Color(0xFF00E5FF),
                onClick = onGoPaddock
            )
        }
    }
}

@Composable
private fun ActionLauncherTile(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
