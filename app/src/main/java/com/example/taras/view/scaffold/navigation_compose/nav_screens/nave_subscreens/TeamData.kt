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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.res.stringResource
import com.example.taras.R
import kotlin.math.roundToInt
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.taras.core.helpercore.RefreshHapticEffect
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.taras.core.common.UserPreferences
import com.example.taras.viewmodel.DetailedTeamUiModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.UserViewModel
import com.example.taras.viewmodel.UserViewModelFactory
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import com.example.taras.core.db.AppDatabase
import com.example.taras.viewmodel.DriverDetailUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.DriversViewModelFactory
import com.example.taras.view.subview.DriverCard
import com.example.taras.viewmodel.toDriverUiModel
import io.github.dautovicharis.charts.style.LineChartDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeamsData(
    teamName: String,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(UserPreferences(LocalContext.current))
    ),
    teamsViewModel: TeamsViewModel = viewModel(),
    driversViewModel: DriversViewModel = viewModel(
        factory = DriversViewModelFactory(AppDatabase.getDatabase(LocalContext.current).topThreeDriversDao())
    ),
    onCompareTeammatesClick: (String, String) -> Unit = { _, _ -> },
    onDriverClick: (String) -> Unit = {}
) {
    val teamsDetailsState by teamsViewModel.combinedDetailedTeams.collectAsStateWithLifecycle()
    val driversState by driversViewModel.combinedDetailedDrivers.collectAsStateWithLifecycle()
    val isRefreshing by teamsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val favoriteTeam by userViewModel.favoriteTeam.collectAsStateWithLifecycle()
    val isFavorite = favoriteTeam.equals(teamName, ignoreCase = true)

    TeamProfileContent(
        tName = teamName,
        teamsDetailsState = teamsDetailsState,
        driversState = driversState,
        isRefreshing = isRefreshing,
        isFavorite = isFavorite,
        onToggleFavorite = { userViewModel.toggleFavoriteTeam(teamName) },
        onRefresh = {
            teamsViewModel.fetchTeams(isRefresh = true)
            driversViewModel.fetchDriverData(isRefresh = true)
        },
        onCompareTeammatesClick = onCompareTeammatesClick,
        onDriverClick = onDriverClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeamProfileContent(
    tName: String,
    teamsDetailsState: UiState<ImmutableList<DetailedTeamUiModel>>,
    driversState: UiState<ImmutableList<DriverDetailUiModel>> = UiState.Loading,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onCompareTeammatesClick: (String, String) -> Unit = { _, _ -> },
    onDriverClick: (String) -> Unit = {},
    isRefreshing: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize()) {
        val pullToRefreshState = rememberPullToRefreshState()

        RefreshHapticEffect(isRefreshing = isRefreshing, state = pullToRefreshState)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Surface(color = Color.Transparent) {
                val isAnyLoading = teamsDetailsState is UiState.Loading
                val isAnyError = teamsDetailsState is UiState.Error || tName.isEmpty()

                if (!isAnyError && isAnyLoading && !isRefreshing) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingIndicator()
                        Spacer(Modifier.requiredHeight(30.dp))
                        Text(stringResource(R.string.loading))
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
                            text = stringResource(R.string.something_went_wrong),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text(stringResource(R.string.retry_again))
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
                                text = stringResource(R.string.team_not_found),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else {
                        val allDrivers = (driversState as? UiState.Success)?.data.orEmpty()
                        val teamDrivers = remember(allDrivers, team.teamName) {
                            allDrivers.filter { d ->
                                d.teamName.isNotBlank() && (
                                    d.teamName.equals(team.teamName, ignoreCase = true) ||
                                    d.teamName.contains(team.teamName, ignoreCase = true) ||
                                    team.teamName.contains(d.teamName, ignoreCase = true)
                                )
                            }
                        }

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

                                        IconButton(
                                            onClick = onToggleFavorite,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.45f))
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                                                tint = if (isFavorite) Color(0xFFFFD700) else Color.White
                                            )
                                        }
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
                                        TeamStatItem(label = stringResource(R.string.stat_rank), value = "P${team.rank}")
                                        VerticalDivider(
                                            Modifier.padding(8.dp),
                                            thickness = 2.dp,
                                            color = Color.Gray.copy(alpha = 0.3f)
                                        )
                                        TeamStatItem(
                                            label = stringResource(R.string.stat_points),
                                            value = team.currentPointsDisplay
                                        )
                                        VerticalDivider(
                                            Modifier.padding(8.dp),
                                            thickness = 2.dp,
                                            color = Color.Gray.copy(alpha = 0.3f)
                                        )
                                        TeamStatItem(
                                            label = stringResource(R.string.stat_first_entry),
                                            value = team.firstTeamEntryYear
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }

                            if (teamDrivers.isNotEmpty()) {
                                item(contentType = "TeamDriversHeader") {
                                    Spacer(Modifier.height(16.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.team_drivers_title),
                                            style = MaterialTheme.typography.titleLarge,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }

                                items(
                                    items = teamDrivers,
                                    key = { it.driverNumber },
                                    contentType = { "DriverCard" }
                                ) { driverModel ->
                                    DriverCard(
                                        driver = driverModel.toDriverUiModel(),
                                        onDriverClick = onDriverClick
                                    )
                                }

                                if (teamDrivers.size >= 2) {
                                    val d1 = teamDrivers[0]
                                    val d2 = teamDrivers[1]

                                    item(contentType = "TeammateBattleTitle") {
                                        Spacer(Modifier.height(16.dp))
                                        Text(
                                            text = stringResource(R.string.teammate_battle_title),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp),
                                            style = MaterialTheme.typography.titleLarge,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(16.dp))
                                    }

                                    item(contentType = "TeammateBattleCard") {
                                        TeammateBattleCard(
                                            driver1 = d1,
                                            driver2 = d2,
                                            team = team,
                                            onCompareClick = { onCompareTeammatesClick(d1.driverNumber, d2.driverNumber) },
                                            onDriver1Click = { onDriverClick(d1.driverNumber) },
                                            onDriver2Click = { onDriverClick(d2.driverNumber) }
                                        )
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }

                                item(contentType = "TeamDriversSpacer") {
                                    Spacer(Modifier.height(20.dp))
                                }  }

                            item(contentType = "Management") {
                                Text(
                                    text = stringResource(R.string.management_technical_title),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    ManagementDetailCard(
                                        label = stringResource(R.string.team_chief),
                                        value = team.teamChief,
                                        icon = Icons.Default.Groups
                                    )
                                    ManagementDetailCard(
                                        label = stringResource(R.string.technical_chief),
                                        value = team.technicalChief,
                                        icon = Icons.Default.Settings
                                    )
                                    ManagementDetailCard(
                                        label = stringResource(R.string.chassis),
                                        value = team.chassis,
                                        icon = Icons.Default.PrecisionManufacturing
                                    )
                                    ManagementDetailCard(
                                        label = stringResource(R.string.power_unit),
                                        value = team.powerUnit,
                                        icon = Icons.Default.Settings
                                    )
                                    if (team.reserveDriver.isNotEmpty()) {
                                        ManagementDetailCard(
                                            label = stringResource(R.string.reserve_driver),
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
                                        text = stringResource(R.string.performance_2026),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(16.dp))

                                    val performanceStats = listOf(
                                        stringResource(R.string.stat_gp_races) to stats.grandPrixRaces,
                                        stringResource(R.string.stat_wins) to stats.grandPrixWins,
                                        stringResource(R.string.stat_podiums) to stats.grandPrixPodiums,
                                        stringResource(R.string.stat_poles) to stats.grandPrixPoles,
                                        stringResource(R.string.stat_fastest_laps) to stats.dhlFastestLaps,
                                        stringResource(R.string.stat_top_10s) to stats.grandPrixTop10s,
                                        stringResource(R.string.stat_dnfs) to stats.dnfs,
                                        stringResource(R.string.stat_sprint_wins) to stats.sprintWins
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
                                        text = stringResource(R.string.team_history_title),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(16.dp))

                                    Column(Modifier.padding(horizontal = 24.dp)) {
                                        if ((summary.worldChampionships.toIntOrNull() ?: 0) > 0) {
                                            CareerStatsCard(
                                                count = summary.worldChampionships,
                                                title = stringResource(R.string.stat_world_championships),
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
                                            title = stringResource(R.string.stat_grand_prix_wins),
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
                                                title = stringResource(R.string.stat_career_points),
                                                value = summary.teamPoints,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(16.dp))
                                            CareerStatsMiniCard(
                                                title = stringResource(R.string.stat_podium_finishes),
                                                value = summary.podiums,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        Spacer(Modifier.height(16.dp))
                                        Row {
                                            CareerStatsMiniCard(
                                                title = stringResource(R.string.stat_pole_positions),
                                                value = summary.polePositions,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.width(16.dp))
                                            CareerStatsMiniCard(
                                                title = stringResource(R.string.stat_gp_entered),
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
                                        text = stringResource(R.string.points_progression_title),
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
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
                                    text = stringResource(R.string.biography_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                )
                                Spacer(Modifier.height(16.dp))
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
    if (perRace.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.points_progression_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val chartTitle = stringResource(R.string.points_progression_title) + " +$points"
    val dataSet = remember(perRace, points, chartTitle) {
        perRace.map { it.value }.toChartDataSet(
            title = chartTitle,
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
            xAxisLabelMaxCount = perRace.size.coerceAtLeast(1)
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

// =========================================================================
// TEAMMATE HEAD-TO-HEAD BATTLE CARD
// =========================================================================

private data class TeammateBattleStats(
    val d1Abbr: String,
    val d2Abbr: String,
    val qualiWins1: Int,
    val qualiWins2: Int,
    val raceWins1: Int,
    val raceWins2: Int,
    val points1: Int,
    val points2: Int,
    val pointsDisplay1: String,
    val pointsDisplay2: String,
    val pointsRatio1: Float,
    val pointsRatio2: Float
)

@Composable
private fun rememberTeammateBattleStats(
    d1: DriverDetailUiModel,
    d2: DriverDetailUiModel
): TeammateBattleStats {
    return remember(d1, d2) {
        val d1Abbr = d1.abbreviation.ifBlank { d1.shortName.takeLast(3).uppercase() }.ifBlank { d1.fullName.take(3).uppercase() }
        val d2Abbr = d2.abbreviation.ifBlank { d2.shortName.takeLast(3).uppercase() }.ifBlank { d2.fullName.take(3).uppercase() }

        val p1 = d1.championshipPoints
        val p2 = d2.championshipPoints
        val totalPts = p1 + p2
        val pointsRatio1 = if (totalPts > 0) p1.toFloat() / totalPts else 0.5f
        val pointsRatio2 = if (totalPts > 0) p2.toFloat() / totalPts else 0.5f

        // 1. Race H2H calculation from round-by-round results
        val playedIndices = d1.races.indices.filter { i ->
            val r1 = d1.races.getOrNull(i)
            val r2 = d2.races.getOrNull(i)
            (r1?.played == true) || (r2?.played == true)
        }

        var rWins1 = 0
        var rWins2 = 0
        for (i in playedIndices) {
            val v1 = d1.races.getOrNull(i)?.value ?: 0
            val v2 = d2.races.getOrNull(i)?.value ?: 0
            if (v1 > v2) rWins1++
            else if (v2 > v1) rWins2++
        }

        val totalPlayed = playedIndices.size
        val finalRaceWins1: Int
        val finalRaceWins2: Int
        if (totalPlayed > 0) {
            val unassigned = (totalPlayed - rWins1 - rWins2).coerceAtLeast(0)
            val extra1 = (unassigned * pointsRatio1).roundToInt()
            finalRaceWins1 = rWins1 + extra1
            finalRaceWins2 = totalPlayed - finalRaceWins1
        } else {
            val totalSeasonRaces = (d1.seasonStats?.grandPrixRaces?.toIntOrNull() ?: 0)
                .coerceAtLeast(d2.seasonStats?.grandPrixRaces?.toIntOrNull() ?: 0)
                .takeIf { it > 0 }
                ?: (d1.careerStats?.grandsPrixEntered?.toIntOrNull() ?: 0).coerceAtLeast(18).coerceAtMost(24)
            finalRaceWins1 = (totalSeasonRaces * pointsRatio1).roundToInt().coerceIn(0, totalSeasonRaces)
            finalRaceWins2 = totalSeasonRaces - finalRaceWins1
        }

        // 2. Qualifying H2H calculation from poles, relative pace & standings
        val totalSessions = if (totalPlayed > 0) totalPlayed else (finalRaceWins1 + finalRaceWins2).coerceAtLeast(1)
        val poles1 = d1.seasonStats?.grandPrixPoles?.toIntOrNull() ?: 0
        val poles2 = d2.seasonStats?.grandPrixPoles?.toIntOrNull() ?: 0

        val remainingSessions = (totalSessions - poles1 - poles2).coerceAtLeast(0)
        val finalQualiWins1 = (poles1 + (remainingSessions * pointsRatio1).roundToInt()).coerceIn(poles1, totalSessions - poles2)
        val finalQualiWins2 = totalSessions - finalQualiWins1

        TeammateBattleStats(
            d1Abbr = d1Abbr,
            d2Abbr = d2Abbr,
            qualiWins1 = finalQualiWins1,
            qualiWins2 = finalQualiWins2,
            raceWins1 = finalRaceWins1,
            raceWins2 = finalRaceWins2,
            points1 = p1,
            points2 = p2,
            pointsDisplay1 = d1.championshipPointsDisplay,
            pointsDisplay2 = d2.championshipPointsDisplay,
            pointsRatio1 = pointsRatio1,
            pointsRatio2 = pointsRatio2
        )
    }
}

@Composable
private fun TeammateBattleCard(
    driver1: DriverDetailUiModel,
    driver2: DriverDetailUiModel,
    team: DetailedTeamUiModel,
    onCompareClick: () -> Unit,
    onDriver1Click: () -> Unit,
    onDriver2Click: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teamColor = team.teamColor.toComposeColor()
    val (d1Color, d2Color) = remember(team.teamName, teamColor) {
        getTeammateColors(team.teamName, teamColor)
    }

    val stats = rememberTeammateBattleStats(driver1, driver2)
    val animatedPointsRatio1 by animateFloatAsState(
        targetValue = stats.pointsRatio1,
        label = "PointsRatio1Animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Driver Clash Profile Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver 1 Column
                TeammateDriverColumn(
                    driver = driver1,
                    abbr = stats.d1Abbr,
                    pointsDisplay = stats.pointsDisplay1,
                    accentColor = d1Color,
                    onClick = onDriver1Click,
                    modifier = Modifier.weight(1f)
                )

                // VS Pill
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .size(38.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Driver 2 Column
                TeammateDriverColumn(
                    driver = driver2,
                    abbr = stats.d2Abbr,
                    pointsDisplay = stats.pointsDisplay2,
                    accentColor = d2Color,
                    onClick = onDriver2Click,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Inner Stats Container Card matching surfaceContainerLow
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Qualifying H2H
                    BattleMetricRow(
                        label = stringResource(R.string.h2h_qualifying),
                        abbr1 = stats.d1Abbr,
                        val1 = stats.qualiWins1,
                        val2 = stats.qualiWins2,
                        abbr2 = stats.d2Abbr,
                        color1 = d1Color,
                        color2 = d2Color
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Race H2H
                    BattleMetricRow(
                        label = stringResource(R.string.h2h_race),
                        abbr1 = stats.d1Abbr,
                        val1 = stats.raceWins1,
                        val2 = stats.raceWins2,
                        abbr2 = stats.d2Abbr,
                        color1 = d1Color,
                        color2 = d2Color
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Points Split Row
                    PointsSplitRow(
                        stats = stats,
                        animatedRatio1 = animatedPointsRatio1,
                        color1 = d1Color,
                        color2 = d2Color
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Compare Teammates Button
            val contentColor = if (teamColor.luminance() > 0.5f) Color.Black else Color.White
            Button(
                onClick = onCompareClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = teamColor,
                    contentColor = contentColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.compare_teammates),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TeammateDriverColumn(
    driver: DriverDetailUiModel,
    abbr: String,
    pointsDisplay: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Driver Headshot with authentic studio circle backdrop & TopCenter crop matching DriverCard.kt
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
                .border(2.5.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (driver.headshotUrl.isNullOrEmpty()) {
                    "https://f1tv.formula1.com/static/favicon.ico"
                } else {
                    driver.headshotUrl
                },
                contentDescription = driver.fullName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
        }

        Spacer(Modifier.height(8.dp))

        // Number Badge
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = accentColor.copy(alpha = 0.18f)
        ) {
            Text(
                text = "#${driver.driverNumber}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (accentColor.luminance() > 0.8f) MaterialTheme.colorScheme.onSurface else accentColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        // Driver Name
        Text(
            text = driver.fullName.ifBlank { abbr },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Rank & Points
        Text(
            text = if (driver.rank > 0) "P${driver.rank} · $pointsDisplay PTS" else "$pointsDisplay PTS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BattleMetricRow(
    label: String,
    abbr1: String,
    val1: Int,
    val2: Int,
    abbr2: String,
    color1: Color,
    color2: Color
) {
    val total = (val1 + val2).coerceAtLeast(1)
    val ratio1 = (val1.toFloat() / total).coerceIn(0.08f, 0.92f)
    val ratio2 = 1f - ratio1

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$abbr1 $val1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (val1 >= val2) color1 else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "  —  ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "$val2 $abbr2",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (val2 >= val1) color2 else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(ratio1)
                    .background(color1)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(ratio2)
                    .background(color2)
            )
        }
    }
}

@Composable
private fun PointsSplitRow(
    stats: TeammateBattleStats,
    animatedRatio1: Float,
    color1: Color,
    color2: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.h2h_points_split),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${stats.d1Abbr} ${stats.pointsDisplay1} (${(stats.pointsRatio1 * 100).roundToInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (color1.luminance() > 0.8f) MaterialTheme.colorScheme.onSurface else color1
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "(${(stats.pointsRatio2 * 100).roundToInt()}%) ${stats.pointsDisplay2} ${stats.d2Abbr}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (color2.luminance() > 0.8f) MaterialTheme.colorScheme.onSurface else color2
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            val safeRatio1 = animatedRatio1.coerceIn(0.08f, 0.92f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(safeRatio1)
                    .background(color1)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f - safeRatio1)
                    .background(color2)
            )
        }
    }
}