@file:SuppressLint("RestrictedApi")

package com.example.taras.core.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
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
 * Root Composable for the responsive 2×2 / 4×2 Countdown & Season Matrix Widget.
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
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity(launchIntent))
                .padding(10.dp)
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
            color = GlanceTheme.colors.primary,
            modifier = GlanceModifier.size(24.dp)
        )
        Spacer(modifier = GlanceModifier.height(6.dp))
        Text(
            text = "Loading Countdown…",
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
            text = "Unable to Load Session",
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
    seasonRounds: List<SeasonRoundItem>
) {
    val size = LocalSize.current
    val isExtended = size.width >= 250.dp

    if (isExtended) {
        // Extended 4×2 Layout: Season Winner Matrix (Left) + Countdown & Local Time (Right)
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // Left Pane: Season Winner Matrix Bitmap
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .cornerRadius(16.dp)
                    .background(GlanceTheme.colors.surfaceVariant)
                    .padding(8.dp)
            ) {
                Column(modifier = GlanceModifier.fillMaxSize()) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = "🏁 SEASON MATRIX",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = "24 ROUNDS",
                            style = TextStyle(
                                color = GlanceTheme.colors.outline,
                                fontWeight = FontWeight.Medium,
                                fontSize = 8.sp
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    SeasonMatrixBitmapImage(
                        rounds = seasonRounds,
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            // Right Pane: Countdown Module with Local Date/Time in Right Bottom Corner
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            ) {
                CountdownModule(
                    raceCurrent = raceCurrent,
                    nextSession = nextSession,
                    isCompact = true
                )
            }
        }
    } else {
        // Compact 2×2 Layout: Full Countdown with Right Bottom Corner Local Date/Time
        CountdownModule(
            raceCurrent = raceCurrent,
            nextSession = nextSession,
            isCompact = false
        )
    }
}

/**
 * Reusable Countdown Module for both 2×2 and 4×2 modes.
 */
@Composable
private fun CountdownModule(
    raceCurrent: CurrentRace?,
    nextSession: SessionInfo?,
    isCompact: Boolean
) {
    val raceName = nextSession?.raceName ?: raceCurrent?.raceName ?: "Formula 1"
    val circuitName = nextSession?.circuitName ?: raceCurrent?.circuitName ?: "World Championship"
    val sessionName = nextSession?.sessionName.orEmpty().ifBlank { "Next Session" }
    val targetInstant = nextSession?.targetInstant

    val (days, hours, minutes) = CountdownMatrixWidgetUtils.computeCountdownDhm(targetInstant)
    val isLive = nextSession?.countdown?.contains("live", ignoreCase = true) == true ||
            nextSession?.countdown?.contains("started", ignoreCase = true) == true

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // Header Row: Badge Pills
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🏎️ COUNTDOWN",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.width(4.dp))

            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(if (isLive) GlanceTheme.colors.primary else GlanceTheme.colors.surfaceVariant)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isLive) "🟢 LIVE NOW" else "⏱️ ${sessionName.uppercase()}",
                    style = TextStyle(
                        color = if (isLive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        // Grand Prix Title
        Text(
            text = raceName,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 12.sp else 13.sp
            ),
            maxLines = 1
        )

        Spacer(modifier = GlanceModifier.height(5.dp))

        // 3-Column Split Digital Countdown Pill
        SplitDigitalCountdownPill(
            days = days,
            hours = hours,
            minutes = minutes,
            isLive = isLive,
            isCompact = isCompact
        )

        Spacer(modifier = GlanceModifier.height(5.dp))

        // Footer Row: Circuit Name (Left) + Upcoming Session Local Date & Time (Right Bottom Corner)
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.Bottom
        ) {
            // Left: Circuit
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.Bottom
            ) {
                Text(
                    text = "🏁 $circuitName",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )
            }

            // Right Bottom Corner: Local Date & Bold Local Time
            LocalSessionDateTimePill(targetInstant = targetInstant)
        }
    }
}

/**
 * 3-Column Split Countdown Pill displaying Days, Hours, and Minutes.
 */
@Composable
private fun SplitDigitalCountdownPill(
    days: String,
    hours: String,
    minutes: String,
    isLive: Boolean,
    isCompact: Boolean
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(14.dp)
            .background(GlanceTheme.colors.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLive) {
            Text(
                text = "SESSION IN PROGRESS",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        } else {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                TimeUnitColumn(value = days, label = "days", isCompact = isCompact)
                Text(
                    text = " : ",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 14.sp else 16.sp
                    )
                )
                TimeUnitColumn(value = hours, label = "hours", isCompact = isCompact)
                Text(
                    text = " : ",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 14.sp else 16.sp
                    )
                )
                TimeUnitColumn(value = minutes, label = "mins", isCompact = isCompact)
            }
        }
    }
}

@Composable
private fun TimeUnitColumn(
    value: String,
    label: String,
    isCompact: Boolean
) {
    Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
        Text(
            text = value,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 15.sp else 18.sp
            )
        )
        Text(
            text = label,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
                fontSize = 8.sp
            )
        )
    }
}

/**
 * Dedicated Right-Bottom-Corner Composable showing upcoming session local date and bold local time.
 */
@Composable
fun LocalSessionDateTimePill(
    targetInstant: Instant?,
    modifier: GlanceModifier = GlanceModifier
) {
    val localDateStr = CountdownMatrixWidgetUtils.formatLocalSessionDate(targetInstant)
    val localTimeStr = CountdownMatrixWidgetUtils.formatLocalSessionTime(targetInstant)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Horizontal.End
    ) {
        Text(
            text = localDateStr,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 9.sp
            )
        )
        Text(
            text = localTimeStr,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}

/**
 * Composable that displays the Canvas-rendered Season Winner Matrix.
 */
@Composable
private fun SeasonMatrixBitmapImage(
    rounds: List<SeasonRoundItem>,
    modifier: GlanceModifier = GlanceModifier
) {
    val bitmap = CountdownMatrixWidgetUtils.renderSeasonMatrixBitmap(
        rounds = rounds,
        widthPx = 280,
        heightPx = 180
    )

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = "F1 Season Round Matrix",
        modifier = modifier
    )
}
