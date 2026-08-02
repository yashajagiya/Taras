package com.example.taras.view.subview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.viewmodel.DriverUiModel




@Composable
fun DriverCard(
    driver: DriverUiModel,
    onDriverClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = driver.teamColor?.toComposeColor() ?: Color.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable{onDriverClick(driver.driverNumber.toString())}
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (driver.headshotUrl.isNullOrEmpty()) {
                    "https://f1tv.formula1.com/static/favicon.ico"
                } else {
                    driver.headshotUrl
                },
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = driver.fullName ?: driver.name,
                    style = typography.titleMedium
                )
                Text(
                    text = driver.teamName,
                    style = typography.bodySmall
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pos: ${driver.rank}",
                        style = typography.labelLarge
                    )
                    Text(
                        text = "Pts: ${driver.points}",
                        style = typography.labelLarge
                    )
                }
            }

            AsyncImage(
                model = if (driver.carNumberImage.isNullOrEmpty()) {
                    "https://f1tv.formula1.com/static/favicon.ico"
                } else {
                    driver.carNumberImage
                },
                contentDescription = null,
                modifier = Modifier
                    .height(70.dp)
                    .weight(1f),
                contentScale = ContentScale.Fit,
                alpha = 0.8f
            )
        }
    }
}
