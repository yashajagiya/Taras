package com.example.taras.core.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.taras.core.common.UserPreferences
import com.example.taras.core.helpercore.RaceRepository
import kotlinx.coroutines.flow.firstOrNull

class NextWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repository = RaceRepository()
        val (raceCurrentState, nextSessionInfo) = repository.getNextRaceData(context)

        val userPreferences = UserPreferences(context)
        val favDriverName = userPreferences.favoriteDriverNameFlow.firstOrNull()
        val favDriverRank = userPreferences.favoriteDriverRankFlow.firstOrNull()
        val favDriverPoints = userPreferences.favoriteDriverPointsFlow.firstOrNull()

        val favoriteDriverInfo = if (!favDriverName.isNullOrBlank()) {
            FavoriteDriverWidgetInfo(
                name = favDriverName,
                position = if (!favDriverRank.isNullOrBlank()) "P$favDriverRank" else "",
                points = favDriverPoints?.takeIf { it.isNotBlank() }?.let { "$it pts" }
            )
        } else null

        provideContent {
            NextRaceWidgetUI(
                raceCurrentState = raceCurrentState,
                nextSessionInfoForWidget = nextSessionInfo,
                favoriteDriverInfo = favoriteDriverInfo
            )
        }
    }
}
