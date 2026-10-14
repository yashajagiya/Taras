package com.example.taras.core.widgets

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.taras.core.helpercore.RaceRepository

class CountdownMatrixWidget : GlanceAppWidget() {

    companion object {
        val COMPACT_2X2 = DpSize(140.dp, 140.dp)
        val TALL_2X3 = DpSize(140.dp, 210.dp)
        val EXTENDED_4X2 = DpSize(260.dp, 140.dp)
        val LARGE_4X3 = DpSize(260.dp, 210.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(COMPACT_2X2, TALL_2X3, EXTENDED_4X2, LARGE_4X3)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = RaceRepository()
        val (raceCurrentState, nextSessionInfo, calendarEvents) = repository.getOfflineResilientCalendar(context)

        val currentRoundNum = nextSessionInfo?.roundNumber ?: 1
        val seasonRounds = calendarEvents.map { event ->
            SeasonRoundItem(
                roundNumber = event.round,
                winnerNumber = event.winner?.drivernumber?.toString(),
                teamColor = getTeamColor(event.winner?.teamWinner),
                isCurrentOrNext = (event.round == currentRoundNum)
            )
        }

        // Schedule zero-battery milestone alarm for exact session start
        CountdownMatrixWidgetUtils.scheduleNextSessionMilestoneAlarm(context, nextSessionInfo?.targetInstant)

        val colors = getWidgetThemeColors(context)

        // Load real official F1 circuit track outline image if available
        val trackImageUrl = calendarEvents.find { it.round == currentRoundNum }?.circuit?.trackImage
            ?: nextSessionInfo?.circuitName?.let { cName ->
                calendarEvents.find { it.circuit.name.contains(cName, ignoreCase = true) }?.circuit?.trackImage
            }
        val circuitBitmap = CountdownMatrixWidgetUtils.loadCircuitBitmap(context, trackImageUrl)

        provideContent {
            CountdownMatrixWidgetUI(
                raceCurrentState = raceCurrentState,
                nextSessionInfo = nextSessionInfo,
                seasonRounds = seasonRounds,
                circuitBitmap = circuitBitmap,
                colors = colors
            )
        }
    }
}
