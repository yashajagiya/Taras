package com.example.taras.core.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
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
     * Extracts a clean uppercase Grand Prix title without corporate sponsor noise or emojis.
     * Example: "BELGIAN", "UNITED STATES", "ITALIAN", "BRITISH"
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
     * Extracts a concise track name (e.g. "Spa", "COTA", "Monza", "Silverstone").
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
                .take(12)
                .ifBlank { "Track" }
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

    // High-accuracy SVG paths for track silhouettes
    private val circuitPaths = mapOf(
        "spa" to "M 16,96 C 12,93 24,74 37,58 L 68,31 C 83,19 112,11 128,15 C 138,17 140,25 130,29 L 111,33 C 103,35 105,43 115,45 L 132,47 C 140,49 138,57 128,59 L 105,61 C 97,63 101,73 113,79 L 134,89 C 140,93 134,101 122,97 L 95,83 C 83,75 73,63 59,67 L 31,87 C 23,93 19,98 16,96 Z",
        "cota" to "M 25,85 L 30,20 C 32,15 42,15 45,22 L 55,40 C 58,45 65,45 68,40 C 72,35 78,35 82,42 C 86,50 92,50 95,45 L 105,25 C 108,20 115,22 118,28 L 130,85 C 132,92 125,95 118,92 L 95,85 C 88,82 82,88 80,95 C 78,102 70,102 65,95 L 45,90 C 35,88 28,92 25,85 Z",
        "monza" to "M 20,80 L 25,25 C 26,18 35,16 45,18 L 120,20 C 130,22 135,32 130,42 L 115,50 C 110,55 112,62 120,65 L 128,70 C 135,75 130,88 115,88 L 35,88 C 25,88 20,85 20,80 Z",
        "silverstone" to "M 30,80 L 35,35 C 38,25 50,20 62,28 L 75,38 C 82,42 90,38 95,30 L 105,18 C 115,15 125,22 128,32 L 130,55 C 132,68 122,78 110,75 L 85,72 C 75,70 65,80 55,85 L 40,88 C 32,88 28,85 30,80 Z",
        "monaco" to "M 25,85 L 28,50 C 30,42 38,38 48,32 L 65,22 C 75,18 85,22 92,30 L 100,42 C 105,50 98,58 90,55 L 75,52 C 68,50 65,60 72,68 L 110,75 C 120,78 122,90 110,92 L 60,92 C 45,92 38,82 30,85 Z",
        "albert_park" to "M 30,80 L 28,35 C 28,25 38,18 50,20 L 95,25 C 105,28 110,38 108,48 L 105,65 C 102,75 110,82 120,80 L 128,78 C 135,82 130,92 118,92 L 50,92 C 38,92 32,88 30,80 Z"
    )
    private val defaultCircuitPath = "M 20,75 C 15,65 22,35 35,25 C 48,15 80,18 95,22 C 110,26 128,20 135,32 C 142,45 132,65 120,72 C 108,80 92,68 80,70 C 68,72 55,85 40,85 C 28,85 22,82 20,75 Z"

    /**
     * Renders the double-walled race track vector silhouette onto a Bitmap.
     * Outer wall: Silver (#D5D7DC)
     * Inner wall: Dark Obsidian (#111214)
     */
    fun renderCircuitMapBitmap(
        circuitName: String?,
        widthPx: Int = 180,
        heightPx: Int = 110,
        outerStrokeColorArgb: Int = 0xFFD5D7DC.toInt(),
        innerStrokeColorArgb: Int = 0xFF111214.toInt()
    ): Bitmap {
        val safeWidth = widthPx.coerceAtLeast(60)
        val safeHeight = heightPx.coerceAtLeast(40)
        val key = getNormalizedCircuitKey(circuitName)
        val cacheKey = "track:$key:${safeWidth}x${safeHeight}:$outerStrokeColorArgb:$innerStrokeColorArgb"

        bitmapCache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val pathData = circuitPaths[key] ?: defaultCircuitPath
        val path = try {
            androidx.core.graphics.PathParser.createPathFromPathData(pathData)
        } catch (_: Throwable) {
            android.graphics.Path().apply {
                addOval(10f, 10f, safeWidth - 10f, safeHeight - 10f, android.graphics.Path.Direction.CW)
            }
        }

        // Scale and center the track path to the bitmap canvas with padding
        val bounds = android.graphics.RectF()
        path.computeBounds(bounds, true)
        val padding = (safeHeight * 0.08f).coerceAtLeast(6f)
        val destRect = android.graphics.RectF(padding, padding, safeWidth - padding, safeHeight - padding)
        val matrix = android.graphics.Matrix().apply {
            setRectToRect(bounds, destRect, android.graphics.Matrix.ScaleToFit.CENTER)
        }
        path.transform(matrix)

        // Outer light-silver track wall
        val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (safeHeight * 0.10f).coerceIn(4.5f, 7f)
            color = outerStrokeColorArgb
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(path, outerPaint)

        // Inner dark cutout core creating the double-wall track outline
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (safeHeight * 0.045f).coerceIn(1.8f, 2.8f)
            color = innerStrokeColorArgb
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(path, innerPaint)

        bitmapCache.put(cacheKey, bitmap)
        return bitmap
    }

    private fun getNormalizedCircuitKey(circuitName: String?): String {
        val clean = circuitName?.lowercase().orEmpty()
        return when {
            clean.contains("spa") || clean.contains("francorchamps") -> "spa"
            clean.contains("americas") || clean.contains("cota") || clean.contains("austin") -> "cota"
            clean.contains("monza") || clean.contains("nazionale") -> "monza"
            clean.contains("silverstone") -> "silverstone"
            clean.contains("monaco") || clean.contains("monte carlo") -> "monaco"
            clean.contains("albert") || clean.contains("melbourne") -> "albert_park"
            else -> "default"
        }
    }

    /**
     * Renders a clean 3-stripe or iconic national flag for the upcoming race.
     */
    fun renderCountryFlagBitmap(countryOrGpName: String?, widthPx: Int = 42, heightPx: Int = 28): Bitmap {
        val clean = countryOrGpName?.lowercase().orEmpty()
        val cacheKey = "flag:$clean:${widthPx}x$heightPx"
        bitmapCache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val clipPath = android.graphics.Path().apply {
            addRoundRect(
                android.graphics.RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat()),
                5f, 5f,
                android.graphics.Path.Direction.CW
            )
        }
        canvas.clipPath(clipPath)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

        when {
            // Belgium: Black | Yellow | Red
            clean.contains("belg") -> {
                val stripeW = widthPx / 3f
                paint.color = android.graphics.Color.parseColor("#000000")
                canvas.drawRect(0f, 0f, stripeW, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#FAE042")
                canvas.drawRect(stripeW, 0f, stripeW * 2, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#ED2939")
                canvas.drawRect(stripeW * 2, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // USA: Blue canton + Red/White stripes
            clean.contains("united states") || clean.contains("usa") || clean.contains("austin") || clean.contains("cota") || clean.contains("miami") || clean.contains("las vegas") -> {
                val numStripes = 7
                val stripeH = heightPx / numStripes.toFloat()
                for (s in 0 until numStripes) {
                    paint.color = if (s % 2 == 0) android.graphics.Color.parseColor("#BF0A30") else android.graphics.Color.WHITE
                    canvas.drawRect(0f, s * stripeH, widthPx.toFloat(), (s + 1) * stripeH, paint)
                }
                paint.color = android.graphics.Color.parseColor("#002868")
                canvas.drawRect(0f, 0f, widthPx * 0.42f, stripeH * 4, paint)
            }
            // Italy: Green | White | Red
            clean.contains("ital") || clean.contains("monza") || clean.contains("imola") -> {
                val stripeW = widthPx / 3f
                paint.color = android.graphics.Color.parseColor("#009246")
                canvas.drawRect(0f, 0f, stripeW, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(stripeW, 0f, stripeW * 2, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#CE2B37")
                canvas.drawRect(stripeW * 2, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // UK / Britain: Blue with crosses
            clean.contains("brit") || clean.contains("kingdom") || clean.contains("silverstone") -> {
                paint.color = android.graphics.Color.parseColor("#012169")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(widthPx * 0.38f, 0f, widthPx * 0.62f, heightPx.toFloat(), paint)
                canvas.drawRect(0f, heightPx * 0.35f, widthPx.toFloat(), heightPx * 0.65f, paint)
                paint.color = android.graphics.Color.parseColor("#C8102E")
                canvas.drawRect(widthPx * 0.42f, 0f, widthPx * 0.58f, heightPx.toFloat(), paint)
                canvas.drawRect(0f, heightPx * 0.40f, widthPx.toFloat(), heightPx * 0.60f, paint)
            }
            // Netherlands: Red | White | Blue
            clean.contains("dutch") || clean.contains("nether") || clean.contains("zandvoort") -> {
                val stripeH = heightPx / 3f
                paint.color = android.graphics.Color.parseColor("#AE1C28")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), stripeH, paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(0f, stripeH, widthPx.toFloat(), stripeH * 2, paint)
                paint.color = android.graphics.Color.parseColor("#21468B")
                canvas.drawRect(0f, stripeH * 2, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // Monaco: Red | White
            clean.contains("monaco") -> {
                val stripeH = heightPx / 2f
                paint.color = android.graphics.Color.parseColor("#CE1126")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), stripeH, paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(0f, stripeH, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // Japan: White with red circle
            clean.contains("japan") || clean.contains("suzuka") -> {
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#BC002D")
                canvas.drawCircle(widthPx / 2f, heightPx / 2f, heightPx * 0.32f, paint)
            }
            // Fallback: Racing Checkered Flag pattern
            else -> {
                val sq = 7f
                val cols = (widthPx / sq).toInt() + 1
                val rows = (heightPx / sq).toInt() + 1
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        paint.color = if ((r + c) % 2 == 0) android.graphics.Color.parseColor("#1A1A1A") else android.graphics.Color.parseColor("#E0E0E0")
                        canvas.drawRect(c * sq, r * sq, (c + 1) * sq, (r + 1) * sq, paint)
                    }
                }
            }
        }

        bitmapCache.put(cacheKey, bitmap)
        return bitmap
    }

    /**
     * Renders a 6 columns × 4 rows (24 rounds) matrix into a single Bitmap via Canvas.
     * Matches Image 2: Bold F1 driver car numbers, solid grey circles, and red highlight ring.
     */
    fun renderSeasonMatrixBitmap(
        rounds: List<SeasonRoundItem>,
        widthPx: Int,
        heightPx: Int,
        outlineColorArgb: Int = 0xFF6E7075.toInt(),
        activeHighlightArgb: Int = 0xFFFF1801.toInt()
    ): Bitmap {
        val safeWidth = widthPx.coerceAtLeast(140)
        val safeHeight = heightPx.coerceAtLeast(90)

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
