@file:SuppressLint("RestrictedApi")

package com.example.taras.core.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.taras.core.common.UiState
import com.example.taras.view.MainActivity
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo
import kotlin.time.Instant

/**
 * Root Composable for the responsive 2×2, 2×3, 4×2, and 4×3 F1 Countdown & Season Matrix Widget.
 * Fully styled according to the user's Material 3 theme and app colors (GlanceTheme.colors).
 */
@Composable
fun CountdownMatrixWidgetUI(
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfo: SessionInfo?,
    seasonRounds: List<SeasonRoundItem>,
    currentRoundNumber: Int = 1,
    totalRounds: Int = 24,
    hasSprint: Boolean = false,
    laps: Int? = null,
    countryName: String? = null,
    circuitBitmap: Bitmap? = null,
    colors: ColorProviders = rememberWidgetThemeColors(),
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.example.taras.ACTION_OPEN_COUNTDOWN_WIDGET"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("nav_target", "paddock")
    }

    GlanceTheme(colors = colors) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .appWidgetBackground()
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity(launchIntent))
                .padding(
                    if (LocalSize.current.width >= 230.dp) 14.dp
                    else if (LocalSize.current.height >= 170.dp) 12.dp
                    else 10.dp
                )
        ) {
            when (raceCurrentState) {
                is UiState.Loading -> {
                    LoadingView()
                }
                is UiState.Error -> {
                    ErrorView(raceCurrentState.message)
                }
                is UiState.Success -> {
                    SuccessContentView(
                        raceCurrent = raceCurrentState.data,
                        nextSession = nextSessionInfo,
                        seasonRounds = seasonRounds,
                        currentRoundNumber = currentRoundNumber,
                        totalRounds = totalRounds,
                        hasSprint = hasSprint,
                        laps = laps,
                        countryName = countryName,
                        circuitBitmap = circuitBitmap
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = GlanceTheme.colors.primary,
            modifier = GlanceModifier.size(24.dp)
        )
        Spacer(modifier = GlanceModifier.height(6.dp))
        Text(
            text = "Loading…",
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Text(
            text = "No Session Data",
            style = TextStyle(
                color = GlanceTheme.colors.error,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = message,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 10.sp
            ),
            maxLines = 2
        )
    }
}

@Composable
private fun SuccessContentView(
    raceCurrent: CurrentRace?,
    nextSession: SessionInfo?,
    seasonRounds: List<SeasonRoundItem>,
    currentRoundNumber: Int,
    totalRounds: Int,
    hasSprint: Boolean,
    laps: Int?,
    countryName: String?,
    circuitBitmap: Bitmap?
) {
    val size = LocalSize.current
    val isExtended = size.width >= 230.dp
    val isTall = size.height >= 170.dp
    val context = LocalContext.current

    if (isExtended) {
        // Extended 4×2 or 4×3 Dual Pane: Season Matrix on Left + Countdown Module on Right
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // Left Pane: Season Grid with Telemetry Header
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                val completedRaces = seasonRounds.count { !it.winnerNumber.isNullOrBlank() }
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .width(3.dp)
                            .height(10.dp)
                            .cornerRadius(1.5.dp)
                            .background(GlanceTheme.colors.primary)
                    ) {}
                    Spacer(modifier = GlanceModifier.width(5.dp))
                    Text(
                        text = "SEASON GRID • $completedRaces/$totalRounds RACES",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                }

                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val outlineArgb = GlanceTheme.colors.outline.getColor(context).toArgb()
                    val primaryArgb = GlanceTheme.colors.primary.getColor(context).toArgb()

                    val matrixBitmap = CountdownMatrixWidgetUtils.renderSeasonMatrixBitmap(
                        rounds = seasonRounds,
                        widthPx = if (isTall) 300 else 260,
                        heightPx = if (isTall) 190 else 150,
                        outlineColorArgb = outlineArgb,
                        activeHighlightArgb = primaryArgb,
                        totalRounds = totalRounds
                    )
                    Image(
                        provider = ImageProvider(matrixBitmap),
                        contentDescription = "Season Matrix",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = GlanceModifier.width(12.dp))

            // Right Pane: Luxury Countdown Module
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            ) {
                CountdownModule(
                    raceCurrent = raceCurrent,
                    nextSession = nextSession,
                    currentRoundNumber = currentRoundNumber,
                    hasSprint = hasSprint,
                    laps = laps,
                    countryName = countryName,
                    circuitBitmap = circuitBitmap,
                    isExtended = true,
                    isTall = isTall
                )
            }
        }
    } else {
        // Compact 2×2 or 2×3 Card: Luxury Countdown Module Only
        CountdownModule(
            raceCurrent = raceCurrent,
            nextSession = nextSession,
            currentRoundNumber = currentRoundNumber,
            hasSprint = hasSprint,
            laps = laps,
            countryName = countryName,
            circuitBitmap = circuitBitmap,
            isExtended = false,
            isTall = isTall
        )
    }
}

/**
 * Flagship F1 Countdown Module styled with Taras Material 3 theme colors.
 * Elevated with bespoke luxury details:
 * - Round & Session Telemetry Badge (`R14 • PRACTICE 1`) + Sprint Badge (`⚡ SPRINT`)
 * - Precision Swiss Chronograph 2-Column Pod with Hairline Separator (`DAYS` | `HOURS`)
 * - Double-Walled Circuit Silhouette with Official F1 Start/Finish Line Tick
 * - Day-of-week Calendar Date (`FRI, 17 JUL`) + Bold Local Time
 * - High-Contrast Black & White Circuit in Light Theme; Silver & Obsidian in Dark Theme
 * - Laps Telemetry Badge (`Spa • 44L`)
 */
@Composable
private fun CountdownModule(
    raceCurrent: CurrentRace?,
    nextSession: SessionInfo?,
    currentRoundNumber: Int,
    hasSprint: Boolean,
    laps: Int?,
    countryName: String?,
    circuitBitmap: Bitmap?,
    isExtended: Boolean,
    isTall: Boolean
) {
    val context = LocalContext.current
    val raceName = nextSession?.raceName ?: raceCurrent?.raceName ?: "Formula 1 Grand Prix"
    val circuitName = nextSession?.circuitName ?: raceCurrent?.circuitName ?: "Circuit"
    val sessionName = nextSession?.sessionName.orEmpty().ifBlank { "Practice 1" }
    val targetInstant = nextSession?.targetInstant

    // 2-Column Countdown: Days and Hours only (Zero battery drain)
    val (days, hours) = CountdownMatrixWidgetUtils.computeCountdownDh(targetInstant)
    val isLive = nextSession?.countdown?.contains("live", ignoreCase = true) == true ||
            nextSession?.countdown?.contains("started", ignoreCase = true) == true

    val gpTitle = CountdownMatrixWidgetUtils.getShortGpTitle(raceName, nextSession?.circuitName ?: raceCurrent?.circuitName)
    val countryTitle = CountdownMatrixWidgetUtils.getDisplayCountryName(countryName, gpTitle)
    val shortCircuitName = CountdownMatrixWidgetUtils.getShortCircuitName(circuitName)
    val formattedDate = CountdownMatrixWidgetUtils.formatLocalSessionDate(targetInstant)
    val localTime = CountdownMatrixWidgetUtils.formatLocalSessionTime(targetInstant)

    // Dynamic track contrast: Black and White when theme is light, Silver and Black when theme is dark
    val surfaceColor = GlanceTheme.colors.surface.getColor(context)
    val isDarkTheme = (surfaceColor.red * 0.299f + surfaceColor.green * 0.587f + surfaceColor.blue * 0.114f) < 0.5f

    val trackOuterColorArgb = if (isDarkTheme) {
        android.graphics.Color.parseColor("#D5D7DC") // White/light silver outer wall on dark theme
    } else {
        android.graphics.Color.parseColor("#111214") // Black outer wall on light theme
    }

    val trackInnerColorArgb = if (isDarkTheme) {
        android.graphics.Color.parseColor("#111214") // Obsidian cutout core on dark theme
    } else {
        android.graphics.Color.parseColor("#FFFFFF") // White cutout core on light theme
    }

    val primaryArgb = GlanceTheme.colors.primary.getColor(context).toArgb()

    val trackBitmap = circuitBitmap ?: CountdownMatrixWidgetUtils.renderCircuitMapBitmap(
        circuitName = shortCircuitName,
        widthPx = 180,
        heightPx = 110,
        outerStrokeColorArgb = trackOuterColorArgb,
        innerStrokeColorArgb = trackInnerColorArgb,
        startFinishColorArgb = primaryArgb
    )

    val litLights = CountdownMatrixWidgetUtils.computeGantryLights(targetInstant, isLive)

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // 1. Header: Telemetry Round Tag + Sprint Tag + Country Name
        HeaderRow(
            countryName = countryTitle,
            roundNumber = currentRoundNumber,
            hasSprint = hasSprint,
            isLive = isLive,
            isTall = isTall
        )

        Spacer(modifier = GlanceModifier.height(if (isTall) 5.dp else 3.dp))

        // Session progression timeline directly above the countdown pod
        val timeline = CountdownMatrixWidgetUtils.computeWeekendTimeline(
            sessions = raceCurrent?.parsedSessions,
            currentSessionName = sessionName,
            targetInstant = targetInstant,
            isLive = isLive,
            hasSprint = hasSprint
        )
        WeekendTimelineStrip(timeline = timeline)

        Spacer(modifier = GlanceModifier.defaultWeight())

        // 2. Center: Luxury Chronograph Countdown Pod (FIA 5-Lights Gantry + Days | Hours)
        CountdownPill(
            days = days,
            hours = hours,
            litLights = litLights,
            isLive = isLive,
            isExtended = isExtended,
            isTall = isTall
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        // 3. Footer Row: Double-line Circuit Silhouette with Start/Finish Tick + Laps + Date & Time
        FooterRow(
            trackBitmap = trackBitmap,
            circuitShortName = shortCircuitName,
            laps = laps,
            sessionDate = formattedDate,
            localTime = localTime,
            isTall = isTall,
            isExtended = isExtended
        )
    }
}

@Composable
private fun HeaderRow(
    countryName: String,
    roundNumber: Int,
    hasSprint: Boolean,
    isLive: Boolean,
    isTall: Boolean
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // Iconic F1 Red Vertical Telemetry Accent Bar
        Box(
            modifier = GlanceModifier
                .width(3.dp)
                .height(if (isTall) 13.dp else 11.5.dp)
                .cornerRadius(1.5.dp)
                .background(GlanceTheme.colors.primary)
        ) {}

        Spacer(modifier = GlanceModifier.width(5.dp))

        // Precision Round Stadium Badge (e.g. "R14")
        Box(
            modifier = GlanceModifier
                .cornerRadius(999.dp)
                .background(GlanceTheme.colors.primaryContainer)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "R${roundNumber.toString().padStart(2, '0')}",
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            )
        }

        if (hasSprint) {
            Spacer(modifier = GlanceModifier.width(4.dp))
            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.secondaryContainer)
                    .padding(horizontal = 5.5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "SPRINT",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.5.sp
                    )
                )
            }
        }

        if (isLive) {
            Spacer(modifier = GlanceModifier.width(4.dp))
            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.primary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "LIVE",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.5.sp
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.width(6.dp))

        // Country name with broadcast typography
        Text(
            text = countryName.uppercase(),
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = if (isTall) 13.5.sp else 12.sp
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun WeekendTimelineStrip(
    timeline: List<SessionTimelineItem>,
    modifier: GlanceModifier = GlanceModifier
) {
    val size = LocalSize.current
    val isNarrow = size.width < 190.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        timeline.forEachIndexed { index, item ->
            TimelineStageItem(
                item = item,
                hasConnectorAfter = index < timeline.size - 1,
                isNarrow = isNarrow
            )
        }
    }
}

@Composable
private fun TimelineStageItem(
    item: SessionTimelineItem,
    hasConnectorAfter: Boolean,
    isNarrow: Boolean
) {
    Row(
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        val bg = when {
            item.isCurrent -> GlanceTheme.colors.primaryContainer
            item.isCompleted -> GlanceTheme.colors.surfaceVariant
            else -> GlanceTheme.colors.surface
        }
        val textColor = when {
            item.isCurrent -> GlanceTheme.colors.onPrimaryContainer
            item.isCompleted -> GlanceTheme.colors.onSurfaceVariant
            else -> GlanceTheme.colors.outline
        }

        Box(
            modifier = GlanceModifier
                .cornerRadius(999.dp)
                .background(bg)
                .padding(
                    horizontal = if (item.isCurrent) (if (isNarrow) 4.5.dp else 6.dp) else (if (isNarrow) 3.dp else 4.dp),
                    vertical = 1.5.dp
                )
        ) {
            Text(
                text = item.shortCode,
                style = TextStyle(
                    color = textColor,
                    fontWeight = if (item.isCurrent) FontWeight.Bold else FontWeight.Medium,
                    fontSize = if (isNarrow) 7.sp else 7.5.sp
                )
            )
        }

        if (hasConnectorAfter) {
            Spacer(modifier = GlanceModifier.width(if (isNarrow) 1.dp else 2.dp))
            Box(
                modifier = GlanceModifier
                    .width(if (isNarrow) 2.5.dp else 3.5.dp)
                    .height(1.dp)
                    .cornerRadius(0.5.dp)
                    .background(GlanceTheme.colors.outline)
            ) {}
            Spacer(modifier = GlanceModifier.width(if (isNarrow) 1.dp else 2.dp))
        }
    }
}

@Composable
private fun F1StartingGantry(
    litCount: Int,
    isLive: Boolean,
    modifier: GlanceModifier = GlanceModifier
) {
    val size = LocalSize.current
    val isTall = size.height >= 170.dp
    val dotSize = if (isTall) 6.dp else 5.dp
    val dotSpacing = if (isTall) 5.dp else 4.dp

    Row(
        modifier = modifier
            .cornerRadius(999.dp)
            .background(GlanceTheme.colors.surface)
            .padding(
                horizontal = if (isTall) 10.dp else 8.dp,
                vertical = if (isTall) 3.5.dp else 2.5.dp
            ),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        val litColor = GlanceTheme.colors.primary
        val unlitColor = GlanceTheme.colors.outline

        for (i in 1..5) {
            val isLit = i <= litCount
            if (i > 1) {
                Spacer(modifier = GlanceModifier.width(dotSpacing))
            }
            Box(
                modifier = GlanceModifier
                    .size(dotSize)
                    .cornerRadius(999.dp)
                    .background(if (isLit) litColor else unlitColor)
            ) {}
        }
    }
}

@Composable
private fun CountdownPill(
    days: String,
    hours: String,
    litLights: Int,
    isLive: Boolean,
    isExtended: Boolean,
    isTall: Boolean
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(18.dp)
            .background(GlanceTheme.colors.surfaceVariant)
            .padding(vertical = if (isTall) 8.dp else 5.5.dp, horizontal = if (isTall) 12.dp else 10.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // Authentic 5-Lights FIA Starting Gantry inside sculpted housing with un-congested spacing
        F1StartingGantry(
            litCount = litLights,
            isLive = isLive,
            modifier = GlanceModifier.padding(bottom = if (isTall) 5.5.dp else 4.dp)
        )

            if (isLive) {
                Box(
                    modifier = GlanceModifier
                        .cornerRadius(999.dp)
                        .background(GlanceTheme.colors.primary)
                        .padding(horizontal = 10.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = "LIGHTS OUT • TRACK ACTIVE",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isTall) 11.5.sp else 10.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    // Column 1: Days
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = days,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isTall) 21.sp else 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(1.dp))
                        Text(
                            text = "DAYS",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isTall) 8.sp else 7.5.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    // Precision Chronograph Hairline Separator
                    Box(
                        modifier = GlanceModifier
                            .width(1.dp)
                            .height(if (isTall) 22.dp else 18.dp)
                            .background(GlanceTheme.colors.outline)
                    ) {}

                    // Column 2: Hours
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = hours,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isTall) 21.sp else 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(1.dp))
                        Text(
                            text = "HOURS",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isTall) 8.sp else 7.5.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }

@Composable
private fun FooterRow(
    trackBitmap: Bitmap,
    circuitShortName: String,
    laps: Int?,
    sessionDate: String,
    localTime: String,
    isTall: Boolean,
    isExtended: Boolean
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.Bottom
    ) {
        // Left: Circuit Silhouette with Start/Finish tick + Circuit Name & Laps below it
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.Vertical.Bottom,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Image(
                provider = ImageProvider(trackBitmap),
                contentDescription = "Track Map",
                modifier = GlanceModifier.size(
                    width = if (isTall) 62.dp else 48.dp,
                    height = if (isTall) 36.dp else 26.dp
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            val circuitLabel = if (laps != null && laps > 0) "${circuitShortName.uppercase()} • ${laps}L" else circuitShortName.uppercase()
            Text(
                text = circuitLabel,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTall) 9.5.sp else 8.5.sp
                ),
                maxLines = 1
            )
        }

        // Right Bottom: Local Session Day/Date & Bold Time
        Column(
            horizontalAlignment = Alignment.Horizontal.End,
            verticalAlignment = Alignment.Vertical.Bottom
        ) {
            Text(
                text = sessionDate,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isTall) 10.5.sp else 9.sp,
                    textAlign = TextAlign.End
                )
            )
            Spacer(modifier = GlanceModifier.height(1.dp))
            Text(
                text = localTime,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTall) 18.sp else 16.sp,
                    textAlign = TextAlign.End
                )
            )
        }
    }
}
