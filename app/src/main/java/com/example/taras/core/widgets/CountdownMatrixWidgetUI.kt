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
                .padding(14.dp)
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
    circuitBitmap: Bitmap?
) {
    val size = LocalSize.current
    val isExtended = size.width >= 250.dp
    val isTall = size.height >= 180.dp
    val context = LocalContext.current

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
                val outlineArgb = GlanceTheme.colors.outline.getColor(context).toArgb()
                val primaryArgb = GlanceTheme.colors.primary.getColor(context).toArgb()

                val matrixBitmap = CountdownMatrixWidgetUtils.renderSeasonMatrixBitmap(
                    rounds = seasonRounds,
                    widthPx = if (isTall) 320 else 280,
                    heightPx = if (isTall) 220 else 180,
                    outlineColorArgb = outlineArgb,
                    activeHighlightArgb = primaryArgb
                )
                Image(
                    provider = ImageProvider(matrixBitmap),
                    contentDescription = "Season Matrix",
                    modifier = GlanceModifier.fillMaxSize()
                )
            }

            Spacer(modifier = GlanceModifier.width(14.dp))

            // Right Pane: Countdown Module
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            ) {
                CountdownModule(
                    raceCurrent = raceCurrent,
                    nextSession = nextSession,
                    circuitBitmap = circuitBitmap,
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
            circuitBitmap = circuitBitmap,
            isExtended = false,
            isTall = isTall
        )
    }
}

/**
 * F1 Countdown Module styled with Taras Material 3 theme colors.
 * Minutes removed to eliminate battery drain; no country flag.
 */
@Composable
private fun CountdownModule(
    raceCurrent: CurrentRace?,
    nextSession: SessionInfo?,
    circuitBitmap: Bitmap?,
    isExtended: Boolean,
    isTall: Boolean
) {
    val raceName = nextSession?.raceName ?: raceCurrent?.raceName ?: "Formula 1 Grand Prix"
    val circuitName = nextSession?.circuitName ?: raceCurrent?.circuitName ?: "Circuit"
    val sessionName = nextSession?.sessionName.orEmpty().ifBlank { "Next Session" }
    val targetInstant = nextSession?.targetInstant

    val (days, hours) = CountdownMatrixWidgetUtils.computeCountdownDh(targetInstant)
    val isLive = nextSession?.countdown?.contains("live", ignoreCase = true) == true ||
            nextSession?.countdown?.contains("started", ignoreCase = true) == true

    val gpTitle = CountdownMatrixWidgetUtils.getShortGpTitle(raceName, nextSession?.circuitName ?: raceCurrent?.circuitName)
    val shortCircuitName = CountdownMatrixWidgetUtils.getShortCircuitName(circuitName)
    val shortDate = CountdownMatrixWidgetUtils.formatShortSessionDate(targetInstant)
    val localTime = CountdownMatrixWidgetUtils.formatLocalSessionTime(targetInstant)

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // 1. Header Row: Grand Prix Name + Session Subtitle (No flag, No emojis)
        HeaderRow(
            gpTitle = gpTitle,
            sessionName = sessionName
        )

        Spacer(modifier = GlanceModifier.height(if (isTall) 10.dp else 6.dp))

        // 2. Center: 2-Column Countdown Pill (Days | Hours) — No minutes to prevent battery drain
        CountdownPill(
            days = days,
            hours = hours,
            isLive = isLive,
            isExtended = isExtended,
            isTall = isTall
        )

        Spacer(modifier = GlanceModifier.height(if (isTall) 10.dp else 6.dp))

        // 3. Footer Row: Real Circuit Track Map + Name (Left) + Local Date & Bold Time (Right Bottom Corner)
        FooterRow(
            circuitShortName = shortCircuitName,
            circuitBitmap = circuitBitmap,
            shortDate = shortDate,
            localTime = localTime,
            isTall = isTall
        )
    }
}

@Composable
private fun HeaderRow(
    gpTitle: String,
    sessionName: String
) {
    Column(
        modifier = GlanceModifier.fillMaxWidth()
    ) {
        Text(
            text = gpTitle,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            maxLines = 1
        )
        Spacer(modifier = GlanceModifier.height(1.dp))
        Text(
            text = sessionName,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp
            ),
            maxLines = 1
        )
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
            .background(GlanceTheme.colors.surfaceVariant)
            .padding(vertical = if (isTall) 12.dp else 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLive) {
            Text(
                text = "SESSION IN PROGRESS",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
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
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isExtended) 20.sp else 23.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "days",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
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
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isExtended) 20.sp else 23.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "hours",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
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
    circuitBitmap: Bitmap?,
    shortDate: String,
    localTime: String,
    isTall: Boolean
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Vertical.Bottom
    ) {
        // Left: Real Circuit Track Map (if loaded) + Circuit Name
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.Vertical.Bottom,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            if (circuitBitmap != null) {
                Image(
                    provider = ImageProvider(circuitBitmap),
                    contentDescription = "Track Map",
                    modifier = GlanceModifier.size(
                        width = if (isTall) 74.dp else 60.dp,
                        height = if (isTall) 40.dp else 30.dp
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
            }
            Text(
                text = circuitShortName,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }

        // Right Bottom Corner: Local Session Date & Bold Time
        Column(
            horizontalAlignment = Alignment.Horizontal.End,
            verticalAlignment = Alignment.Vertical.Bottom
        ) {
            Text(
                text = shortDate,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    textAlign = TextAlign.End
                )
            )
            Spacer(modifier = GlanceModifier.height(1.dp))
            Text(
                text = localTime,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.End
                )
            )
        }
    }
}
