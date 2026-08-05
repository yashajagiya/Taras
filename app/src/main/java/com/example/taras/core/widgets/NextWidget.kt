package com.example.taras.core.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.taras.viewmodel.RaceRepository
import coil3.ImageLoader
import coil3.request.*
import coil3.Extras
import coil3.toBitmap
import android.graphics.Bitmap
import android.util.Log
import com.example.taras.core.common.UiState

class NextWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repository = RaceRepository()
        val (raceCurrentState, nextSessionInfo) = repository.getNextRaceData()

        val trackBitmap = if (raceCurrentState is UiState.Success) {
            raceCurrentState.data?.trackImage?.let { url ->
                fetchImageAsBitmap(context, url)
            }
        } else null

        provideContent {
            NextRaceWidgetUI(
                raceCurrentState = raceCurrentState,
                nextSessionInfoForWidget = nextSessionInfo,
                trackBitmap = StableBitmap(trackBitmap)
            )
        }
    }

    private suspend fun fetchImageAsBitmap(context: Context, url: String): Bitmap? {
        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .apply {
                    extras[Extras.Key.allowHardware] = false
                }
                .size(200, 200) // Resize for RemoteViews
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                result.image.toBitmap()
            } else null
        } catch (e: Exception) {
            Log.e("widgets", "Error fetching image data", e)
            null
        }
    }
}
