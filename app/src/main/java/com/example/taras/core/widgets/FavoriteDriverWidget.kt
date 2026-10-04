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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.taras.view.MainActivity

class FavoriteDriverWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = WidgetDataRepository()
        val data = repository.getFavoriteDriverData(context)
        provideContent {
            val colors = rememberWidgetThemeColors()
            FavoriteDriverWidgetUI(data = data, colors = colors)
        }
    }
}

class FavoriteDriverWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FavoriteDriverWidget()
}

@Composable
fun FavoriteDriverWidgetUI(
    data: FavoriteDriverWidgetData,
    colors: ColorProviders = rememberWidgetThemeColors(),
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.example.taras.ACTION_OPEN_FAVORITE_DRIVER"
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
            if (!data.hasFavorite || data.name.isNullOrBlank()) {
                // Friendly Empty / Onboarding State
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(999.dp)
                            .background(GlanceTheme.colors.primaryContainer)
                            .size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⭐",
                            style = TextStyle(fontSize = 20.sp)
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Text(
                        text = "Pick Your Favorite Driver",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    Text(
                        text = "Select a driver in Taras to track rank, points, and race results",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(999.dp)
                            .background(GlanceTheme.colors.primary)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Choose Driver ›",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            } else {
                // Driver Selected State
                val teamColorProvider = getTeamColorProvider(data.team)

                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    // Header Row: Category Badge + Driver Racing Number + Profile Chip
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(999.dp)
                                .background(GlanceTheme.colors.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⭐ FAVORITE DRIVER",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }

                        if (!data.number.isNullOrBlank()) {
                            Spacer(modifier = GlanceModifier.width(6.dp))
                            Box(
                                modifier = GlanceModifier
                                    .cornerRadius(999.dp)
                                    .background(teamColorProvider)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "#${data.number}",
                                    style = TextStyle(
                                        color = WidgetWhiteColorProvider,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(999.dp)
                                .background(GlanceTheme.colors.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PROFILE ›",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(5.dp))

                    // Driver Identity Row with Official Team Color Accent Strip
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .width(4.dp)
                                .height(28.dp)
                                .cornerRadius(2.dp)
                                .background(teamColorProvider)
                        ) {}

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = data.name,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                maxLines = 1
                            )
                            if (!data.team.isNullOrBlank()) {
                                Text(
                                    text = data.team,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurfaceVariant,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Bento Stats Row: Position & Points
                    val cleanRank = data.rank?.let {
                        if (it.startsWith("P", ignoreCase = true)) it else "P$it"
                    } ?: "N/A"

                    val ptsDisplay = data.points?.takeIf { it.isNotBlank() }?.let {
                        if (it.endsWith("pts", ignoreCase = true)) it else "$it pts"
                    } ?: "--"

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        // Position Bento Card
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .cornerRadius(14.dp)
                                .background(GlanceTheme.colors.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Column {
                                Text(
                                    text = "STANDINGS",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                )
                                Text(
                                    text = cleanRank,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = GlanceModifier.width(6.dp))

                        // Points Bento Card
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .cornerRadius(14.dp)
                                .background(GlanceTheme.colors.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL POINTS",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                )
                                Text(
                                    text = ptsDisplay,
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(5.dp))

                    // Last Race Result Bento Card
                    val lastRacePosDisplay = data.lastRacePosition?.let {
                        if (it.startsWith("P", ignoreCase = true)) it else "P$it"
                    } ?: "N/A"

                    val lastRacePtsDisplay = if (!data.lastRacePoints.isNullOrBlank()) {
                        "+${data.lastRacePoints} pts"
                    } else data.lastRaceTime ?: ""

                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .cornerRadius(14.dp)
                            .background(GlanceTheme.colors.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = "LAST RACE · ${data.lastRaceName ?: "Grand Prix"}",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = if (lastRacePosDisplay != "N/A") "🏁 Finished $lastRacePosDisplay" else "🏁 Result Pending",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            if (lastRacePtsDisplay.isNotBlank()) {
                                Box(
                                    modifier = GlanceModifier
                                        .cornerRadius(999.dp)
                                        .background(GlanceTheme.colors.primary)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = lastRacePtsDisplay,
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
