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
    for (team in teams) {
        if (content.contains(team.teamName, ignoreCase = true)) {
            return team.teamColor?.toComposeColor() ?: defaultColor
        }
    }

    //dynamic drivers
    for (driver in drivers) {
        val fullName = driver.fullName ?: ""
        val name = driver.name
        if ((fullName.isNotEmpty() && content.contains(fullName, ignoreCase = true)) ||
            (name.isNotEmpty() && content.contains(name, ignoreCase = true))
        ) {
            return driver.teamColor?.toComposeColor() ?: defaultColor
        }
    }


    return defaultColor
}


