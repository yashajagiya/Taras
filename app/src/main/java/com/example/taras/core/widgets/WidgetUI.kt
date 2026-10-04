package com.example.taras.core.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.color.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.taras.core.common.UiState
import com.example.taras.ui.theme.DarkColorScheme
import com.example.taras.ui.theme.LightColorScheme
import com.example.taras.view.MainActivity
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo

val TarasM3WidgetColors: ColorProviders = androidx.glance.material3.ColorProviders(
    light = LightColorScheme,
    dark = DarkColorScheme
)

data class FavoriteDriverWidgetInfo(
    val name: String,
    val position: String,
    val points: String? = null
)

@Composable
fun NextRaceWidgetUI(
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfoForWidget: SessionInfo?,
    favoriteDriverInfo: FavoriteDriverWidgetInfo? = null,
    colors: ColorProviders = rememberWidgetThemeColors(),
    modifier: GlanceModifier = GlanceModifier
) {
    val context = androidx.glance.LocalContext.current
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.example.taras.ACTION_OPEN_NEXT_RACE"
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
                .padding(12.dp)
        ) {
            when (raceCurrentState) {
                is UiState.Loading -> {
                    WidgetLoadingView()
                }

                is UiState.Error -> {
                    WidgetErrorView(raceCurrentState.message)
                }

                is UiState.Success -> {
                    WidgetSuccessView(
                        raceCurrent = raceCurrentState.data,
                        nextSessionInfoForWidget = nextSessionInfoForWidget,
                        favoriteDriverInfo = favoriteDriverInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetLoadingView() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        CircularProgressIndicator(color = GlanceTheme.colors.primary)
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "Loading Next Session...",
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun WidgetErrorView(message: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "Unable to load session",
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
private fun WidgetSuccessView(
    raceCurrent: CurrentRace?,
    nextSessionInfoForWidget: SessionInfo?,
    favoriteDriverInfo: FavoriteDriverWidgetInfo? = null
) {
    val countdownText = nextSessionInfoForWidget?.countdown ?: "Season Break"
    val isLive = countdownText.contains("live", ignoreCase = true) ||
            countdownText.contains("now", ignoreCase = true) ||
            countdownText.contains("progress", ignoreCase = true)

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // Expressive Header Row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "🏎️ NEXT SESSION",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.width(6.dp))

            // Dynamic Live / Upcoming Status Pill
            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(if (isLive) GlanceTheme.colors.primary else GlanceTheme.colors.surfaceVariant)
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isLive) "🟢 LIVE" else "⏱️ UPCOMING",
                    style = TextStyle(
                        color = if (isLive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Box(
                modifier = GlanceModifier
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "PADDOCK ›",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(6.dp))

        // Race Grand Prix Title
        val raceTitle = (nextSessionInfoForWidget?.raceName ?: raceCurrent?.raceName)
            ?.takeIf { it.isNotBlank() } ?: "Upcoming Grand Prix"

        Text(
            text = raceTitle,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            maxLines = 1
        )

        Spacer(modifier = GlanceModifier.height(5.dp))

        // M3 Elevated Countdown Card
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(16.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Column {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = countdownText,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    if (nextSessionInfoForWidget != null && nextSessionInfoForWidget.sessionName.isNotBlank()) {
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(999.dp)
                                .background(GlanceTheme.colors.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = nextSessionInfoForWidget.sessionName.uppercase(),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.height(3.dp))

                val sessionDetails = if (nextSessionInfoForWidget != null) {
                    "🏁 ${nextSessionInfoForWidget.circuitName}"
                } else {
                    raceCurrent?.circuitName ?: "Season Break"
                }
                Text(
                    text = sessionDetails,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )
            }
        }

        // Optional Favorite Driver Highlight Pill
        if (favoriteDriverInfo != null && favoriteDriverInfo.name.isNotBlank()) {
            Spacer(modifier = GlanceModifier.height(5.dp))
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .cornerRadius(999.dp)
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                    Text(
                        text = "⭐ ${favoriteDriverInfo.name} · ${favoriteDriverInfo.position}" +
                                (favoriteDriverInfo.points?.let { " ($it)" } ?: ""),
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
