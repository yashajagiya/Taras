package com.example.taras.core.helpercore

import android.util.Log
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.number
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.Clock

fun getCurrentMoment(): Instant = Clock.System.now()

fun getDatetimeInUtc(): LocalDateTime = getCurrentMoment().toLocalDateTime(TimeZone.UTC)

fun getTodayDate(): String =
    getCurrentMoment().toLocalDateTime(currentSystemDefault()).date.toString()

fun getTodayTime(): String {
    val now = getDatetimeInUtc()
    return "${now.hour.toString().padStart(2, '0')}:${
        now.minute.toString().padStart(2, '0')
    }:${now.second.toString().padStart(2, '0')}Z"
}

fun getCurrentTime() = Currenttime(
    year = getDatetimeInUtc().year,
    month = getDatetimeInUtc().month.number,
    dayOfMonth = getDatetimeInUtc().day,
    hour = getDatetimeInUtc().hour,
    minute = getDatetimeInUtc().minute,
    second = getDatetimeInUtc().second,
    todayDate = getTodayDate(),
    todayTime = getTodayTime()
)

data class Currenttime(
    val year: Int?,
    val month: Int?,
    val dayOfMonth: Int?,
    val hour: Int?,
    val minute: Int?,
    val second: Int?,
    val todayDate: String?,
    val todayTime: String?
)

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
    val totalSeconds = duration.inWholeSeconds
    if (totalSeconds <= 0) return "00:00:00"

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${seconds.toString().padStart(2, '0')}"
}

fun currentSeasonName(): String {
    return getTodayDate().take(4)
}
