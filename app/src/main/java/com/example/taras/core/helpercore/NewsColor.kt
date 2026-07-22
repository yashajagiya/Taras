package com.example.taras.core.helpercore

import androidx.compose.ui.graphics.Color
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.TeamsImageResponse

fun getNewsColor(
    newsItem: RssItem,
    drivers: List<DriverDetail>,
    teams: List<TeamsImageResponse>,
    defaultColor: Color
): Color {
    val title = newsItem.title ?: ""
    val description = newsItem.description ?: ""
    val content = "$title $description"

    val teamSpecialCases = mapOf(
        "Mercedes" to "0x00D2BE",
        "Ferrari" to "0xE10600",
        "Red Bull" to "0x0600EF",
        "McLaren" to "0xFF8700",
        "Aston Martin" to "0x006F62",
        "Alpine" to "0x0090FF",
        "Williams" to "0x005AFF",
        "Haas" to "0xFFFFFF",
        "Sauber" to "0x52E252",
        "RB" to "0x6692FF",
        "AlphaTauri" to "0x4E7C9B",
        "Racing Bulls" to "0x6692FF"
    )

    for ((name, colorHex) in teamSpecialCases) {
        if (content.contains(name, ignoreCase = true)) {
            return colorHex.toComposeColor()
        }
    }

    for (team in teams) {
        if (content.contains(team.teamName, ignoreCase = true)) {
            return team.teamColor.toComposeColor()
        }
    }

    for (driver in drivers) {
        val lastName = driver.lastName
        val fullName = driver.fullName
        if ((!lastName.isNullOrBlank() && content.contains(lastName, ignoreCase = true)) ||
            (!fullName.isNullOrBlank() && content.contains(fullName, ignoreCase = true))
        ) {
            return driver.teamColour?.toComposeColor() ?: defaultColor
        }
    }

    return defaultColor
}


