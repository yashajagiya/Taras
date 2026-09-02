package com.example.taras.view.subview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.viewmodel.TeamUiModel

@Composable
fun TeamCard(
    teamData: TeamUiModel,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = teamData.teamColor?.toComposeColor() ?: Color.Transparent

    val contentColor = if (containerColor.luminance() > 0.5f) Color.Black else Color.White

    Card(
        modifier
            .fillMaxWidth()
            .clickable { onTeamClick(teamData.teamName) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = teamData.teamLogo?.takeIf { it.isNotEmpty() }
                        ?: "https://f1tv.formula1.com/static/favicon.ico",
                    contentDescription = "${teamData.teamName} Logo",
                    modifier = Modifier.height(40.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = teamData.teamName,
                    modifier = Modifier.padding(start = 8.dp),
                    style = typography.titleMedium
                )
            }

            AsyncImage(
                model = teamData.teamCar?.takeIf { it.isNotEmpty() }
                    ?: "https://f1tv.formula1.com/static/favicon.ico",
                contentDescription = "${teamData.teamName} Car",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pos: ${teamData.rank}",
                    style = typography.labelLarge
                )
                Text(
                    text = "Pts: ${teamData.points}",
                    style = typography.labelLarge
                )
            }
        }
    }
}