package com.example.taras.core.helpercore

import androidx.compose.ui.graphics.Color
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.TeamUiModel

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun getNewsColor(
    newsItem: RssItem,
    defaultColor: Color,
    drivers: ImmutableList<DriverUiModel> = persistentListOf(),
    teams: ImmutableList<TeamUiModel> = persistentListOf()
): Color {
    val title = newsItem.title ?: ""
    val description = newsItem.description ?: ""
    val content = "$title $description"

    //dynamic teams
    for ((teamName, _, _, teamColor) in teams) {
        if (content.contains(teamName, ignoreCase = true)) {
            return teamColor?.toComposeColor() ?: defaultColor
        }
    }

    //dynamic drivers
    for ((_, _, name, _, _, teamColor, _, _, fullName1) in drivers) {
        val fullName = fullName1 ?: ""
        if ((fullName.isNotEmpty() && content.contains(fullName, ignoreCase = true)) ||
            (name.isNotEmpty() && content.contains(name, ignoreCase = true))
        ) {
            return teamColor?.toComposeColor() ?: defaultColor
        }
    }


    return defaultColor
}


