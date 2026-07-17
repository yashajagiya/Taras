package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.api.taras.model.DriverDetail
import com.example.taras.api.taras.model.TeamsImageResponse
import com.example.taras.common.UiState
import com.example.taras.rss.RssItem
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.NewsViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val driversViewModel: DriversViewModel = viewModel()
            val teamsViewModel: TeamsViewModel = viewModel()
            val newsViewModel: NewsViewModel = viewModel()

            val driversState by driversViewModel.drivers.collectAsState()
            val driverDetailsState by driversViewModel.driverDetails.collectAsState()

            val teamsState by teamsViewModel.teams.collectAsState()
            val teamsImagesState by teamsViewModel.teamsImage.collectAsState()

            val newsState by newsViewModel.news.collectAsState()



            TarasTheme {
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp)
            ) {

                when (driversState) {
                    is UiState.Loading -> {
                        item { Text("Loading Drivers...", modifier = Modifier.padding(16.dp)) }
                    }

                    is UiState.Error -> {
                        item {
                            Text(
                                "Something went wrong with drivers",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    is UiState.Success -> {
                        val driverResponse = (driversState as UiState.Success).data
                        val drivers = driverResponse.entries
                        if (drivers.isEmpty()) {
                            item { Text("No Drivers Found", modifier = Modifier.padding(16.dp)) }
                        } else {
                            items(drivers) { driverData ->
                                val detail =
                                    (driverDetailsState as? UiState.Success)?.data?.find { it.driverNumber == driverData.driverNumber }
                                val driverColor =
                                    detail?.teamColour?.toComposeColor() ?: Color.White
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = driverColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = shapes.medium

                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = if (detail?.headshotUrl.isNullOrEmpty()) {
                                                "https://f1tv.formula1.com/static/favicon.ico"
                                            } else {
                                                detail.headshotUrl
                                            },
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.2f)),
                                            contentScale = ContentScale.Crop,
                                            alignment = Alignment.TopCenter
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1.5f)
                                                .padding(horizontal = 8.dp)
                                        ) {
                                            Text(
                                                text = detail?.fullName ?: driverData.name,
                                                style = typography.titleMedium
                                            )
                                            Text(
                                                text = detail?.teamName ?: driverData.teamName,
                                                style = typography.bodySmall
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Pos: ${driverData.rank}",
                                                    style = typography.labelLarge
                                                )
                                                Text(
                                                    text = "Pts: ${driverData.championshipPts.displayValue}",
                                                    style = typography.labelLarge
                                                )
                                            }
                                        }

                                        AsyncImage(
                                            model = if (detail?.racingNumberMask.isNullOrEmpty()) {
                                                "https://f1tv.formula1.com/static/favicon.ico"
                                            } else {
                                                detail.racingNumberMask
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
                        }
                    }
                }

                when (teamsState) {
                    is UiState.Loading -> {
                        item { Text("Loading Teams...", modifier = Modifier.padding(16.dp)) }
                    }

                    is UiState.Error -> {
                        item {
                            Text(
                                "Something went wrong with teams",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    is UiState.Success -> {
                        val teamsResponse = (teamsState as UiState.Success).data
                        val teamss = teamsResponse.entries

                        if (teamss.isEmpty()) {
                            item { Text("No Teams Found", modifier = Modifier.padding(16.dp)) }
                        } else {
                            items(teamss) { teamData ->
                                val detail =
                                    (teamsImagesState as? UiState.Success)?.data?.find {
                                        it.teamName.equals(teamData.team, ignoreCase = true)
                                    }
                                val teamColor = detail?.teamColor?.toComposeColor() ?: Color.White
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(
                                            containerColor = teamColor,
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
                                                    model = if (detail?.teamLogo.isNullOrEmpty()) {
                                                        "https://f1tv.formula1.com/static/favicon.ico"
                                                    } else {
                                                        detail.teamLogo
                                                    },
                                                    contentDescription = null,
                                                    modifier = Modifier.height(40.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                                Text(
                                                    text = teamData.team,
                                                    modifier = Modifier.padding(start = 8.dp),
                                                    style = typography.titleMedium
                                                )
                                            }

                                            AsyncImage(
                                                model = if (detail?.teamCar.isNullOrEmpty()) {
                                                    "https://f1tv.formula1.com/static/favicon.ico"
                                                } else {
                                                    detail.teamCar
                                                },
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),
                                                contentScale = ContentScale.Fit
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "Pos: ${teamData.rank}")
                                                Text(text = "Pts: ${teamData.points.displayValue}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                when (newsState) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Loading Latest News...", style = typography.bodyMedium)
                            }
                        }
                    }

                    is UiState.Error -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    "Unable to load news. Please try again later.",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Red
                                )
                            }
                        }
                    }

                    is UiState.Success -> {
                        val news = (newsState as UiState.Success).data
                        if (news.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📰",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Latest News",
                                        style = typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "LIVE",
                                        color = Color.Red,
                                        style = typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(
                                                Color.Red.copy(alpha = 0.1f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            item {
                                val pagerState = rememberPagerState(pageCount = { news.size })
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    pageSpacing = 12.dp
                                ) { page ->
                                    val newsItem = news[page]
                                    val defaultColor =colorScheme.surfaceVariant
                                    
                                    val driversList = (driverDetailsState as? UiState.Success)?.data ?: emptyList()
                                    val teamsList = (teamsImagesState as? UiState.Success)?.data ?: emptyList()
                                    
                                    val newsColor = getNewsColor(
                                        newsItem = newsItem,
                                        drivers = driversList,
                                        teams = teamsList,
                                        defaultColor = defaultColor
                                    )
                                    NewsCard(
                                        item = newsItem, 
                                        containerColor = newsColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getNewsColor(
    newsItem: RssItem,
    drivers: List<DriverDetail>,
    teams: List<TeamsImageResponse>,
    defaultColor: Color
): Color {
    val title = newsItem.title ?: ""
    val description = newsItem.description ?: ""
    val content = "$title $description"

    // 1. Check for specific Team Names (High Priority)
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

    // 2. Check API Team names
    for (team in teams) {
        if (content.contains(team.teamName, ignoreCase = true)) {
            return team.teamColor.toComposeColor()
        }
    }

    // 3. Check for Drivers (Low Priority)
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

fun String.toComposeColor(): Color {
    return try {
        val hex = this.removePrefix("0x").removePrefix("#")
        val colorLong = when (hex.length) {
            6 -> ("FF$hex").toLong(16) // Add alpha FF for 6-digit hex
            8 -> hex.toLong(16)
            else -> return Color.Transparent
        }
        Color(colorLong)
    } catch (e: Exception) {
        Color.Transparent
    }
}