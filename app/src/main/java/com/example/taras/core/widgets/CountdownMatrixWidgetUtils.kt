package com.example.taras.core.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Data representation of a single round for the 6×4 season winner matrix.
 */
data class SeasonRoundItem(
    val roundNumber: Int,
    val winnerNumber: String?,
    val teamColor: Color,
    val isCurrentOrNext: Boolean = false
)

object CountdownMatrixWidgetUtils {

    /**
     * Splits remaining duration into Days, Hours, and Minutes.
     */
    fun computeCountdownDhm(
        targetInstant: Instant?,
        now: Instant = Clock.System.now()
    ): Triple<String, String, String> {
        if (targetInstant == null) return Triple("--", "--", "--")
        val diff = targetInstant - now
        if (diff.isNegative()) return Triple("00", "00", "00")

        val totalMinutes = diff.inWholeMinutes
        val days = totalMinutes / (24 * 60)
        val hours = (totalMinutes % (24 * 60)) / 60
        val minutes = totalMinutes % 60

        return Triple(
            days.toString().padStart(2, '0'),
            hours.toString().padStart(2, '0'),
            minutes.toString().padStart(2, '0')
        )
    }

    /**
     * Formats the target session's date into the user's local device timezone.
     * Example: "SUN, 24 MAY"
     */
    fun formatLocalSessionDate(
        targetInstant: Instant?,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): String {
        if (targetInstant == null) return "TBD"
        return try {
            val ldt = targetInstant.toLocalDateTime(timeZone)
            val dayOfWeek = ldt.dayOfWeek.name.take(3).uppercase()
            val month = ldt.month.name.take(3).uppercase()
            "$dayOfWeek, ${ldt.day} $month"
        } catch (_: Exception) {
            "TBD"
        }
    }

    /**
     * Formats the target session's time into the user's local device timezone (24h format).
     * Example: "15:00"
     */
    fun formatLocalSessionTime(
        targetInstant: Instant?,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): String {
        if (targetInstant == null) return "--:--"
        return try {
            val ldt = targetInstant.toLocalDateTime(timeZone)
            val hour = ldt.hour.toString().padStart(2, '0')
            val minute = ldt.minute.toString().padStart(2, '0')
            "$hour:$minute"
        } catch (_: Exception) {
            "--:--"
        }
    }

    /**
     * In-memory LRU Cache that works across both Android runtime and JVM unit test environments
     * without android.util.LruCache mock errors.
     */
    class MemoryLruCache<K, V>(
        private val maxByteSize: Int,
        private val sizeOf: (K, V) -> Int = { _, _ -> 1 }
    ) {
        private val map = LinkedHashMap<K, V>(16, 0.75f, true)
        private var currentSize = 0

        @Synchronized
        fun get(key: K): V? = map[key]

        @Synchronized
        fun put(key: K, value: V) {
            val size = sizeOf(key, value)
            map[key] = value
            currentSize += size
            val iterator = map.entries.iterator()
            while (currentSize > maxByteSize && iterator.hasNext()) {
                val entry = iterator.next()
                currentSize -= sizeOf(entry.key, entry.value)
                iterator.remove()
            }
        }

        @Synchronized
        fun size(): Int = map.size

        @Synchronized
        fun byteSize(): Int = currentSize

        @Synchronized
        fun clear() {
            map.clear()
            currentSize = 0
        }
    }

    /**
     * 4 MiB Cache for rendered matrix bitmaps to prevent Binder buffer overflow
     * (TransactionTooLargeException) and eliminate redundant redraws.
     */
    val bitmapCache = MemoryLruCache<String, Bitmap>(4 * 1024 * 1024) { _, bitmap ->
        try {
            bitmap.byteCount
        } catch (_: Throwable) {
            1024
        }
    }

    /**
     * Renders a 6 columns × 4 rows (24 rounds) matrix into a single Bitmap via Canvas.
     * Avoids Glance's 10-child layout limits.
     */
    fun renderSeasonMatrixBitmap(
        rounds: List<SeasonRoundItem>,
        widthPx: Int,
        heightPx: Int,
        outlineColorArgb: Int = 0xFF857370.toInt(),
        activeHighlightArgb: Int = 0xFFC00100.toInt()
    ): Bitmap {
        val safeWidth = widthPx.coerceAtLeast(120)
        val safeHeight = heightPx.coerceAtLeast(80)

        val hash = rounds.map { "${it.roundNumber}:${it.winnerNumber}:${it.teamColor.value}:${it.isCurrentOrNext}" }.hashCode()
        val cacheKey = "matrix:$hash:${safeWidth}x$safeHeight"

        bitmapCache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cols = 6
        val rows = 4
        val cellW = safeWidth / cols.toFloat()
        val cellH = safeHeight / rows.toFloat()

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = (cellH * 0.58f).coerceIn(12f, 26f)
        }

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = outlineColorArgb
        }

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = activeHighlightArgb
        }

        val dotRadius = (cellH * 0.16f).coerceIn(3.5f, 7f)
        val fontMetrics = textPaint.fontMetrics
        val textBaselineOffset = (fontMetrics.descent - fontMetrics.ascent) / 2f - fontMetrics.descent

        val roundMap = rounds.associateBy { it.roundNumber }

        for (i in 0 until 24) {
            val roundNum = i + 1
            val col = i % cols
            val row = i / cols

            val cx = col * cellW + cellW / 2f
            val cy = row * cellH + cellH / 2f

            val item = roundMap[roundNum]

            if (item?.isCurrentOrNext == true) {
                canvas.drawCircle(cx, cy, dotRadius * 2.2f, ringPaint)
            }

            if (item != null && !item.winnerNumber.isNullOrBlank()) {
                textPaint.color = item.teamColor.toArgb()
                canvas.drawText(item.winnerNumber, cx, cy + textBaselineOffset, textPaint)
            } else {
                canvas.drawCircle(cx, cy, dotRadius, dotPaint)
            }
        }

        bitmapCache.put(cacheKey, bitmap)
        return bitmap
    }

    /**
     * Schedules a single exact milestone alarm for the upcoming session start
     * so the widget transitions to "LIVE NOW" at the exact minute with 0% idle battery drain.
     */
    fun scheduleNextSessionMilestoneAlarm(context: Context, targetInstant: Instant?) {
        if (targetInstant == null) return
        val targetMillis = targetInstant.toEpochMilliseconds()
        val nowMillis = System.currentTimeMillis()
        if (targetMillis <= nowMillis) return

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return
            val intent = Intent(context, CountdownMatrixWidgetReceiver::class.java).apply {
                action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                1112,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            }
        } catch (_: Exception) {
            // Silently ignore if OS restricts alarms
        }
    }
}

