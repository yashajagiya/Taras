package com.example.taras.core.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.BitmapImage
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
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
     * Splits remaining duration into Days and Hours only (removes minutes to eliminate battery drain).
     * Example: "05", "19"
     */
    fun computeCountdownDh(
        targetInstant: Instant?,
        now: Instant = Clock.System.now()
    ): Pair<String, String> {
        if (targetInstant == null) return Pair("--", "--")
        val diff = targetInstant - now
        if (diff.isNegative()) return Pair("00", "00")

        val totalHours = diff.inWholeHours
        val days = totalHours / 24
        val hours = totalHours % 24

        return Pair(
            days.toString().padStart(2, '0'),
            hours.toString().padStart(2, '0')
        )
    }

    /**
     * Splits remaining duration into Days, Hours, and Minutes (preserved for backwards-compatibility).
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
     * Formats target session date into compact Month + Day format (e.g. "Jul 17", "Oct 23").
     */
    fun formatShortSessionDate(
        targetInstant: Instant?,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): String {
        if (targetInstant == null) return "TBD"
        return try {
            val ldt = targetInstant.toLocalDateTime(timeZone)
            val month = ldt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            "$month ${ldt.day}"
        } catch (_: Exception) {
            "TBD"
        }
    }

    /**
     * Formats the target session's time into the user's local device timezone (24h format).
     * Example: "11:30"
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
     * Extracts a clean Grand Prix title.
     * Example: "BELGIAN GRAND PRIX", "UNITED STATES GP", "MONACO GP"
     */
    fun getShortGpTitle(raceName: String?, country: String?): String {
        val cleanRace = raceName?.trim().orEmpty()
        val cleanCountry = country?.trim().orEmpty()
        return when {
            cleanCountry.contains("belgium", ignoreCase = true) || cleanRace.contains("belgian", ignoreCase = true) -> "BELGIAN"
            cleanCountry.contains("united states", ignoreCase = true) || cleanRace.contains("united states", ignoreCase = true) || cleanRace.contains("austin", ignoreCase = true) -> "UNITED STATES"
            cleanCountry.contains("italy", ignoreCase = true) || cleanRace.contains("italian", ignoreCase = true) || cleanRace.contains("monza", ignoreCase = true) -> "ITALIAN"
            cleanCountry.contains("united kingdom", ignoreCase = true) || cleanRace.contains("british", ignoreCase = true) || cleanRace.contains("silverstone", ignoreCase = true) -> "BRITISH"
            cleanCountry.contains("australia", ignoreCase = true) || cleanRace.contains("australian", ignoreCase = true) -> "AUSTRALIAN"
            cleanCountry.contains("monaco", ignoreCase = true) || cleanRace.contains("monaco", ignoreCase = true) -> "MONACO"
            cleanCountry.contains("spain", ignoreCase = true) || cleanRace.contains("spanish", ignoreCase = true) -> "SPANISH"
            cleanCountry.contains("canada", ignoreCase = true) || cleanRace.contains("canadian", ignoreCase = true) -> "CANADIAN"
            cleanCountry.contains("austria", ignoreCase = true) || cleanRace.contains("austrian", ignoreCase = true) -> "AUSTRIAN"
            cleanCountry.contains("netherlands", ignoreCase = true) || cleanRace.contains("dutch", ignoreCase = true) -> "DUTCH"
            cleanCountry.contains("japan", ignoreCase = true) || cleanRace.contains("japanese", ignoreCase = true) -> "JAPANESE"
            cleanCountry.contains("brazil", ignoreCase = true) || cleanRace.contains("brazilian", ignoreCase = true) || cleanRace.contains("são paulo", ignoreCase = true) -> "BRAZILIAN"
            cleanCountry.contains("mexico", ignoreCase = true) || cleanRace.contains("mexican", ignoreCase = true) -> "MEXICAN"
            cleanCountry.contains("singapore", ignoreCase = true) -> "SINGAPORE"
            cleanCountry.contains("qatar", ignoreCase = true) -> "QATAR"
            cleanCountry.contains("bahrain", ignoreCase = true) -> "BAHRAIN"
            cleanCountry.contains("saudi", ignoreCase = true) -> "SAUDI ARABIA"
            cleanCountry.contains("azerbaijan", ignoreCase = true) -> "AZERBAIJAN"
            cleanCountry.contains("china", ignoreCase = true) || cleanRace.contains("chinese", ignoreCase = true) -> "CHINESE"
            cleanCountry.contains("hungary", ignoreCase = true) || cleanRace.contains("hungarian", ignoreCase = true) -> "HUNGARIAN"
            cleanCountry.contains("uae", ignoreCase = true) || cleanRace.contains("abu dhabi", ignoreCase = true) -> "ABU DHABI"
            cleanCountry.isNotBlank() -> cleanCountry.uppercase()
            else -> cleanRace
                .replace("Formula 1", "", ignoreCase = true)
                .replace("Grand Prix", "", ignoreCase = true)
                .replace("GP", "", ignoreCase = true)
                .replace(Regex("[^a-zA-Z\\s]"), "")
                .trim()
                .uppercase()
                .ifBlank { "FORMULA 1" }
        }
    }

    /**
     * Extracts a concise circuit name for the footer.
     */
    fun getShortCircuitName(circuitName: String?): String {
        val clean = circuitName?.trim().orEmpty()
        return when {
            clean.contains("spa", ignoreCase = true) || clean.contains("francorchamps", ignoreCase = true) -> "Spa"
            clean.contains("americas", ignoreCase = true) || clean.contains("cota", ignoreCase = true) || clean.contains("austin", ignoreCase = true) -> "COTA"
            clean.contains("monza", ignoreCase = true) -> "Monza"
            clean.contains("silverstone", ignoreCase = true) -> "Silverstone"
            clean.contains("monaco", ignoreCase = true) -> "Monaco"
            clean.contains("albert park", ignoreCase = true) || clean.contains("melbourne", ignoreCase = true) -> "Albert Park"
            clean.contains("suzuka", ignoreCase = true) -> "Suzuka"
            clean.contains("interlagos", ignoreCase = true) || clean.contains("pace", ignoreCase = true) -> "Interlagos"
            clean.contains("red bull ring", ignoreCase = true) || clean.contains("spielberg", ignoreCase = true) -> "Spielberg"
            clean.contains("zandvoort", ignoreCase = true) -> "Zandvoort"
            clean.contains("sakhir", ignoreCase = true) || clean.contains("bahrain", ignoreCase = true) -> "Sakhir"
            clean.contains("jeddah", ignoreCase = true) -> "Jeddah"
            clean.contains("miami", ignoreCase = true) -> "Miami"
            clean.contains("las vegas", ignoreCase = true) -> "Las Vegas"
            clean.contains("yas marina", ignoreCase = true) || clean.contains("abu dhabi", ignoreCase = true) -> "Yas Marina"
            clean.contains("marina bay", ignoreCase = true) || clean.contains("singapore", ignoreCase = true) -> "Marina Bay"
            clean.contains("barcelona", ignoreCase = true) || clean.contains("catalunya", ignoreCase = true) -> "Catalunya"
            clean.contains("hungaroring", ignoreCase = true) || clean.contains("budapest", ignoreCase = true) -> "Hungaroring"
            clean.contains("baku", ignoreCase = true) || clean.contains("azerbaijan", ignoreCase = true) -> "Baku"
            clean.contains("montreal", ignoreCase = true) || clean.contains("villeneuve", ignoreCase = true) -> "Montreal"
            clean.contains("mexico", ignoreCase = true) || clean.contains("rodriguez", ignoreCase = true) -> "Mexico City"
            clean.contains("imola", ignoreCase = true) || clean.contains("dino ferrari", ignoreCase = true) -> "Imola"
            clean.contains("lusail", ignoreCase = true) || clean.contains("qatar", ignoreCase = true) -> "Lusail"
            clean.contains("shanghai", ignoreCase = true) || clean.contains("china", ignoreCase = true) -> "Shanghai"
            else -> clean
                .replace("Circuit", "", ignoreCase = true)
                .replace("Autodromo", "", ignoreCase = true)
                .replace("International", "", ignoreCase = true)
                .replace("de", "", ignoreCase = true)
                .trim()
                .take(16)
                .ifBlank { "Circuit" }
        }
    }

    /**
     * In-memory LRU Cache that works across both Android runtime and JVM unit test environments.
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

    val bitmapCache = MemoryLruCache<String, Bitmap>(4 * 1024 * 1024) { _, bitmap ->
        try {
            bitmap.byteCount
        } catch (_: Throwable) {
            1024
        }
    }

    /**
     * Loads the real official F1 circuit track outline image from the web via Coil3 and caches it.
     */
    suspend fun loadCircuitBitmap(context: Context, url: String?): Bitmap? {
        if (url.isNullOrBlank()) return null
        val cacheKey = "track_img:$url"
        bitmapCache.get(cacheKey)?.let { return it }

        return try {
            val imageLoader = SingletonImageLoader.get(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(240, 140)
                .build()
            val result = imageLoader.execute(request)
            val bitmap = when (result) {
                is SuccessResult -> {
                    (result.image as? BitmapImage)?.bitmap
                        ?: try {
                            result.image.toBitmap()
                        } catch (_: Throwable) {
                            null
                        }
                }
                else -> null
            }
            if (bitmap != null) {
                bitmapCache.put(cacheKey, bitmap)
            }
            bitmap
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Renders a 6 columns × 4 rows (24 rounds) matrix into a single Bitmap via Canvas.
     * Uses Material 3 outlineColor and activeHighlightColor.
     */
    fun renderSeasonMatrixBitmap(
        rounds: List<SeasonRoundItem>,
        widthPx: Int,
        heightPx: Int,
        outlineColorArgb: Int,
        activeHighlightArgb: Int
    ): Bitmap {
        val safeWidth = widthPx.coerceAtLeast(140)
        val safeHeight = heightPx.coerceAtLeast(90)

        val hash = rounds.map { "${it.roundNumber}:${it.winnerNumber}:${it.teamColor.value}:${it.isCurrentOrNext}" }.hashCode()
        val cacheKey = "matrix:$hash:${safeWidth}x$safeHeight:$outlineColorArgb:$activeHighlightArgb"

        bitmapCache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cols = 6
        val rows = 4
        val cellW = safeWidth / cols.toFloat()
        val cellH = safeHeight / rows.toFloat()

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = (cellH * 0.60f).coerceIn(13f, 26f)
        }

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = outlineColorArgb
        }

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = activeHighlightArgb
        }

        val dotRadius = (cellH * 0.25f).coerceIn(5.5f, 11f)
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

            if (item != null && !item.winnerNumber.isNullOrBlank()) {
                textPaint.color = item.teamColor.toArgb()
                canvas.drawText(item.winnerNumber, cx, cy + textBaselineOffset, textPaint)
            } else {
                canvas.drawCircle(cx, cy, dotRadius, dotPaint)
                if (item?.isCurrentOrNext == true) {
                    canvas.drawCircle(cx, cy, dotRadius * 1.55f, ringPaint)
                }
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
