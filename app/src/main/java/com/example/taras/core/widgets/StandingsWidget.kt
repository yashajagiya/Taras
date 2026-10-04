@file:SuppressLint("RestrictedApi")

package com.example.taras.core.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.taras.view.MainActivity

class StandingsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = WidgetDataRepository()
        val standings = repository.getTopThreeStandings(context)
        provideContent {
            val colors = rememberWidgetThemeColors()
            StandingsWidgetUI(standings = standings, colors = colors)
        }
    }
}

class StandingsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = StandingsWidget()
}

@Composable
fun StandingsWidgetUI(
    standings: List<TopDriverWidgetItem>,
    colors: ColorProviders = rememberWidgetThemeColors(),
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.example.taras.ACTION_OPEN_STANDINGS"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("nav_target", "grid")
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
            if (standings.isEmpty()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    CircularProgressIndicator(color = GlanceTheme.colors.primary)
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = "Loading Standings...",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else {
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
                                text = "🏆 TOP 3 DRIVERS",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
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
                                text = "FULL GRID ›",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Top 3 Drivers with Team Color Strips & Medals
                    val top3 = standings.take(3)
                    top3.forEachIndexed { index, driver ->
                        val isLeader = index == 0
                        val cardBg = if (isLeader) {
                            GlanceTheme.colors.primaryContainer
                        } else {
                            GlanceTheme.colors.surfaceVariant
                        }

                        val badgeBg = when (index) {
                            0 -> WidgetGoldProvider
                            1 -> WidgetSilverProvider
                            else -> WidgetBronzeProvider
                        }

                        val badgeTextColor = if (index < 2) {
                            WidgetDarkColorProvider
                        } else {
                            WidgetWhiteColorProvider
                        }

                        val nameColor = if (isLeader) {
                            GlanceTheme.colors.onPrimaryContainer
                        } else {
                            GlanceTheme.colors.onSurface
                        }

                        val teamSubColor = if (isLeader) {
                            GlanceTheme.colors.primary
                        } else {
                            GlanceTheme.colors.onSurfaceVariant
                        }

                        Box(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .cornerRadius(if (isLeader) 14.dp else 12.dp)
                                .background(cardBg)
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Vertical.CenterVertically
                            ) {
                                // Podium Position Badge
                                Box(
                                    modifier = GlanceModifier
                                        .cornerRadius(999.dp)
                                        .background(badgeBg)
                                        .size(if (isLeader) 24.dp else 22.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${driver.position}",
                                        style = TextStyle(
                                            color = badgeTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Spacer(modifier = GlanceModifier.width(6.dp))

                                // Official Team Color Strip
                                Box(
                                    modifier = GlanceModifier
                                        .width(if (isLeader) 4.dp else 3.dp)
                                        .height(if (isLeader) 26.dp else 22.dp)
                                        .cornerRadius(2.dp)
                                        .background(getTeamColorProvider(driver.team))
                                ) {}

                                Spacer(modifier = GlanceModifier.width(6.dp))

                                // Driver Name and Constructor Team
                                Column(modifier = GlanceModifier.defaultWeight()) {
                                    Text(
                                        text = driver.name,
                                        style = TextStyle(
                                            color = nameColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = if (isLeader) 13.sp else 12.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = driver.team,
                                        style = TextStyle(
                                            color = teamSubColor,
                                            fontSize = 9.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                // Points Chip
                                Box(
                                    modifier = GlanceModifier
                                        .cornerRadius(999.dp)
                                        .background(if (isLeader) GlanceTheme.colors.primary else GlanceTheme.colors.surface)
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = driver.points,
                                        style = TextStyle(
                                            color = if (isLeader) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (index < top3.size - 1) {
                            Spacer(modifier = GlanceModifier.height(3.dp))
                        }
                    }
                }
            }
        }
    }
}
