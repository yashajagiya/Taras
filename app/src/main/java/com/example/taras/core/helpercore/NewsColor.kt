package com.example.taras.core.helpercore

import androidx.compose.ui.graphics.Color
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.TeamsImageResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun getNewsColor(
    newsItem: RssItem,
    defaultColor: Color,
    drivers: ImmutableList<DriverDetail> = persistentListOf(),
    teams: ImmutableList<TeamsImageResponse> = persistentListOf()
): Color {
    val title = newsItem.title ?: ""
    val description = newsItem.description ?: ""
    val content = "$title $description"

    // 1. Try dynamic teams
    for (team in teams) {
        if (content.contains(team.teamName, ignoreCase = true)) {
            return team.teamColor.toComposeColor()
        }
    }

    // 2. Try dynamic drivers
    for (driver in drivers) {
        val fullName = driver.fullName ?: ""
        val lastName = driver.lastName ?: ""
        if ((fullName.isNotEmpty() && content.contains(fullName, ignoreCase = true)) ||
            (lastName.isNotEmpty() && content.contains(lastName, ignoreCase = true))
        ) {
            driver.teamColour?.let { return it.toComposeColor() }
        }
    }


    return defaultColor
}


