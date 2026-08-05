package com.example.taras.core.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.ContentScale
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.material3.ColorProviders
import android.graphics.Bitmap
import androidx.glance.Image
import androidx.glance.ImageProvider
import com.example.taras.core.common.UiState
import com.example.taras.ui.theme.DarkColorScheme
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo

private val TarasWidgetColors = ColorProviders(
    light = DarkColorScheme,
    dark = DarkColorScheme
)



@Immutable
data class StableBitmap(val bitmap: Bitmap?)

@Composable
fun NextRaceWidgetUI(
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfoForWidget: SessionInfo?,
    trackBitmap: StableBitmap = StableBitmap(null),
    modifier: GlanceModifier = GlanceModifier
) {
    GlanceTheme(colors = TarasWidgetColors) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
        ) {
            when (raceCurrentState) {
                is UiState.Loading -> {
                    WidgetLoadingView()
                }

                is UiState.Error -> {
                    WidgetErrorView(raceCurrentState.message)
                }

                is UiState.Success -> {
                    WidgetSuccessView(raceCurrentState.data, nextSessionInfoForWidget, trackBitmap)
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
        Text(
            text = "Loading...",
            style = TextStyle(color = GlanceTheme.colors.onBackground)
        )
    }
}

@Composable
private fun WidgetErrorView(message: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "Error",
            style = TextStyle(
                color = GlanceTheme.colors.error,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = message,
            style = TextStyle(color = GlanceTheme.colors.onBackground)
        )
    }
}

@Composable
private fun WidgetSuccessView(
    raceCurrent: CurrentRace?,
    nextSessionInfoForWidget: SessionInfo?,
    trackBitmap: StableBitmap
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.primaryContainer)
            .padding(16.dp)

    ) {
        trackBitmap.bitmap?.let { bitmap ->
            Image(
                provider = ImageProvider(bitmap),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            modifier = GlanceModifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "NEXT SESSION",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = (nextSessionInfoForWidget?.raceName ?: raceCurrent?.raceName)
                        ?.takeIf { it.isNotBlank() } ?: "Upcoming Race",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 2
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = nextSessionInfoForWidget?.countdown ?: "00:00:00",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = if (nextSessionInfoForWidget != null) {
                        "until ${nextSessionInfoForWidget.sessionName} · ${nextSessionInfoForWidget.circuitName}"
                    } else {
                        "No upcoming sessions · ${raceCurrent?.circuitName ?: "N/A"}"
                    },
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = 14.sp
                    )
                )
            }

        }
    }
}
