package com.example.taras.core.helpercore

import android.util.Log
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.number
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.Clock

fun getCurrentMoment(): Instant = Clock.System.now()

//fun getDatetimeInUtc(): LocalDateTime = getCurrentMoment().toLocalDateTime(TimeZone.UTC)

fun getTodayDate(): String =
    getCurrentMoment().toLocalDateTime(currentSystemDefault()).date.toString()

fun String.toRemoveDateExtra(): Int {
    val cleanStringTime = this.replace("-", "")
    return cleanStringTime.toIntOrNull() ?: 0
}

fun parseSessionTimeToInstant(date: String?, time: String?): Instant? {
    if (date == null || time == null) return null
    return try {
        var normalizedTime = time.trim()
        val hasZ = normalizedTime.endsWith("Z")
        if (hasZ) normalizedTime = normalizedTime.removeSuffix("Z")

        val parts = normalizedTime.split(":")
        normalizedTime = when (parts.size) {
            1 -> "${parts[0]}:00:00"
            2 -> "${parts[0]}:${parts[1]}:00"
            else -> normalizedTime
        }

        val isoString = "${date}T${normalizedTime}Z"
        Instant.parse(isoString)
    } catch (e: Exception) {
        Log.e("CurrenTime", "Error parsing $date $time: ${e.message}")
        null
    }
}

fun formatToLocalSession(date: String?, time: String?): String {
    val instant = parseSessionTimeToInstant(date, time) ?: return ""
    val localDateTime = instant.toLocalDateTime(currentSystemDefault())
    val day = localDateTime.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day $hour:$minute"
}

fun formatToLocalFull(date: String?, time: String?): String {
    val instant = parseSessionTimeToInstant(date, time) ?: return ""
    val localDateTime = instant.toLocalDateTime(currentSystemDefault())
    val day = localDateTime.day.toString().padStart(2, '0')
    val month = localDateTime.month.number.toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$minute"
}

fun formatCountdown(duration: Duration): String {
    if (!duration.isPositive()) return "00:00:00"

    return duration.toComponents { days, hours, minutes, seconds, _ ->

        val h = hours.toString().padStart(2, '0')
        val m = minutes.toString().padStart(2, '0')
        val s = seconds.toString().padStart(2, '0')

        if (days > 0) {
            "$days:$h:$m:$s"
        } else {
            "$h:$m:$s"
        }
    }
}

fun formatCountdownWidgets(duration: Duration): String {
    if (!duration.isPositive()) return "Session Started"

    return duration.toComponents { days, hours, _, _, _ ->
        val daysPart = if (days > 0) "$days ${if (days == 1L) "Day" else "Days"}" else ""
        val hoursPart = if (hours > 0) "$hours ${if (hours == 1) "Hour" else "Hours"}" else ""

        val timeStr = listOf(daysPart, hoursPart).filter { it.isNotEmpty() }.joinToString(" ")

        if (timeStr.isEmpty()) {
            "Less than 1 Hour Left"
        } else {
            "$timeStr Left"
        }
    }
}

fun String.toMonthes(): String {
    return when (this) {
        "01" -> "Jan"
        "02" -> "Feb"
        "03" -> "Mar"
        "04" -> "Apr"
        "05" -> "May"
        "06" -> "Jun"
        "07" -> "Jul"
        "08" -> "Aug"
        "09" -> "Sep"
        "10" -> "Oct"
        "11" -> "Nov"
        "12" -> "Dec"
        else -> "N/A"
    }

}

fun String.toGetMonths(): String {
    val date = this.split("-")
    return date[1]
}

fun String.toGetDate(): String {
    val date = this.split("-")
    return date[2]
}