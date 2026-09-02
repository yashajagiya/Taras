package com.example.taras.core.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MyAppWidgetReceiver () : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = NextWidget()
}