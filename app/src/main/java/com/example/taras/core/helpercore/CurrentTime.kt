package com.example.taras.core.helpercore

import android.util.Log
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
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

fun String.toMonthes(): String{
    return when(this){
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
fun String.toGetMonths(): String{
    val date = this.split("-")
    return date[1]
}

fun String.toGetDate(): String{
    val date = this.split("-")
    return date[2]
}