package com.example.taras.core.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CountdownMatrixWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CountdownMatrixWidget()

    override fun onReceive(context: Context, intent: Intent) {
        // Sanitize widget IDs to prevent Glance crashing on deleted/orphaned widget IDs
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (ids != null) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val validIds = ids.filter { appWidgetManager.getAppWidgetInfo(it) != null }.toIntArray()
                if (validIds.isEmpty()) return
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, validIds)
            }
        }
        try {
            super.onReceive(context, intent)
        } catch (_: Throwable) {
            // Silently absorb orphaned widget lifecycle exceptions (e.g. IllegalArgumentException: No app widget info for X)
        }
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                MainScope().launch {
                    try {
                        val manager = GlanceAppWidgetManager(context)
                        val glanceIds = manager.getGlanceIds(CountdownMatrixWidget::class.java)
                        glanceIds.forEach { id ->
                            try {
                                glanceAppWidget.update(context, id)
                            } catch (_: Throwable) {
                                // Skip orphaned glance ids
                            }
                        }
                    } catch (_: Exception) {
                        // Silently handle if update cannot be scheduled yet during boot
                    }
                }
            }
        }
    }
}
