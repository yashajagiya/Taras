package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.network_calls.taras.model.Racedata
import com.example.taras.viewmodel.DetailedTeamUiModel
import com.example.taras.viewmodel.TeamsViewModel
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TeamsData(
    teamName: String,
    modifier: Modifier = Modifier,
    teamsViewModel: TeamsViewModel = viewModel()
) {
    val teamsDetailsState by teamsViewModel.combinedDetailedTeams.collectAsStateWithLifecycle()

    TeamProfileContent(
        tName = teamName,
        modifier = modifier,
        teamsDetailsState = teamsDetailsState,
        onRefresh = { teamsViewModel.fetchTeams() }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TeamProfileContent(
    tName: String,
    teamsDetailsState: UiState<ImmutableList<DetailedTeamUiModel>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(teamsDetailsState, tName) {
            if (teamsDetailsState !is UiState.Loading && tName.isNotEmpty()) {
                isRefreshing = false
            } else if (teamsDetailsState is UiState.Error && tName.isEmpty()) {
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
                val isAnyLoading = teamsDetailsState is UiState.Loading
                val isAnyError = teamsDetailsState is UiState.Error || tName.isEmpty()

                if (!isAnyError && isAnyLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingIndicator()
                        Spacer(Modifier.requiredHeight(30.dp))
                        Text("Loading...")
                    }
                } else if (isAnyError && !isRefreshing) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Something went wrong",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Retry Again")
                        }
                    }
                } else {
                    val teamsDetails = (teamsDetailsState as? UiState.Success)?.data
                    val team = teamsDetails?.find { it.teamName.equals(tName, ignoreCase = true) }

                    if (team == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Team not found",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item(contentType = "Header") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = team.teamColor.toComposeColor()
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .padding(16.dp)
                                    ) {
                                        AsyncImage(
                                            model = team.teamLogoUrl.takeIf { !it.isNullOrEmpty() }
                                                ?: "https://f1tv.formula1.com/static/favicon.ico",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(200.dp)
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-20).dp),
                                            alpha = 0.2f,
                                            contentScale = ContentScale.Fit
                                        )

                                        AsyncImage(
                                            model = team.teamCarUrl.takeIf { !it.isNullOrEmpty() }
                                                ?: "https://f1tv.formula1.com/static/favicon.ico",
                                            contentDescription = "${team.teamName} Car",
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            alignment = Alignment.Center,
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = team.teamColor.toComposeColor()
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(
                                        bottomStart = 28.dp,
                                        bottomEnd = 28.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = team.teamName,
                                            fontSize = 36.sp,
                                            letterSpacing = 1.sp,
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = team.fullTeamName,
                                            fontSize = 14.sp,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Flag,
                                                contentDescription = "Base",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = team.baseLocation,
                                                fontSize = 16.sp,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                            item(contentType = "Stats") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TeamStatItem(label = "RANK", value = "P${team.rank}")
                                        VerticalDivider(
                                            Modifier.padding(8.dp),
                                            thickness = 2.dp,
                                            color = Color.Gray.copy(alpha = 0.3f)
                                        )
                                        TeamStatItem(
                                            label = "POINTS",
                                            value = team.currentPointsDisplay
                                        )
                                        VerticalDivider(
                                            Modifier.padding(8.dp),
                                            thickness = 2.dp,
                                            color = Color.Gray.copy(alpha = 0.3f)
                                        )
                                        TeamStatItem(
                                            label = "FIRST ENTRY",
                                            value = team.firstTeamEntryYear
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }

                            item(contentType = "Management") {
                                Text(
                                    text = "Management & Technical",
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.height(12.dp))

                                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    ManagementDetailCard(
                                        label = "Team Chief",
                                        value = team.teamChief,
                                        icon = Icons.Default.Groups
                                    )
                                    ManagementDetailCard(
                                        label = "Technical Chief",
                                        value = team.technicalChief,
                                        icon = Icons.Default.Settings
                                    )
                                    ManagementDetailCard(
                                        label = "Chassis",
                                        value = team.chassis,
                                        icon = Icons.Default.PrecisionManufacturing
                                    )
                                    ManagementDetailCard(
                                        label = "Power Unit",
                                        value = team.powerUnit,
                                        icon = Icons.Default.Settings
                                    )
                                    if (team.reserveDriver.isNotEmpty()) {
                                        ManagementDetailCard(
                                            label = "Reserve Driver",
                                            value = team.reserveDriver,
                                            icon = Icons.Default.Groups
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }

                            team.seasonStats?.let { stats ->
                                item(contentType = "Performance") {
                                    Text(
                                        text = "2026 Performance",
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(Modifier.height(16.dp))

                                    val performanceStats = listOf(
                                        "GP Races" to stats.grandPrixRaces,
                                        "Wins" to stats.grandPrixWins,
                                        "Podiums" to stats.grandPrixPodiums,
                                        "Poles" to stats.grandPrixPoles,
                                        "Fastest Laps" to stats.dhlFastestLaps,
                                        "Top 10s" to stats.grandPrixTop10s,
                                        "DNFs" to stats.dnfs,
                                        "Sprint Wins" to stats.sprintWins
                                    )

                                    FlowRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        performanceStats.forEach { (label, value) ->
                                            StateMiniCard(
                                                label = label,
                                                value = value,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .defaultMinSize(minWidth = 100.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(24.dp))
                                }
                            }

                            team.teamSummary?.let { summary ->
                                item(contentType = "History") {
                                    Text(
                                        text = "Team History",
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        textAlign = TextAlign.Start,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(Modifier.height(16.dp))

                                    Column(Modifier.padding(horizontal = 24.dp)) {
                                        if ((summary.worldChampionships.toIntOrNull() ?: 0) > 0) {
                                            CareerStatsCard(
                                                count = summary.worldChampionships,
                                                title = "World Championships",
                                                icon = Icons.Default.EmojiEvents,
                                                imageColor = MaterialTheme.colorScheme.onPrimary,
                                                valueColor = MaterialTheme.colorScheme.onPrimary,
                                                titleColor = MaterialTheme.colorScheme.onPrimary,
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                            )
                                            Spacer(Modifier.height(16.dp))
                                        }

                                        CareerStatsCard(
                                            count = summary.highestRaceFinish,
                                            title = "Grand Prix Wins",
                                            icon = Icons.Default.FlagCircle,
                                            imageColor = MaterialTheme.colorScheme.primary.copy(
                                                alpha = .5f
                                            ),
                                            valueColor = MaterialTheme.colorScheme.primary,
                                            titleColor = Color.Black,
                                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        Spacer(Modifier.height(16.dp))

                                        Row {
                                            CareerStatsMiniCard(
                                                title = "Career Points",
                                                value = summary.teamPoints,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(16.dp))
                                            CareerStatsMiniCard(
                                                title = "Podium Finishes",
                                                value = summary.podiums,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        Spacer(Modifier.height(16.dp))
                                        Row {
                                            CareerStatsMiniCard(
                                                title = "Pole Positions",
                                                value = summary.polePositions,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(16.dp))
                                            CareerStatsMiniCard(
                                                title = "GP Entered",
                                                value = summary.grandsPrixEntered,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(24.dp))
                                }
                            }

                            item(contentType = "Chart") {
                                Column(
                                    Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Points Progression",
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = Color.Black,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ChartPerTeam(
                                        perRace = team.races,
                                        points = team.currentPointsDisplay,
                                    )
                                }
                                Spacer(Modifier.height(24.dp))
                            }

                            item(contentType = "Bio") {
                                Text(
                                    text = "Biography",
                                    style = MaterialTheme.typography.titleLarge,
                                    letterSpacing = 1.sp,
                                    color = Color.Black,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                TeamBioCard(bioText = team.biography)
                                Spacer(Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartPerTeam(
    perRace: ImmutableList<com.example.taras.network_calls.taras.model.Racedata>,
    points: String
) {
    val dataSet = remember(perRace, points) {
        perRace.map { it.value }.toChartDataSet(
            title = "Points Progression +$points",
            labels = perRace.map { it.name }
        )
    }
    LineChart(
        dataSet = dataSet,
        style = LineChartDefaults.style(
            pointVisible = true,
            pointColor = MaterialTheme.colorScheme.primary,
            pointSize = 8f,
            xAxisLabelsVisible = true,
            yAxisLabelsVisible = true,
            xAxisLabelMaxCount = perRace.size
        )
    )
}

@Composable
private fun TeamStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ManagementDetailCard(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value.takeIf { it.isNotBlank() } ?: "N/A",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TeamBioCard(bioText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = bioText,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}