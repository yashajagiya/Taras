package com.example.taras.core.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.taras.core.helpercore.RaceRepository

class NextWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repository = RaceRepository()
        val (raceCurrentState, nextSessionInfo) = repository.getNextRaceData(context)

        provideContent {
            NextRaceWidgetUI(
                raceCurrentState = raceCurrentState,
                nextSessionInfoForWidget = nextSessionInfo
            )
        }
    }
}
