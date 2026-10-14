@file:SuppressLint("RestrictedApi")

package com.example.taras.core.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.glance.unit.ColorProvider
import com.example.taras.core.common.UiState
import com.example.taras.view.MainActivity
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo
import kotlin.time.Instant

// Exact Obsidian Obsidian Design Tokens from Spec
private val ObsidianSurfaceColor = Color(0xFF111214)
private val ObsidianPillColor = Color(0xFF1C1D20)
private val ObsidianWhiteText = Color(0xFFF5F6F8)
private val ObsidianMutedText = Color(0xFF93969C)
private val ObsidianF1Red = Color(0xFFFF1801)

/**
 * Root Composable for the responsive 2×2, 2×3, 4×2, and 4×3 F1 Countdown & Season Matrix Widget.
 */
@Composable
fun CountdownMatrixWidgetUI(
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfo: SessionInfo?,
    seasonRounds: List<SeasonRoundItem>,
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
                .cornerRadius(32.dp)
                .background(ColorProvider(ObsidianSurfaceColor))
                .clickable(actionStartActivity(launchIntent))
                .padding(18.dp)
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
                        seasonRounds = seasonRounds
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
            color = ColorProvider(ObsidianWhiteText),
            modifier = GlanceModifier.size(26.dp)
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "Loading…",
            style = TextStyle(
                color = ColorProvider(ObsidianMutedText),
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
                color = ColorProvider(ObsidianF1Red),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = message,
            style = TextStyle(
                color = ColorProvider(ObsidianMutedText),
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
    seasonRounds: List<SeasonRoundItem>
) {
    val size = LocalSize.current
    val isExtended = size.width >= 250.dp
    val isTall = size.height >= 180.dp

    if (isExtended) {
        // Extended 4×2 or 4×3 Dual Pane: Season Matrix on Left + Countdown Module on Right
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // Left Pane: 6x4 Season Winner Matrix
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val matrixBitmap = CountdownMatrixWidgetUtils.renderSeasonMatrixBitmap(
                    rounds = seasonRounds,
                    widthPx = if (isTall) 320 else 280,
                    heightPx = if (isTall) 220 else 180
                )
                Image(
                    provider = ImageProvider(matrixBitmap),
                    contentDescription = "Season Matrix",
                    modifier = GlanceModifier.fillMaxSize()
                )
            }

            Spacer(modifier = GlanceModifier.width(16.dp))

            // Right Pane: Countdown Module (Header, Pill, Track Map + Date/Time)
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            ) {
                CountdownModule(
                    raceCurrent = raceCurrent,
                    nextSession = nextSession,
                    isExtended = true,
                    isTall = isTall
                )
            }
        }
    } else {
        // Compact 2×2 or 2×3 Card: Countdown Module Only
        CountdownModule(
            raceCurrent = raceCurrent,
            nextSession = nextSession,
            isExtended = false,
            isTall = isTall
        )
    }
}

/**
 * Clean F1 Countdown Module matching exact visual spec from Image 2 and Image 3.
 */
@Composable
private fun CountdownModule(
    raceCurrent: CurrentRace?,
    nextSession: SessionInfo?,
    isExtended: Boolean,
    isTall: Boolean
) {
    val raceName = nextSession?.raceName ?: raceCurrent?.raceName ?: "Belgian Grand Prix"
    val circuitName = nextSession?.circuitName ?: raceCurrent?.circuitName ?: "Circuit de Spa-Francorchamps"
    val sessionName = nextSession?.sessionName.orEmpty().ifBlank { "Practice 1" }
    val targetInstant = nextSession?.targetInstant

    val (days, hours) = CountdownMatrixWidgetUtils.computeCountdownDh(targetInstant)
    val isLive = nextSession?.countdown?.contains("live", ignoreCase = true) == true ||
            nextSession?.countdown?.contains("started", ignoreCase = true) == true

    val gpTitle = CountdownMatrixWidgetUtils.getShortGpTitle(raceName, nextSession?.circuitName ?: raceCurrent?.circuitName)
    val shortCircuitName = CountdownMatrixWidgetUtils.getShortCircuitName(circuitName)
    val shortDate = CountdownMatrixWidgetUtils.formatShortSessionDate(targetInstant)
    val localTime = CountdownMatrixWidgetUtils.formatLocalSessionTime(targetInstant)

    val flagBitmap = CountdownMatrixWidgetUtils.renderCountryFlagBitmap(
        countryOrGpName = gpTitle,
        widthPx = 56,
        heightPx = 38
    )

    val trackMapBitmap = CountdownMatrixWidgetUtils.renderCircuitMapBitmap(
        circuitName = circuitName,
        widthPx = if (isTall) 200 else 170,
        heightPx = if (isTall) 124 else 104
    )

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // 1. Header: Flag + Short Title (BELGIAN) + Session Subtitle (Practice 1)
        HeaderRow(
            gpTitle = gpTitle,
            sessionName = sessionName,
            flagBitmap = flagBitmap
        )

        Spacer(modifier = GlanceModifier.height(if (isTall) 14.dp else 10.dp))

        // 2. Center: 2-Column Countdown Pill (Days | Hours) — Minutes removed to prevent battery drain
        CountdownPill(
            days = days,
            hours = hours,
            isLive = isLive,
            isExtended = isExtended,
            isTall = isTall
        )

        Spacer(modifier = GlanceModifier.height(if (isTall) 14.dp else 10.dp))

        // 3. Footer: Double-Walled Track Map with Short Name (Left) + Local Date & Time (Right)
        FooterRow(
            circuitShortName = shortCircuitName,
            trackMapBitmap = trackMapBitmap,
            shortDate = shortDate,
            localTime = localTime,
            isExtended = isExtended,
            isTall = isTall
        )
    }
}

@Composable
private fun HeaderRow(
    gpTitle: String,
    sessionName: String,
    flagBitmap: Bitmap
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Image(
            provider = ImageProvider(flagBitmap),
            contentDescription = "Country Flag",
            modifier = GlanceModifier
                .size(width = 28.dp, height = 19.dp)
                .cornerRadius(4.dp)
        )
        Spacer(modifier = GlanceModifier.width(10.dp))
        Column {
            Text(
                text = gpTitle,
                style = TextStyle(
                    color = ColorProvider(ObsidianWhiteText),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                maxLines = 1
            )
            Text(
                text = sessionName,
                style = TextStyle(
                    color = ColorProvider(ObsidianMutedText),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CountdownPill(
    days: String,
    hours: String,
    isLive: Boolean,
    isExtended: Boolean,
    isTall: Boolean
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(18.dp)
            .background(ColorProvider(ObsidianPillColor))
            .padding(vertical = if (isTall) 14.dp else 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLive) {
            Text(
                text = "SESSION IN PROGRESS",
                style = TextStyle(
                    color = ColorProvider(ObsidianF1Red),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            )
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
                            color = ColorProvider(ObsidianWhiteText),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isExtended) 21.sp else 25.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "days",
                        style = TextStyle(
                            color = ColorProvider(ObsidianMutedText),
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                // Column 2: Hours
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = hours,
                        style = TextStyle(
                            color = ColorProvider(ObsidianWhiteText),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isExtended) 21.sp else 25.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "hours",
                        style = TextStyle(
                            color = ColorProvider(ObsidianMutedText),
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
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
    circuitShortName: String,
    trackMapBitmap: Bitmap,
    shortDate: String,
    localTime: String,
    isExtended: Boolean,
    isTall: Boolean
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.Bottom
    ) {
        // Left: Circuit Track Silhouette Map + Track Name
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.Vertical.Bottom,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Image(
                provider = ImageProvider(trackMapBitmap),
                contentDescription = "Track Map",
                modifier = GlanceModifier.size(
                    width = if (isTall) 98.dp else (if (isExtended) 84.dp else 92.dp),
                    height = if (isTall) 60.dp else (if (isExtended) 52.dp else 56.dp)
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = circuitShortName,
                style = TextStyle(
                    color = ColorProvider(ObsidianMutedText),
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
        }

        // Right: Local Date & Bold Local Time
        Column(
            horizontalAlignment = Alignment.Horizontal.End,
            verticalAlignment = Alignment.Vertical.Bottom
        ) {
            Text(
                text = shortDate,
                style = TextStyle(
                    color = ColorProvider(ObsidianMutedText),
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End
                )
            )
            Spacer(modifier = GlanceModifier.height(1.dp))
            Text(
                text = localTime,
                style = TextStyle(
                    color = ColorProvider(ObsidianWhiteText),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isExtended) 18.sp else 21.sp,
                    textAlign = TextAlign.End
                )
            )
        }
    }
}
