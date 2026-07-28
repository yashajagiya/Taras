package com.example.taras.view.scaffold.navigation_compose.nav_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.taras.model.DriverDetail
import com.example.taras.network_calls.taras.model.TeamsImageResponse
import com.example.taras.view.subview.NewsCarousel
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.NewsViewModel
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.SessionInfo
import com.example.taras.viewmodel.TeamsViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun NavPaddockScreen(
    modifier: Modifier = Modifier,
    driversViewModel: DriversViewModel = viewModel(),
    teamsViewModel: TeamsViewModel = viewModel(),
    newsViewModel: NewsViewModel = viewModel(),
    racesViewModel: RacesViewModel = viewModel()
) {
    val driverTopThree by driversViewModel.topThree.collectAsStateWithLifecycle()
    val driverDetails by driversViewModel.driverDetails.collectAsStateWithLifecycle()
    val teamsImage by teamsViewModel.teamsImage.collectAsStateWithLifecycle()
    val newsState by newsViewModel.news.collectAsStateWithLifecycle()
    val raceCurrentState by racesViewModel.oneRace.collectAsStateWithLifecycle()
    val nextSessionInfo by racesViewModel.nextSessionInfo.collectAsStateWithLifecycle()

    PaddockContent(
        modifier = modifier,
        driverTopThree = driverTopThree,
        driverDetails = driverDetails,
        teamsImage = teamsImage,
        newsState = newsState,
        raceCurrentState = raceCurrentState,
        nextSessionInfo = nextSessionInfo,
        onRefresh = {
            driversViewModel.fetchDriverData()
            teamsViewModel.fetchTeams()
            newsViewModel.fetchNews()
            racesViewModel.fetchRacesData()
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun PaddockContent(
    driverTopThree: UiState<ImmutableList<DriverUiModel>>,
    driverDetails: UiState<ImmutableList<DriverDetail>>,
    teamsImage: UiState<ImmutableList<TeamsImageResponse>>,
    newsState: UiState<ImmutableList<RssItem>>,
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfo: SessionInfo?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        var isRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(driverTopThree, newsState, raceCurrentState, teamsImage) {
            if (driverTopThree !is UiState.Loading &&
                newsState !is UiState.Loading &&
                raceCurrentState !is UiState.Loading &&
                teamsImage !is UiState.Loading
            ) {
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
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                val isAnyLoading = driverTopThree is UiState.Loading ||
                        newsState is UiState.Loading ||
                        raceCurrentState is UiState.Loading ||
                        teamsImage is UiState.Loading

                val isAnyError = driverTopThree is UiState.Error ||
                        newsState is UiState.Error ||
                        raceCurrentState is UiState.Error ||
                        teamsImage is UiState.Error

                if (isAnyLoading && !isRefreshing && !isAnyError) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingIndicator()
                        Spacer(Modifier.height(24.dp))
                        Text("Loading...")
                    }
                } else if (isAnyError && !isRefreshing) {
                    val errorMessage = when {
                        raceCurrentState is UiState.Error -> raceCurrentState.message
                        driverTopThree is UiState.Error -> driverTopThree.message
                        newsState is UiState.Error -> newsState.message
                        teamsImage is UiState.Error -> teamsImage.message
                        else -> "An unknown error occurred"
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            val raceCurrent = (raceCurrentState as? UiState.Success)?.data

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "NEXT SESSION",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp
                                        )
                                        Text(
                                            text = "LIVE",
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(MaterialTheme.colorScheme.primary)
                                                .padding(horizontal = 12.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = (nextSessionInfo?.raceName ?: raceCurrent?.raceName)
                                            ?.takeIf { it.isNotBlank() } ?: "Upcoming Race",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = nextSessionInfo?.countdown ?: "00:00:00",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (nextSessionInfo != null) {
                                            "until ${nextSessionInfo.sessionName} · ${nextSessionInfo.circuitName}"
                                        } else {
                                            "No upcoming sessions · ${raceCurrent?.circuitName ?: "N/A"}"
                                        },
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        item {
                            val drivers = (driverTopThree as? UiState.Success)?.data
                            val p1Driver = drivers?.getOrNull(0)
                            val p2Driver = drivers?.getOrNull(1)
                            val p3Driver = drivers?.getOrNull(2)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // P1 Leader Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors
                                        (containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "CHAMPIONSHIP",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.7f
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = "P1",
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        AsyncImage(
                                            model = p1Driver?.headshotUrl
                                                ?: "https://f1tv.formula1.com/static/favicon.ico",
                                            contentDescription = "Leader",
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentScale = ContentScale.Crop,
                                            alignment = Alignment.TopCenter
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = p1Driver?.name ?: "N/A",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = p1Driver?.teamName ?: "N/A",
                                            color = p1Driver?.teamColor?.toComposeColor()
                                                ?: MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.8f
                                                ),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = p1Driver?.points ?: "N/A",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = " PTS",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(
                                                    bottom = 6.dp,
                                                    start = 4.dp
                                                )
                                            )
                                        }
                                    }
                                }

                                // P2 & P3 Column
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    RunnerUpDriverCard(driver = p2Driver, positionLabel = "P2")
                                    RunnerUpDriverCard(driver = p3Driver, positionLabel = "P3")
                                }
                            }
                        }

                        item {
                            NewsCarousel(
                                newsState = newsState,
                                modifier = Modifier.padding(vertical = 8.dp),
                                drivers = (driverDetails as? UiState.Success)?.data
                                    ?: persistentListOf(),
                                teams = (teamsImage as? UiState.Success)?.data ?: persistentListOf()
                            )
                        }
                    }
                }
            }
        }
    }
}

//P2 / P3 Drivers
@Composable
private fun RunnerUpDriverCard(
    driver: DriverUiModel?,
    positionLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = driver?.name ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Clip
                )
                Text(
                    text = positionLabel,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = driver?.teamName ?: "N/A",
                color = driver?.teamColor?.toComposeColor()
                    ?: MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = driver?.points ?: "N/A",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " PTS",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }
        }
    }
}