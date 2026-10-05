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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import com.example.taras.R
import com.example.taras.core.helpercore.RaceResultShareHelper
import com.example.taras.core.helpercore.RefreshHapticEffect
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
import coil3.compose.rememberAsyncImagePainter
import com.example.taras.network_calls.ApiConstants
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
    val selectedRound by resultViewModel.selectedRound.collectAsStateWithLifecycle()
    val availableRounds by resultViewModel.availableRounds.collectAsStateWithLifecycle()

    ResultContent(
        fp1State = fp1State,
        fp2State = fp2State,
        fp3State = fp3State,
        qualifyState = qualifyState,
        resultState = raceState,
        isSprintWeekend = isSprintWeekend,
        isRefreshing = isRefreshing,
        selectedRound = selectedRound,
        availableRounds = availableRounds,
        onRoundSelected = { resultViewModel.selectRound(it) },
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
    selectedRound: Int?,
    availableRounds: List<Int>,
    onRoundSelected: (Int) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val networkObserver = remember { NetworkObserver(context) }
    val isConnected by networkObserver.isConnected.collectAsStateWithLifecycle(initialValue = true)

    val tabs = if (isSprintWeekend) {
        listOf(
            stringResource(R.string.session_fp1),
            stringResource(R.string.session_sprint_qualy),
            stringResource(R.string.session_sprint_race),
            stringResource(R.string.session_qualy),
            stringResource(R.string.session_results)
        )
    } else {
        listOf(
            stringResource(R.string.session_fp1),
            stringResource(R.string.session_fp2),
            stringResource(R.string.session_fp3),
            stringResource(R.string.session_qualy),
            stringResource(R.string.session_results)
        )
    }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var shareSessionData by remember { mutableStateOf<Pair<String, SessionResultUiState>?>(null) }

    val currentSessionState = when (pagerState.currentPage) {
        0 -> fp1State
        1 -> fp2State
        2 -> fp3State
        3 -> qualifyState
        4 -> resultState
        else -> resultState
    }
    val currentResults = (currentSessionState as? UiState.Success)?.data
    val canShare = currentResults != null && currentResults.results.isNotEmpty()

    val loadingFp = stringResource(R.string.results_loading_fp)
    val loadingSprintQ = stringResource(R.string.results_loading_sprint_qualy)
    val loadingSprintRace = stringResource(R.string.results_loading_sprint_race)
    val loadingQualy = stringResource(R.string.results_loading_qualy)
    val loadingRace = stringResource(R.string.results_loading_race)

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = modifier.fillMaxSize()) {
                if (availableRounds.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(availableRounds) { round ->
                            val isSelected = selectedRound == round
                            val isLatest = round == availableRounds.firstOrNull()
                            val chipText = if (isLatest) "Round $round (Latest)" else "Round $round"
                            FilterChip(
                                selected = isSelected,
                                onClick = { onRoundSelected(round) },
                                label = {
                                    Text(
                                        text = chipText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

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
                                loadingMessage = loadingFp,
                                isConnected = isConnected,
                                onShareClick = { shareSessionData = tabs[0] to it }
                            )

                            1 -> SessionTabs(
                                state = fp2State,
                                isRefreshing = isRefreshing,
                                loadingMessage = if (isSprintWeekend) loadingSprintQ else loadingFp,
                                isConnected = isConnected,
                                onShareClick = { shareSessionData = tabs[1] to it }
                            )

                            2 -> SessionTabs(
                                state = fp3State,
                                isRefreshing = isRefreshing,
                                loadingMessage = if (isSprintWeekend) loadingSprintRace else loadingFp,
                                isConnected = isConnected,
                                onShareClick = { shareSessionData = tabs[2] to it }
                            )

                            3 -> SessionTabs(
                                state = qualifyState,
                                isRefreshing = isRefreshing,
                                loadingMessage = loadingQualy,
                                isConnected = isConnected,
                                onShareClick = { shareSessionData = tabs[3] to it }
                            )

                            4 -> SessionTabs(
                                state = resultState,
                                isRefreshing = isRefreshing,
                                loadingMessage = loadingRace,
                                isConnected = isConnected,
                                onShareClick = { shareSessionData = tabs[4] to it }
                            )
                        }
                    }
                }
            }

            if (canShare) {
                ExtendedFloatingActionButton(
                    onClick = {
                        shareSessionData = tabs[pagerState.currentPage] to currentResults
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.share),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 20.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    shareSessionData?.let { (sessionTitle, sessionData) ->
        ShareResultBottomSheet(
            sessionName = sessionTitle,
            data = sessionData,
            onDismiss = { shareSessionData = null }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SessionTabs(
    state: UiState<SessionResultUiState>,
    isRefreshing: Boolean,
    loadingMessage: String,
    isConnected: Boolean,
    onShareClick: (SessionResultUiState) -> Unit,
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
                                contentDescription = stringResource(R.string.no_internet_connection),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.no_internet_connection),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.something_went_wrong),
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
                        item(contentType = "Header") {
                            SessionHeader(
                                header = data.header,
                                onShareClick = { onShareClick(data) }
                            )
                        }
                    }
                    items(
                        data.results,
                        key = { "${it.position}_${it.driver}_${it.number}_${it.team}" },
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
private fun SessionHeader(
    header: ResultHeader,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
        Spacer(modifier = Modifier.height(10.dp))
        FilledTonalButton(
            onClick = onShareClick,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.share),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.results_share_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareResultBottomSheet(
    sessionName: String,
    data: SessionResultUiState,
    onDismiss: () -> Unit
) {
    @Suppress("DEPRECATION")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedLimit by remember { mutableStateOf(3) } // 3 = Podium, 10 = Top 10, -1 = All

    val raceName = data.header?.raceName ?: "Formula 1 Grand Prix"
    val circuitName = data.header?.circuitName ?: ""
    val results = data.results

    val formattedSummary = remember(raceName, sessionName, results, selectedLimit) {
        RaceResultShareHelper.generateRaceResultSummary(
            raceName = raceName,
            sessionName = sessionName,
            results = results,
            limit = selectedLimit
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.results_share_sheet_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.results_share_sheet_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Limit Selector Chips
            Text(
                text = stringResource(R.string.results_summary_format),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLimit == 3,
                    onClick = { selectedLimit = 3 },
                    label = { Text(stringResource(R.string.results_limit_podium)) },
                    shape = RoundedCornerShape(999.dp)
                )

                if (results.size >= 10) {
                    FilterChip(
                        selected = selectedLimit == 10,
                        onClick = { selectedLimit = 10 },
                        label = { Text(stringResource(R.string.results_limit_top10)) },
                        shape = RoundedCornerShape(999.dp)
                    )
                }

                FilterChip(
                    selected = selectedLimit == -1,
                    onClick = { selectedLimit = -1 },
                    label = { Text(stringResource(R.string.results_limit_all, results.size)) },
                    shape = RoundedCornerShape(999.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formatted Text Preview Card
            Text(
                text = stringResource(R.string.results_preview),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = formattedSummary,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Share Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Copy Button
                FilledTonalButton(
                    onClick = {
                        RaceResultShareHelper.copyToClipboard(context, formattedSummary)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.copy),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.results_copy_text),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Button (Native Share Sheet)
                Button(
                    onClick = {
                        RaceResultShareHelper.shareRaceResultText(
                            context = context,
                            summaryText = formattedSummary,
                            subject = "$raceName $sessionName"
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = stringResource(R.string.share),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.results_share_text),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Share Graphic Card (Image)
            OutlinedButton(
                onClick = {
                    val bitmap = RaceResultShareHelper.generateRaceResultCardBitmap(
                        raceName = raceName,
                        sessionName = sessionName,
                        circuitName = circuitName,
                        results = results
                    )
                    RaceResultShareHelper.shareRaceResultImage(
                        context = context,
                        bitmap = bitmap,
                        title = "$raceName - $sessionName Card"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(999.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Image,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.results_share_graphic_card),
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
                        model = data.headshotUrl ?: ApiConstants.FALLBACK_DRIVER_IMAGE_URL,
                        error = rememberAsyncImagePainter(
                            model = ApiConstants.FALLBACK_DRIVER_IMAGE_URL
                        ),
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