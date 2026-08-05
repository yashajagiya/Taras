package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.removeNameExtra
import com.example.taras.viewmodel.RaceClearData
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.SessionTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircuitData(
    circuitId: String,
    modifier: Modifier = Modifier,
    racesViewModel: RacesViewModel = viewModel()
) {
    val racesState by racesViewModel.combinedRaces.collectAsStateWithLifecycle()

    CircuitProfileContent(
        circuitId = circuitId,
        racesState = racesState,
        onRefresh = { racesViewModel.fetchRacesData() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircuitProfileContent(
    circuitId: String,
    racesState: UiState<ImmutableList<RaceClearData>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(racesState) {
            if (racesState !is UiState.Loading) {
                isRefreshing = false
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                onRefresh()
            },
            state = pullToRefreshState,
            indicator = {
                if (!isRefreshing) {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = false,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        ) {
            Surface(color = Color.Transparent) {
                when (racesState) {
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LoadingIndicator()
                            Spacer(Modifier.requiredHeight(30.dp))
                            Text("Loading Circuit Data...")
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = racesState.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onRefresh) {
                                Text("Retry")
                            }
                        }
                    }

                    is UiState.Success -> {
                        val race = racesState.data.find {
                            it.circuitId.equals(circuitId, ignoreCase = true)
                        }

                        if (race == null) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Circuit information not found",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                item {
                                    CircuitHeader(race)
                                }

                                if (race.winnerName.isNotEmpty()) {
                                    item {
                                        WinnerCard(race)
                                    }
                                }

                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        TechnicalSpecsCard(
                                            race = race,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                item {
                                    WeekendScheduleSection(race)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WinnerCard(race: RaceClearData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏆", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "RACE WINNER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = race.winnerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = race.winnerTeam,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CircuitHeader(
    race: RaceClearData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f) // Taller aspect ratio to make image feel bigger
            ) {
                AsyncImage(
                    model = race.trackImage,
                    contentDescription = "Circuit Map",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp), // Reduced padding to let the image expand
                    contentScale = ContentScale.Fit,
                    alpha = 0.9f
                )

                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Text(
                        text = "ROUND ${race.roundNumber} • ${race.gpName.uppercase()}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = race.circuitName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${race.city}, ${race.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TechnicalSpecsCard(race: RaceClearData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Circuit Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SpecRow(label = "CORNERS", value = race.corners.toString())
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            SpecRow(label = "LENGTH", value = race.circuitLength)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "LAP RECORD",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = race.lapRecord ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (race.fastestLapDriverId != null) {
                        Text(
                            text = "${race.fastestLapDriverId.removeNameExtra()}${if (race.fastestLapYear != null) ", ${race.fastestLapYear}" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            SpecRow(label = "FIRST GP", value = race.firstParticipationYear.toString())
            if (race.laps != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
                SpecRow(label = "LAPS", value = race.laps.toString())
            }
        }
    }
}

@Composable
fun SpecRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun WeekendScheduleSection(
    race: RaceClearData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Weekend Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            // Timeline line
            Box(
                modifier = Modifier
                    .padding(start = 19.dp)
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    .align(Alignment.CenterStart)
                    .matchParentSize()
            )

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SessionTimelineItem(name = "FP1", type = "Practice", time = race.fp1)
                race.fp2.date?.let {
                    SessionTimelineItem(
                        name = "FP2",
                        type = "Practice",
                        time = race.fp2
                    )
                }
                race.fp3.date?.let {
                    SessionTimelineItem(
                        name = "FP3",
                        type = "Practice",
                        time = race.fp3
                    )
                }
                race.sprintQualy.date?.let {
                    SessionTimelineItem(
                        name = "Sprint Qualy",
                        type = "Grid Setup",
                        time = race.sprintQualy
                    )
                }
                race.sprintRace.date?.let {
                    SessionTimelineItem(
                        name = "Sprint Race",
                        type = "Sprint",
                        time = race.sprintRace
                    )
                }
                SessionTimelineItem(
                    name = "Qualifying",
                    type = "Grid Setup",
                    time = race.qualy,
                    isHighlight = true
                )
                SessionTimelineItem(
                    name = "Race",
                    type = "Grand Prix",
                    time = race.race,
                    isHighlight = true,
                    isRace = true
                )
            }
        }
    }
}

@Composable
fun SessionTimelineItem(
    name: String,
    type: String,
    time: SessionTime,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false,
    isRace: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dot
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRace) {
                Icon(
                    imageVector = Icons.Default.SportsScore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(if (isHighlight) 12.dp else 8.dp)
                        .background(
                            if (isHighlight) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape
                        )
                )
            }
        }

        // Content
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighlight) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.8f
                        )
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatSessionDay(time.date),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = time.time?.take(5) ?: "--:--",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun formatSessionDay(dateStr: String?): String {
    if (dateStr == null) return ""
    return try {
        val date = LocalDate.parse(dateStr)
        date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        ""
    }
}
