package com.example.taras.core.helpercore

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.taras.viewmodel.ResultRowData
import java.io.File
import okio.buffer
import okio.sink
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap

object RaceResultShareHelper {

    fun cleanRaceTitle(raceName: String): String {
        var title = raceName.trim()
        title = title.replace("Grand Prix", "GP", ignoreCase = true).trim()
        val currentYear = try {
            getCurrentMoment().toLocalDateTime(currentSystemDefault()).year.toString()
        } catch (_: Exception) {
            "2026"
        }
        if (!title.contains(currentYear) && !title.matches(Regex(".*\\b20\\d\\d\\b.*"))) {
            title = "$currentYear $title"
        }
        return title
    }

    fun cleanSessionTitle(sessionName: String): String {
        val trimmed = sessionName.trim()
        return when {
            trimmed.equals("Results", ignoreCase = true) || trimmed.equals("Race", ignoreCase = true) -> "Race Result"
            trimmed.contains("Qualifying", ignoreCase = true) || trimmed.equals("Qualy", ignoreCase = true) -> "Qualifying Result"
            trimmed.contains("Sprint Q", ignoreCase = true) -> "Sprint Qualifying Result"
            trimmed.contains("Sprint", ignoreCase = true) -> "Sprint Race Result"
            trimmed.startsWith("FP", ignoreCase = true) -> "$trimmed Result"
            else -> "$trimmed Result"
        }
    }

    fun formatDriverSurname(fullName: String): String {
        val trimmed = fullName.trim()
        val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> trimmed
            parts.size == 1 -> parts[0]
            parts.last().equals("Jr.", ignoreCase = true) && parts.size >= 2 -> "${parts[parts.size - 2]} Jr."
            parts.size >= 3 && parts[parts.size - 2].equals("de", ignoreCase = true) -> "${parts[parts.size - 2]} ${parts.last()}"
            parts.size >= 3 && parts[parts.size - 2].equals("van", ignoreCase = true) -> "${parts[parts.size - 2]} ${parts.last()}"
            else -> parts.last()
        }
    }

    fun cleanTeamName(raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("red bull") -> "Red Bull"
            lower.contains("ferrari") -> "Ferrari"
            lower.contains("mclaren") -> "McLaren"
            lower.contains("mercedes") -> "Mercedes"
            lower.contains("aston martin") -> "Aston Martin"
            lower.contains("alpine") -> "Alpine"
            lower.contains("williams") -> "Williams"
            lower.contains("visa") || lower.contains("rb") || lower.contains("racing bulls") -> "Racing Bulls"
            lower.contains("sauber") -> "Sauber"
            lower.contains("haas") -> "Haas"
            else -> raw.trim()
        }
    }

    fun formatExtraTime(rawExtra: String): String {
        val trimmed = rawExtra.trim()
        return when {
            trimmed.isBlank() -> "DNF"
            trimmed.matches(Regex("^\\+\\d+(\\.\\d+)?$")) -> "${trimmed}s"
            else -> trimmed
        }
    }

    fun generateRaceResultSummary(
        raceName: String,
        sessionName: String,
        results: List<ResultRowData>,
        limit: Int = 3
    ): String {
        val cleanTitle = cleanRaceTitle(raceName)
        val sessionLabel = cleanSessionTitle(sessionName)

        val sb = StringBuilder()
        sb.append("$cleanTitle — $sessionLabel\n")

        val items = if (limit > 0) results.take(limit) else results
        items.forEachIndexed { index, item ->
            val pos = item.position.toIntOrNull() ?: (index + 1)
            val prefix = "$pos."
            val driverSurname = formatDriverSurname(item.driver)
            val teamClean = cleanTeamName(item.team)
            val timeDisplay = formatExtraTime(item.extra)
            sb.append("$prefix $driverSurname ($teamClean) — $timeDisplay\n")
        }

        sb.append("via Taras app")
        return sb.toString()
    }

    fun shareRaceResultText(
        context: Context,
        summaryText: String,
        subject: String = "Formula 1 Session Result"
    ) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summaryText)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            val chooser = Intent.createChooser(sendIntent, "Share Race Results")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("RaceResultShare", "Failed to launch text share intent", e)
            Toast.makeText(context, "Could not open share options", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Race Results", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Results copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("RaceResultShare", "Failed to copy to clipboard", e)
            Toast.makeText(context, "Failed to copy", Toast.LENGTH_SHORT).show()
        }
    }

    fun getTeamColorInt(teamName: String): Int {
        val lower = teamName.lowercase()
        return when {
            lower.contains("ferrari") -> "#E80020".toColorInt()
            lower.contains("mclaren") -> "#FF8000".toColorInt()
            lower.contains("red bull") -> "#3671C6".toColorInt()
            lower.contains("mercedes") -> "#27F4D2".toColorInt()
            lower.contains("aston martin") -> "#229971".toColorInt()
            lower.contains("alpine") -> "#FF87BC".toColorInt()
            lower.contains("williams") -> "#64C4FF".toColorInt()
            lower.contains("visa") || lower.contains("rb") || lower.contains("racing bulls") -> "#6692FF".toColorInt()
            lower.contains("sauber") -> "#52E252".toColorInt()
            lower.contains("haas") -> "#B6BABD".toColorInt()
            else -> "#E10600".toColorInt()
        }
    }

    /**
     * Renders a high-resolution 1080x1080 graphic card bitmap suitable for sharing to
     * Twitter, Instagram, Reddit, messaging apps, etc.
     */
    fun generateRaceResultCardBitmap(
        raceName: String,
        sessionName: String,
        circuitName: String,
        results: List<ResultRowData>
    ): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        // 1. Dark Gradient Background
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf("#101117".toColorInt(), "#1A1B24".toColorInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. F1 Red Racing Banner at Top
        val bannerPaint = Paint().apply {
            color = "#E10600".toColorInt()
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), 18f, bannerPaint)

        // 3. Header: Brand badge + Title + Subtitle
        val brandBgPaint = Paint().apply {
            color = "#E10600".toColorInt()
            isAntiAlias = true
        }
        val brandRect = RectF(64f, 54f, 210f, 100f)
        canvas.drawRoundRect(brandRect, 23f, 23f, brandBgPaint)

        val brandTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("TARAS F1", brandRect.centerX(), brandRect.centerY() + 8f, brandTextPaint)

        val cleanTitle = cleanRaceTitle(raceName).uppercase()
        val sessionLabel = cleanSessionTitle(sessionName).uppercase()

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(cleanTitle, 64f, 170f, titlePaint)

        val subtitlePaint = Paint().apply {
            color = "#9E9EAF".toColorInt()
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val circuitStr = if (circuitName.isNotBlank()) "$circuitName · $sessionLabel" else sessionLabel
        canvas.drawText(circuitStr.uppercase(), 64f, 212f, subtitlePaint)

        // 4. Podium Cards (Top 3)
        val podiumDrivers = results.take(3)
        val cardStartY = 250f
        val cardHeight = 220f
        val cardSpacing = 24f

        val cardBgPaint = Paint().apply { isAntiAlias = true }
        val accentPaint = Paint().apply { isAntiAlias = true }
        val posBadgeBgPaint = Paint().apply { isAntiAlias = true }
        val posBadgeTextPaint = Paint().apply {
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val driverNamePaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val teamTextPaint = Paint().apply {
            color = "#B0B1BF".toColorInt()
            textSize = 28f
            isAntiAlias = true
        }
        val timeBadgeBgPaint = Paint().apply {
            color = "#282936".toColorInt()
            isAntiAlias = true
        }
        val timeTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        podiumDrivers.forEachIndexed { index, driver ->
            val topY = cardStartY + index * (cardHeight + cardSpacing)
            val cardRect = RectF(64f, topY, width - 64f, topY + cardHeight)

            // P1 gets spotlight background
            val isP1 = index == 0
            cardBgPaint.color = if (isP1) "#252422".toColorInt() else "#1E1F2A".toColorInt()
            canvas.drawRoundRect(cardRect, 32f, 32f, cardBgPaint)

            // Official Team Color Strip
            val teamColor = getTeamColorInt(driver.team)
            accentPaint.color = teamColor
            val stripRect = RectF(64f, topY, 80f, topY + cardHeight)
            canvas.drawRoundRect(stripRect, 16f, 16f, accentPaint)

            // Position Badge
            val posBadgeRect = RectF(108f, topY + 45f, 200f, topY + 120f)
            posBadgeBgPaint.color = when (index) {
                0 -> "#3D351A".toColorInt()
                1 -> "#30333D".toColorInt()
                else -> "#3D2920".toColorInt()
            }
            canvas.drawRoundRect(posBadgeRect, 20f, 20f, posBadgeBgPaint)

            posBadgeTextPaint.color = when (index) {
                0 -> "#FFD700".toColorInt()
                1 -> "#E0E0E0".toColorInt()
                else -> "#CD7F32".toColorInt()
            }
            canvas.drawText("P${index + 1}", posBadgeRect.centerX(), posBadgeRect.centerY() + 10f, posBadgeTextPaint)

            // Driver Name & Team
            val driverSurname = formatDriverSurname(driver.driver)
            val teamClean = cleanTeamName(driver.team)
            canvas.drawText(driverSurname, 235f, topY + 95f, driverNamePaint)
            canvas.drawText(teamClean, 235f, topY + 155f, teamTextPaint)

            // Time / Gap Chip
            val timeStr = formatExtraTime(driver.extra)
            val timeBadgeRect = RectF(width - 340f, topY + 70f, width - 100f, topY + 145f)
            canvas.drawRoundRect(timeBadgeRect, 24f, 24f, timeBadgeBgPaint)
            canvas.drawText(timeStr, timeBadgeRect.centerX(), timeBadgeRect.centerY() + 10f, timeTextPaint)
        }

        // 5. Footer watermark
        val footerPaint = Paint().apply {
            color = "#6B6C7E".toColorInt()
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated via Taras · Formula 1 Companion", width / 2f, height - 42f, footerPaint)

        return bitmap
    }

    fun shareRaceResultImage(
        context: Context,
        bitmap: Bitmap,
        title: String = "Race Results"
    ) {
        try {
            val imagesFolder = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(imagesFolder, "race_result_${System.currentTimeMillis()}.png")
            file.sink().buffer().use { sink ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, sink.outputStream())
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Race Results Card")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("RaceResultShare", "Failed to share image", e)
            Toast.makeText(context, "Could not generate share image", Toast.LENGTH_SHORT).show()
        }
    }
}
