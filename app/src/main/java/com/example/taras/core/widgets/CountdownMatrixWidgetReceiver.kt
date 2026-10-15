package com.example.taras.core.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CountdownMatrixWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CountdownMatrixWidget()

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val comp = ComponentName(context, CountdownMatrixWidgetReceiver::class.java)

        // 1. Sanitize widget IDs to prevent Glance crashing on deleted/orphaned widget IDs
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: appWidgetManager.getAppWidgetIds(comp)
            val validIds = ids.filter { appWidgetManager.getAppWidgetInfo(it) != null }.toIntArray()
            if (validIds.isEmpty()) {
                // No active widget instance exists on the home screen.
                // Do not forward to Glance, as Glance will query getAppWidgetIds(comp),
                // find orphaned IDs (e.g. 62), and crash with IllegalArgumentException: No app widget info for X.
                return
            }
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, validIds)
        }

        // 2. Sanitize single widget ID intents (like OPTIONS_CHANGED / RESIZED)
        val singleId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (singleId != AppWidgetManager.INVALID_APPWIDGET_ID &&
            intent.action != AppWidgetManager.ACTION_APPWIDGET_DELETED &&
            intent.action != AppWidgetManager.ACTION_APPWIDGET_DISABLED &&
            appWidgetManager.getAppWidgetInfo(singleId) == null) {
            // Drop any intent targeting an unconfirmed, removed, or orphaned widget ID
            return
        }

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_DELETED ||
            intent.action == AppWidgetManager.ACTION_APPWIDGET_DISABLED) {
            try {
                super.onReceive(context, intent)
            } catch (_: Throwable) {
                // Silently absorb orphaned widget lifecycle exceptions
            }
            return
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
                        (glanceAppWidget as? CountdownMatrixWidget)?.safeUpdateAll(context)
                    } catch (_: Throwable) {
                        // Silently handle if update cannot be scheduled yet during boot
                    }
                }
            }
        }
    }
}
