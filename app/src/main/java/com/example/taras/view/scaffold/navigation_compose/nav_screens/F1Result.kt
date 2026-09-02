package com.example.taras.view.scaffold.navigation_compose.nav_screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.UiState
import coil3.compose.AsyncImage
import com.example.taras.core.common.NetworkObserver
import com.example.taras.ui.theme.TeamAlpine
import com.example.taras.ui.theme.TeamAstonMartin
import com.example.taras.ui.theme.TeamFerrari
import com.example.taras.ui.theme.TeamHaas
import com.example.taras.ui.theme.TeamKickSauber
import com.example.taras.ui.theme.TeamMcLaren
import com.example.taras.ui.theme.TeamMercedes
import com.example.taras.ui.theme.TeamRedBull
import com.example.taras.ui.theme.TeamVisaCashApp
import com.example.taras.ui.theme.TeamWilliams
import com.example.taras.viewmodel.ResultHeader
import com.example.taras.viewmodel.ResultRowData
import com.example.taras.viewmodel.ResultViewModel
import com.example.taras.viewmodel.SessionResultUiState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun NavF1DriversScreen(
    modifier: Modifier = Modifier,
    resultViewModel: ResultViewModel = viewModel()
) {
    val fp1State by resultViewModel.fp1Results.collectAsStateWithLifecycle()
    val fp2State by resultViewModel.fp2Results.collectAsStateWithLifecycle()
    val fp3State by resultViewModel.fp3Results.collectAsStateWithLifecycle()
    val qualifyState by resultViewModel.qualifyResults.collectAsStateWithLifecycle()
    val raceState by resultViewModel.raceResults.collectAsStateWithLifecycle()
    val isSprintWeekend by resultViewModel.isSprintWeekend.collectAsStateWithLifecycle()
    val isRefreshing by resultViewModel.isRefreshing.collectAsStateWithLifecycle()

    ResultContent(
        fp1State = fp1State,
        fp2State = fp2State,
        fp3State = fp3State,
        qualifyState = qualifyState,
        resultState = raceState,
        isSprintWeekend = isSprintWeekend,
        isRefreshing = isRefreshing,
        onRefresh = {
            resultViewModel.fetchRacesResultData(isRefresh = true)
        },
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun ResultContent(
    fp1State: UiState<SessionResultUiState>,
    fp2State: UiState<SessionResultUiState>,
    fp3State: UiState<SessionResultUiState>,
    qualifyState: UiState<SessionResultUiState>,
    resultState: UiState<SessionResultUiState>,
    isSprintWeekend: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val networkObserver = remember { NetworkObserver(context) }
    val isConnected by networkObserver.isConnected.collectAsStateWithLifecycle(initialValue = true)

    val tabs = if (isSprintWeekend) {
        listOf("FP1", "Sprint Q", "Sprint Race", "Qualifying", "Results")
    } else {
        listOf("FP1", "FP2", "FP3", "Qualifying", "Results")
    }
    val pagerState = rememberPagerState(pageCount = { tabs.size })

//    DisposableEffect(context) {
//        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        context.registerReceiver(receiver, filter)
//         onDispose {
//             context.unregisterReceiver(receiver)
//         }
//
//    }

    LaunchedEffect(fp1State, fp2State, fp3State, qualifyState, resultState) {
    }

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
        Column(modifier = modifier.fillMaxSize()) {
            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(pagerState.currentPage, true)
                    )
                },
                divider = {},
                tabs = {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { page ->
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    when (page) {
                        0 -> SessionTabs(
                            state = fp1State,
                            isRefreshing = isRefreshing,
                            loadingMessage = "Loading practice results...",
                            isConnected = isConnected
                        )

                        1 -> SessionTabs(
                            state = fp2State,
                            isRefreshing = isRefreshing,
                            loadingMessage = if (isSprintWeekend) "Loading sprint qualifying results..." else "Loading practice results...",
                            isConnected = isConnected
                        )

                        2 -> SessionTabs(
                            state = fp3State,
                            isRefreshing = isRefreshing,
                            loadingMessage = if (isSprintWeekend) "Loading sprint race results..." else "Loading practice results...",
                            isConnected = isConnected
                        )

                        3 -> SessionTabs(
                            state = qualifyState,
                            isRefreshing = isRefreshing,
                            loadingMessage = "Loading qualifying results...",
                            isConnected = isConnected
                        )

                        4 -> SessionTabs(
                            state = resultState,
                            isRefreshing = isRefreshing,
                            loadingMessage = "Loading race results...",
                            isConnected = isConnected
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SessionTabs(
    state: UiState<SessionResultUiState>,
    isRefreshing: Boolean,
    loadingMessage: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        when (state) {
            is UiState.Loading -> {
                if (!isRefreshing) {
                    item(contentType = "Loading") { LoadingView(loadingMessage) }
                }
            }

            is UiState.Error -> {
                if (!isConnected) {
                    item(contentType = "Error") {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SignalWifiStatusbarConnectedNoInternet4,
                                contentDescription = "No internet connection",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No internet connection",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Something went wrong",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

            }

            is UiState.Success -> {
                val data = state.data
                if (!data.isStarted) {
                    item(contentType = "Upcoming") {
                        UpcomingSessionView(data.scheduledTime ?: "Upcoming")
                    }
                } else if (data.results.isEmpty()) {
                    item(contentType = "Empty") { EmptyView() }
                } else {
                    if (data.header != null) {
                        item(contentType = "Header") { SessionHeader(data.header) }
                    }
                    items(
                        data.results,
                        key = { it.position + it.number },
                        contentType = { "Result" }
                    ) { item ->
                        ResultCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionHeader(header: ResultHeader) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = header.raceName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = header.circuitName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun getTeamColor(teamName: String): Color {
    return when {
        teamName.contains("Ferrari", ignoreCase = true) -> TeamFerrari
        teamName.contains("McLaren", ignoreCase = true) -> TeamMcLaren
        teamName.contains("Red Bull", ignoreCase = true) -> TeamRedBull
        teamName.contains("Mercedes", ignoreCase = true) -> TeamMercedes
        teamName.contains("Aston Martin", ignoreCase = true) -> TeamAstonMartin
        teamName.contains("Alpine", ignoreCase = true) -> TeamAlpine
        teamName.contains("Williams", ignoreCase = true) -> TeamWilliams
        teamName.contains("RB", ignoreCase = true) || teamName.contains(
            "Visa",
            ignoreCase = true
        ) -> TeamVisaCashApp

        teamName.contains("Sauber", ignoreCase = true) -> TeamKickSauber
        teamName.contains("Haas", ignoreCase = true) -> TeamHaas
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun ResultCard(
    data: ResultRowData,
    modifier: Modifier = Modifier
) {
    val isFirstPlace = data.position == "1"
    val teamColor = getTeamColor(data.team)

    val pointsFloat = data.points?.toFloatOrNull() ?: 0f
    val displayPoints = if (pointsFloat > 0f) {
        if (pointsFloat % 1f == 0f) pointsFloat.toInt().toString() else pointsFloat.toString()
    } else null

    val containerColor = if (isFirstPlace) {
        MaterialTheme.colorScheme.surfaceContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFirstPlace) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "P${data.position}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isFirstPlace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(48.dp)
                    )

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .width(4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(teamColor)
                    )

                    AsyncImage(
                        model = data.headshotUrl ?: "https://f1tv.formula1.com/static/favicon.ico",
                        contentDescription = data.driver,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = Crop,
                        alignment = Alignment.TopCenter
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.driver,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = data.team,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (displayPoints != null) {
                        Text(
                            text = "$displayPoints PTS",
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (isFirstPlace) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "First Place",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (isFirstPlace) "TIME" else "GAP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.extra,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LAPS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.laps,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingView(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadingIndicator()
        Spacer(Modifier.requiredHeight(30.dp))
        Text(
            text = message
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UpcomingSessionView(
    scheduledTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Schedule,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Session hasn't started yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Scheduled for: $scheduledTime",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No data found for this session.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}