package com.example.taras.core.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.taras.core.helpercore.RaceRepository

class CountdownMatrixWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

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

        provideContent {
            val colors = rememberWidgetThemeColors()
            CountdownMatrixWidgetUI(
                raceCurrentState = raceCurrentState,
                nextSessionInfo = nextSessionInfo,
                seasonRounds = seasonRounds,
                colors = colors
            )
        }
    }
}
