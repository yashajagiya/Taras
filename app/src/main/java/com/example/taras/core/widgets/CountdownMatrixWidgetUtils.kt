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
     * Splits remaining duration into Days, Hours, and Minutes (matching Image 1 & 2 design).
     * Example: "05", "19", "31"
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
     * Splits remaining duration into Days and Hours only.
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
     * Extracts a concise circuit name for the footer (e.g. "Spa", "COTA", "Monza", "Silverstone").
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
                .take(14)
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
     * Exact double-walled vector SVG paths for F1 circuits.
     */
    val circuitPaths = mapOf(
        "spa" to "M 16,96 C 12,93 24,74 37,58 L 68,31 C 83,19 112,11 128,15 C 138,17 140,25 130,29 L 111,33 C 103,35 105,43 115,45 L 132,47 C 140,49 138,57 128,59 L 105,61 C 97,63 101,73 113,79 L 134,89 C 140,93 134,101 122,97 L 95,83 C 83,75 73,63 59,67 L 31,87 C 23,93 19,98 16,96 Z",
        "cota" to "M 15,35 L 20,15 C 22,12 28,12 30,16 L 38,28 C 42,34 50,34 54,28 L 62,22 C 66,18 74,20 76,26 L 85,45 C 88,52 96,52 100,45 L 115,20 C 118,15 125,18 126,24 L 132,70 C 133,78 126,82 120,78 L 100,68 C 94,65 88,70 86,78 C 84,86 76,86 72,80 L 55,68 C 46,62 38,66 32,75 L 22,88 C 16,92 12,86 14,78 L 18,50 Z",
        "monza" to "M 18,75 L 22,22 C 24,15 32,14 42,16 L 118,18 C 128,20 134,28 130,38 L 116,48 C 110,52 112,60 120,64 L 128,68 C 135,74 130,86 116,86 L 35,86 C 24,86 18,82 18,75 Z",
        "silverstone" to "M 28,78 L 32,32 C 35,22 46,18 58,26 L 72,36 C 80,40 88,36 94,28 L 104,16 C 114,14 124,20 126,30 L 128,52 C 130,65 120,76 108,74 L 84,70 C 74,68 64,78 54,84 L 38,86 C 30,86 26,84 28,78 Z",
        "monaco" to "M 22,82 L 25,48 C 28,40 36,36 46,30 L 62,20 C 72,16 82,20 88,28 L 96,40 C 102,48 95,56 88,54 L 72,50 C 66,48 62,58 70,66 L 108,72 C 118,76 120,88 108,90 L 58,90 C 42,90 35,80 22,82 Z",
        "albert_park" to "M 26,76 L 24,32 C 24,22 34,16 46,18 L 92,22 C 102,25 108,35 106,45 L 102,62 C 100,72 108,78 118,76 L 126,74 C 134,78 128,88 116,88 L 48,88 C 36,88 28,84 26,76 Z",
        "suzuka" to "M 25,60 C 20,45 35,25 50,22 C 65,20 75,35 85,50 C 95,65 105,75 120,72 C 130,70 135,55 125,45 C 115,35 100,30 90,40 C 80,50 70,68 55,75 C 40,80 28,75 25,60 Z"
    )
    val defaultCircuitPath = "M 16,96 C 12,93 24,74 37,58 L 68,31 C 83,19 112,11 128,15 C 138,17 140,25 130,29 L 111,33 C 103,35 105,43 115,45 L 132,47 C 140,49 138,57 128,59 L 105,61 C 97,63 101,73 113,79 L 134,89 C 140,93 134,101 122,97 L 95,83 C 83,75 73,63 59,67 L 31,87 C 23,93 19,98 16,96 Z"

    fun getNormalizedCircuitKey(circuitName: String?): String {
        val clean = circuitName?.lowercase().orEmpty()
        return when {
            clean.contains("spa") || clean.contains("francorchamps") -> "spa"
            clean.contains("americas") || clean.contains("cota") || clean.contains("austin") -> "cota"
            clean.contains("monza") || clean.contains("nazionale") -> "monza"
            clean.contains("silverstone") -> "silverstone"
            clean.contains("monaco") || clean.contains("monte carlo") -> "monaco"
            clean.contains("albert") || clean.contains("melbourne") -> "albert_park"
            clean.contains("suzuka") -> "suzuka"
            else -> "spa"
        }
    }

    /**
     * Renders the double-walled race track vector silhouette matching the design spec.
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
        val padding = (safeHeight * 0.08f).coerceAtLeast(5f)
        val destRect = android.graphics.RectF(padding, padding, safeWidth - padding, safeHeight - padding)
        val matrix = android.graphics.Matrix().apply {
            setRectToRect(bounds, destRect, android.graphics.Matrix.ScaleToFit.CENTER)
        }
        path.transform(matrix)

        // Outer light-silver track wall
        val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (safeHeight * 0.10f).coerceIn(4.5f, 6.5f)
            color = outerStrokeColorArgb
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(path, outerPaint)

        // Inner dark cutout core creating the double-wall track outline
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = (safeHeight * 0.045f).coerceIn(1.8f, 2.6f)
            color = innerStrokeColorArgb
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(path, innerPaint)

        bitmapCache.put(cacheKey, bitmap)
        return bitmap
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
     * Resolves the official winning driver/team color for car numbers in the matrix.
     * Matches Image 2: #63 (Blue), #12 (Teal), #44 (Red), #16 (Red).
     */
    fun getDriverWinnerColor(winnerNumber: String?, teamColor: Color): Int {
        return when (winnerNumber) {
            "63" -> android.graphics.Color.parseColor("#2974E8") // Russell Blue (#63)
            "12" -> android.graphics.Color.parseColor("#00D6BE") // Antonelli Teal (#12)
            "44" -> android.graphics.Color.parseColor("#FF2828") // Hamilton Red (#44)
            "16" -> android.graphics.Color.parseColor("#FF2828") // Leclerc Red (#16)
            "1" -> android.graphics.Color.parseColor("#3671C6")  // Verstappen Blue (#1)
            "4", "81" -> android.graphics.Color.parseColor("#FF8000") // McLaren Papaya (#4, #81)
            "14" -> android.graphics.Color.parseColor("#229971") // Aston Martin Green (#14)
            else -> teamColor.toArgb()
        }
    }

    /**
     * Renders a national flag bitmap (e.g. Belgian tricolor, US stripes, etc.) with rounded corners.
     */
    fun renderCountryFlagBitmap(countryOrGpName: String?, widthPx: Int = 56, heightPx: Int = 38): Bitmap {
        val clean = countryOrGpName?.lowercase().orEmpty()
        val cacheKey = "flag:$clean:${widthPx}x$heightPx"
        bitmapCache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val clipPath = android.graphics.Path().apply {
            addRoundRect(
                android.graphics.RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat()),
                6f, 6f,
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
            // Australia: Navy with Union jack canton & stars
            clean.contains("austr") || clean.contains("melbourne") -> {
                paint.color = android.graphics.Color.parseColor("#00008B")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawCircle(widthPx * 0.75f, heightPx * 0.3f, 3f, paint)
                canvas.drawCircle(widthPx * 0.65f, heightPx * 0.5f, 3f, paint)
                canvas.drawCircle(widthPx * 0.85f, heightPx * 0.5f, 3f, paint)
                canvas.drawCircle(widthPx * 0.75f, heightPx * 0.75f, 4f, paint)
            }
            // Canada: Red | White | Red
            clean.contains("canada") || clean.contains("montreal") -> {
                val borderW = widthPx * 0.25f
                paint.color = android.graphics.Color.parseColor("#FF0000")
                canvas.drawRect(0f, 0f, borderW, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(borderW, 0f, widthPx - borderW, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#FF0000")
                canvas.drawRect(widthPx - borderW, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
                canvas.drawCircle(widthPx / 2f, heightPx / 2f, heightPx * 0.22f, paint)
            }
            // Spain: Red | Gold | Red
            clean.contains("spain") || clean.contains("catalunya") || clean.contains("barcelona") -> {
                val stripeH = heightPx / 4f
                paint.color = android.graphics.Color.parseColor("#AA151B")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), stripeH, paint)
                paint.color = android.graphics.Color.parseColor("#F1BF00")
                canvas.drawRect(0f, stripeH, widthPx.toFloat(), stripeH * 3, paint)
                paint.color = android.graphics.Color.parseColor("#AA151B")
                canvas.drawRect(0f, stripeH * 3, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // Austria: Red | White | Red
            clean.contains("austria") || clean.contains("spielberg") -> {
                val stripeH = heightPx / 3f
                paint.color = android.graphics.Color.parseColor("#ED2939")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), stripeH, paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(0f, stripeH, widthPx.toFloat(), stripeH * 2, paint)
                paint.color = android.graphics.Color.parseColor("#ED2939")
                canvas.drawRect(0f, stripeH * 2, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // Brazil: Green with Yellow rhombus & Blue circle
            clean.contains("brazil") || clean.contains("interlagos") || clean.contains("paulo") -> {
                paint.color = android.graphics.Color.parseColor("#009739")
                canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
                val rhombus = android.graphics.Path().apply {
                    moveTo(widthPx / 2f, 4f)
                    lineTo(widthPx - 4f, heightPx / 2f)
                    lineTo(widthPx / 2f, heightPx - 4f)
                    lineTo(4f, heightPx / 2f)
                    close()
                }
                paint.color = android.graphics.Color.parseColor("#FEDD00")
                canvas.drawPath(rhombus, paint)
                paint.color = android.graphics.Color.parseColor("#012169")
                canvas.drawCircle(widthPx / 2f, heightPx / 2f, heightPx * 0.22f, paint)
            }
            // Mexico: Green | White | Red
            clean.contains("mexico") -> {
                val stripeW = widthPx / 3f
                paint.color = android.graphics.Color.parseColor("#006847")
                canvas.drawRect(0f, 0f, stripeW, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.WHITE
                canvas.drawRect(stripeW, 0f, stripeW * 2, heightPx.toFloat(), paint)
                paint.color = android.graphics.Color.parseColor("#CE1126")
                canvas.drawRect(stripeW * 2, 0f, widthPx.toFloat(), heightPx.toFloat(), paint)
            }
            // Fallback: Checkered Flag pattern
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
     * Matches Image 2:
     * - Completed rounds show bold car numbers with official driver/team colors.
     * - Un-raced rounds show clean solid grey circles.
     * - Active round shows a subtle primary highlight ring.
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
            textSize = (cellH * 0.50f).coerceIn(11f, 20f)
        }

        // Solid grey dot color matching Image 2 (#6E7075 or outline)
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = if (outlineColorArgb != 0) outlineColorArgb else 0xFF6E7075.toInt()
        }

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.2f
            color = activeHighlightArgb
        }

        val dotRadius = (cellH * 0.22f).coerceIn(4.5f, 9.5f)
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
                textPaint.color = getDriverWinnerColor(item.winnerNumber, item.teamColor)
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
