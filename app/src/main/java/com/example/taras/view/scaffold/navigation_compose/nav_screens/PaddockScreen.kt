package com.example.taras.view.scaffold.navigation_compose.nav_screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.taras.R
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.RefreshHapticEffect
import com.example.taras.core.helpercore.formatCountdown
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.core.notification.NotificationScheduler
import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.live.LiveConnectionState
import com.example.taras.network_calls.rss.NewsSource
import com.example.taras.network_calls.rss.NewsSources
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TrackStatus
import com.example.taras.view.subview.AudioPlayerBar
import com.example.taras.view.subview.FullScreenImageDialog
import com.example.taras.view.subview.FullScreenLiveTimelineDialog
import com.example.taras.view.subview.NewsCarousel
import com.example.taras.view.subview.upgradeF1LiveImageUrl
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.NewsViewModel
import com.example.taras.viewmodel.PaddockViewModel
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.SessionInfo
import com.example.taras.viewmodel.TeamUiModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.UserViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay

@Composable
fun NavPaddockScreen(
    modifier: Modifier = Modifier,
    paddockViewModel: PaddockViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    driversViewModel: DriversViewModel = viewModel(),
    teamsViewModel: TeamsViewModel = viewModel(),
    newsViewModel: NewsViewModel = viewModel(),
    racesViewModel: RacesViewModel = viewModel(),
    onDriverClick: (String) -> Unit = {},
    onTeamClick: (String) -> Unit = {}
) {
    val driverTopThree by driversViewModel.topThree.collectAsStateWithLifecycle()
    val allDrivers by driversViewModel.combinedLowDrivers.collectAsStateWithLifecycle()
    val allTeams by teamsViewModel.combinedTeams.collectAsStateWithLifecycle()
    val newsState by newsViewModel.news.collectAsStateWithLifecycle()
    val raceCurrentState by racesViewModel.oneRace.collectAsStateWithLifecycle()
    val nextSessionInfo by racesViewModel.nextSessionInfo.collectAsStateWithLifecycle()

    val userName by userViewModel.userName.collectAsStateWithLifecycle()
    val favoriteDriverNumber by userViewModel.favoriteDriverNumber.collectAsStateWithLifecycle()
    val favoriteTeam by userViewModel.favoriteTeam.collectAsStateWithLifecycle()
    val hasSeenWelcome by userViewModel.hasSeenWelcome.collectAsStateWithLifecycle()

    val isDriversRefreshing by driversViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isTeamsRefreshing by teamsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isNewsRefreshing by newsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isRacesRefreshing by racesViewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedSourceId by newsViewModel.selectedSourceId.collectAsStateWithLifecycle()

    val isLiveSession by paddockViewModel.isLiveSession.collectAsStateWithLifecycle()
    val isLiveTimingActive by paddockViewModel.isLiveTimingActive.collectAsStateWithLifecycle()
    val isLiveTimelineActive by paddockViewModel.isLiveTimelineActive.collectAsStateWithLifecycle()
    val timelineConnectionState by paddockViewModel.timelineConnectionState.collectAsStateWithLifecycle()
    val liveDiscovery by paddockViewModel.liveDiscovery.collectAsStateWithLifecycle()
    val liveOverview by paddockViewModel.liveOverview.collectAsStateWithLifecycle()
    val liveTimelineItems by paddockViewModel.liveTimelineItems.collectAsStateWithLifecycle()
    val liveTrackStatus by paddockViewModel.liveTrackStatus.collectAsStateWithLifecycle()
    val liveSnapshot by paddockViewModel.liveSnapshot.collectAsStateWithLifecycle()
    val driverTyres by paddockViewModel.driverTyres.collectAsStateWithLifecycle()
    val qualifyingPositionsMap by paddockViewModel.qualifyingPositionsMap.collectAsStateWithLifecycle()

    StartNofi()

    PaddockContent(
        modifier = modifier,
        driverTopThree = driverTopThree,
        allDrivers = allDrivers,
        allTeams = allTeams,
        newsState = newsState,
        raceCurrentState = raceCurrentState,
        nextSessionInfo = nextSessionInfo,
        userName = userName,
        favoriteDriverNumber = favoriteDriverNumber,
        favoriteTeam = favoriteTeam,
        hasSeenWelcome = hasSeenWelcome,
        isLiveSession = isLiveSession,
        isLiveTimingActive = isLiveTimingActive,
        isLiveTimelineActive = isLiveTimelineActive,
        timelineConnectionState = timelineConnectionState,
        liveTrackStatus = liveTrackStatus,
        liveDiscovery = liveDiscovery,
        liveOverview = liveOverview,
        liveTimelineItems = liveTimelineItems,
        liveSnapshot = liveSnapshot,
        driverTyres = driverTyres,
        qualifyingPositionsMap = qualifyingPositionsMap,
        onDismissWelcome = { userViewModel.setHasSeenWelcome(true) },
        onDriverClick = onDriverClick,
        onTeamClick = onTeamClick,
        onUpdateDriverStats = { number, rank, points, team ->
            userViewModel.updateFavoriteDriverStats(number, rank, points, team)
        },
        isRefreshing = isDriversRefreshing || isTeamsRefreshing || isNewsRefreshing || isRacesRefreshing,
        onRefresh = {
            driversViewModel.fetchDriverData(isRefresh = true)
            teamsViewModel.fetchTeams(isRefresh = true)
            newsViewModel.fetchNews(isRefresh = true)
            racesViewModel.fetchRacesData(isRefresh = true)
            paddockViewModel.refreshLiveFeed()
            paddockViewModel.loadQualifyingPositions()
        },
        newsSources = newsViewModel.availableSources,
        selectedNewsSourceId = selectedSourceId,
        onSelectNewsSource = { newsViewModel.setSourceFilter(it) }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun PaddockContent(
    driverTopThree: UiState<ImmutableList<DriverUiModel>>,
    allDrivers: UiState<ImmutableList<DriverUiModel>>,
    allTeams: UiState<ImmutableList<TeamUiModel>>,
    newsState: UiState<ImmutableList<RssItem>>,
    raceCurrentState: UiState<CurrentRace?>,
    nextSessionInfo: SessionInfo?,
    userName: String = "Guest",
    favoriteDriverNumber: String? = null,
    favoriteTeam: String? = null,
    hasSeenWelcome: Boolean? = true,
    isLiveSession: Boolean = false,
    isLiveTimingActive: Boolean = false,
    isLiveTimelineActive: Boolean = false,
    timelineConnectionState: LiveConnectionState = LiveConnectionState.DISCONNECTED,
    liveTrackStatus: TrackStatus = TrackStatus.GREEN,
    liveDiscovery: LiveDiscoveryResponse? = null,
    liveOverview: TimelineOverview? = null,
    liveTimelineItems: ImmutableList<TimelineItem> = persistentListOf(),
    liveSnapshot: RaceSnapshot? = null,
    driverTyres: Map<String, String>? = null,
    qualifyingPositionsMap: Map<String, Int> = emptyMap(),
    onDismissWelcome: () -> Unit = {},
    onDriverClick: (String) -> Unit = {},
    onTeamClick: (String) -> Unit = {},
    onUpdateDriverStats: (String, String, String, String?) -> Unit = { _, _, _, _ -> },
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    newsSources: List<NewsSource> = NewsSources.FILTER_OPTIONS,
    selectedNewsSourceId: String = NewsSources.ALL.id,
    onSelectNewsSource: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val pullToRefreshState = rememberPullToRefreshState()
        val liveBaseUrl = liveDiscovery?.let { disc ->
            disc.webUrl.ifBlank { disc.timelineRestUrl.substringBefore("/api/") }
        }
        var showFullScreenTimeline by remember { mutableStateOf(false) }

        val isAnyError = driverTopThree is UiState.Error ||
                newsState is UiState.Error ||
                raceCurrentState is UiState.Error ||
                allTeams is UiState.Error

        val isEssentialSuccess =
            driverTopThree is UiState.Success && raceCurrentState is UiState.Success
        val isEssentialLoading =
            driverTopThree is UiState.Loading || raceCurrentState is UiState.Loading

        RefreshHapticEffect(isRefreshing = isRefreshing, state = pullToRefreshState)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            indicator = {
                if (!(isEssentialLoading || isRefreshing)) {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = false,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {

                if (isEssentialLoading || isRefreshing) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingIndicator()
                        Spacer(Modifier.requiredHeight(30.dp))
                        Text(stringResource(R.string.loading))
                    }
                } else if (isAnyError && !isEssentialSuccess) {
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            val favDriver = remember(allDrivers, favoriteDriverNumber) {
                                if (!favoriteDriverNumber.isNullOrEmpty() && allDrivers is UiState.Success) {
                                    allDrivers.data.find { it.driverNumber?.toString() == favoriteDriverNumber }
                                } else null
                            }

                            LaunchedEffect(favDriver, onUpdateDriverStats) {
                                if (favDriver != null && !favoriteDriverNumber.isNullOrEmpty()) {
                                    onUpdateDriverStats(
                                        favoriteDriverNumber,
                                        favDriver.rank.toString(),
                                        favDriver.points,
                                        favDriver.teamName
                                    )
                                }
                            }

                            // 1. Favorite Driver or Team Banner: Shown without welcome greeting
                            if (favDriver != null) {
                                val isWebSocketOn = timelineConnectionState == LiveConnectionState.CONNECTED
                                val isLiveSessionActive = isLiveSession && (liveDiscovery?.isLive == true)
                                val liveFavDriver = if (isWebSocketOn && isLiveSessionActive) {
                                    liveSnapshot?.drivers?.find { d ->
                                        (favDriver.driverNumber != null && d.fullName.contains(favDriver.name, ignoreCase = true)) ||
                                        d.tla.equals(favDriver.name.take(3), ignoreCase = true) ||
                                        d.fullName.contains(favDriver.name, ignoreCase = true) ||
                                        (favDriver.fullName?.isNotBlank() == true && d.fullName.contains(favDriver.fullName, ignoreCase = true))
                                    }
                                } else null

                                val isFinished = liveTrackStatus == TrackStatus.FINISHED
                                val livePos = liveFavDriver?.position
                                val gridPos = if (liveFavDriver != null) {
                                    qualifyingPositionsMap[favDriver.driverNumber?.toString() ?: ""]
                                        ?: qualifyingPositionsMap[liveFavDriver.tla.lowercase()]
                                        ?: qualifyingPositionsMap[favDriver.name.lowercase()]
                                        ?: qualifyingPositionsMap[favDriver.fullName?.lowercase() ?: ""]
                                } else null
                                val posDelta = if (livePos != null && gridPos != null) gridPos - livePos else null

                                Card(
                                    onClick = {
                                        val driverNum = favDriver.driverNumber?.toString() ?: favoriteDriverNumber
                                        if (!driverNum.isNullOrEmpty()) {
                                            onDriverClick(driverNum)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            val badgeText = when {
                                                liveFavDriver != null && isFinished -> "CHEQUERED FLAG"
                                                liveFavDriver != null && liveTrackStatus != TrackStatus.GREEN -> when (liveTrackStatus) {
                                                    TrackStatus.SAFETY_CAR -> "SAFETY CAR"
                                                    TrackStatus.VIRTUAL_SAFETY_CAR -> "VSC"
                                                    TrackStatus.RED_FLAG -> "RED FLAG"
                                                    TrackStatus.YELLOW -> "YELLOW FLAG"
                                                    else -> "RACING LIVE"
                                                }
                                                liveFavDriver != null -> "RACING LIVE"
                                                else -> stringResource(R.string.paddock_fav_driver_badge)
                                            }

                                            val badgeColor = when {
                                                liveFavDriver != null && isFinished -> Color(0xFF37474F)
                                                liveFavDriver != null && (liveTrackStatus == TrackStatus.SAFETY_CAR || liveTrackStatus == TrackStatus.VIRTUAL_SAFETY_CAR) -> Color(0xFFFF9800)
                                                liveFavDriver != null && liveTrackStatus == TrackStatus.RED_FLAG -> Color(0xFFE53935)
                                                liveFavDriver != null && liveTrackStatus == TrackStatus.YELLOW -> Color(0xFFFFEB3B)
                                                liveFavDriver != null -> Color(0xFFE53935)
                                                else -> MaterialTheme.colorScheme.primary
                                            }

                                            Text(
                                                text = badgeText,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeColor
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))

                                            val descText = when {
                                                liveFavDriver != null && isFinished && gridPos != null && posDelta != null -> {
                                                    val deltaStr = when {
                                                        posDelta > 0 -> "+$posDelta places gained"
                                                        posDelta < 0 -> "${kotlin.math.abs(posDelta)} places lost"
                                                        else -> "held grid position"
                                                    }
                                                    "${favDriver.fullName ?: favDriver.name} • Finished P$livePos (Started P$gridPos, $deltaStr)"
                                                }
                                                liveFavDriver != null && gridPos != null && posDelta != null -> {
                                                    val deltaStr = if (posDelta > 0) "+$posDelta" else if (posDelta < 0) "$posDelta" else "="
                                                    "${favDriver.fullName ?: favDriver.name} • P$livePos (Started P$gridPos • $deltaStr)"
                                                }
                                                liveFavDriver != null -> {
                                                    "${favDriver.fullName ?: favDriver.name} • P$livePos in Race"
                                                }
                                                else -> {
                                                    stringResource(
                                                        R.string.paddock_fav_driver_desc,
                                                        favDriver.fullName ?: favDriver.name,
                                                        favDriver.rank.toString(),
                                                        favDriver.points
                                                    )
                                                }
                                            }

                                            Text(
                                                text = descText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        val pillText = when {
                                            liveFavDriver != null && posDelta != null -> {
                                                if (posDelta > 0) "P$livePos ▲+$posDelta"
                                                else if (posDelta < 0) "P$livePos ▼$posDelta"
                                                else "P$livePos"
                                            }
                                            liveFavDriver != null -> "P$livePos"
                                            else -> "P${favDriver.rank}"
                                        }

                                        val pillBgColor = when {
                                            liveFavDriver != null && posDelta != null && posDelta > 0 -> Color(0xFF2E7D32)
                                            liveFavDriver != null && posDelta != null && posDelta < 0 -> Color(0xFFC62828)
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }

                                        val pillFgColor = when {
                                            liveFavDriver != null && posDelta != null && posDelta != 0 -> Color.White
                                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = pillBgColor
                                        ) {
                                            Text(
                                                text = pillText,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = pillFgColor
                                            )
                                        }
                                    }
                                }
                            } else if (!favoriteTeam.isNullOrBlank()) {
                                val team = (allTeams as? UiState.Success)?.data?.find {
                                    it.teamName.equals(favoriteTeam, ignoreCase = true)
                                }
                                Card(
                                    onClick = {
                                        if (favoriteTeam.isNotBlank()) {
                                            onTeamClick(favoriteTeam)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(R.string.paddock_fav_team_badge),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (team != null) stringResource(
                                                    R.string.paddock_fav_team_desc,
                                                    team.teamName,
                                                    team.rank.toString(),
                                                    team.points
                                                ) else favoriteTeam,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (team != null) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "P${team.rank}",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val raceCurrent = (raceCurrentState as? UiState.Success)?.data
                        val isWebSocketOn = timelineConnectionState == LiveConnectionState.CONNECTED
                        val isLiveSessionActive = isLiveSession && (liveDiscovery?.isLive == true)
                        val hasLiveWebhookData = isWebSocketOn && isLiveSessionActive && liveTimelineItems.isNotEmpty()

                        val latestWebhookItem = liveTimelineItems.firstOrNull()

                        if (hasLiveWebhookData && latestWebhookItem != null) {
                            item {
                                val sessionPagerState = rememberPagerState(pageCount = { 2 })
                                val density = LocalDensity.current
                                var nextSessionCardHeightDp by remember { mutableStateOf(210.dp) }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 12.dp)
                                ) {
                                    HorizontalPager(
                                        state = sessionPagerState,
                                        modifier = Modifier.fillMaxWidth(),
                                        pageSpacing = 12.dp,
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        verticalAlignment = Alignment.Top,
                                        beyondViewportPageCount = 1
                                    ) { page ->
                                        if (page == 0) {
                                            LiveSessionCard(
                                                latestWebhookItem = latestWebhookItem,
                                                liveDiscovery = liveDiscovery,
                                                liveTrackStatus = liveTrackStatus,
                                                liveBaseUrl = liveBaseUrl,
                                                liveSnapshot = liveSnapshot,
                                                qualifyingPositionsMap = qualifyingPositionsMap,
                                                minHeight = nextSessionCardHeightDp,
                                                onOpenFullScreen = { showFullScreenTimeline = true }
                                            )
                                        } else {
                                            NextSessionCard(
                                                nextSessionInfo = nextSessionInfo,
                                                raceCurrent = raceCurrent,
                                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                                    val measuredDp = with(density) { coordinates.size.height.toDp() }
                                                    if (measuredDp > 0.dp && kotlin.math.abs(nextSessionCardHeightDp.value - measuredDp.value) > 1f) {
                                                        nextSessionCardHeightDp = measuredDp
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    // Pager Indicator Dots
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(2) { index ->
                                            val isSelected = sessionPagerState.currentPage == index
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .size(if (isSelected) 8.dp else 6.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // NEXT SESSION CARD (Standard standalone full-width)
                            item {
                                NextSessionCard(
                                    nextSessionInfo = nextSessionInfo,
                                    raceCurrent = raceCurrent,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 8.dp,
                                        bottom = 12.dp
                                    )
                                )
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
                                    onClick = {
                                        p1Driver?.driverNumber?.let { onDriverClick(it.toString()) }
                                    },
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
                                                text = stringResource(R.string.paddock_championship),
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
                                                ?: ApiConstants.FALLBACK_DRIVER_IMAGE_URL,
                                            error = rememberAsyncImagePainter(
                                                model = ApiConstants.FALLBACK_DRIVER_IMAGE_URL
                                            ),
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
                                                text = " " + stringResource(R.string.points_pts),
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
                                    RunnerUpDriverCard(driver = p2Driver, positionLabel = "P2", onDriverClick = onDriverClick)
                                    RunnerUpDriverCard(driver = p3Driver, positionLabel = "P3", onDriverClick = onDriverClick)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp
                                ),
                                text = stringResource(R.string.paddock_latest_news_title),
                                color = Color.Black,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                        }

                        item {
                            NewsSourceFilterRow(
                                sources = newsSources,
                                selectedSourceId = selectedNewsSourceId,
                                onSelectSource = onSelectNewsSource,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        item {
                            NewsCarousel(
                                newsState = newsState,
                                modifier = Modifier.padding(vertical = 8.dp),
                                drivers = (allDrivers as? UiState.Success)?.data
                                    ?: persistentListOf(),
                                teams = (allTeams as? UiState.Success)?.data
                                    ?: persistentListOf()
                            )
                        }
                    }
                }
            }
        }

        if (showFullScreenTimeline) {
            FullScreenLiveTimelineDialog(
                items = liveTimelineItems,
                baseUrl = liveBaseUrl,
                grandPrixName = formatShortGpName(liveDiscovery?.race ?: liveDiscovery?.grandPrix ?: (nextSessionInfo?.raceName ?: (raceCurrentState as? UiState.Success)?.data?.raceName)),
                sessionName = liveDiscovery?.session ?: nextSessionInfo?.sessionName,
                trackStatus = liveTrackStatus.name,
                leader = liveDiscovery?.leader,
                driverTyres = driverTyres,
                onDismiss = { showFullScreenTimeline = false }
            )
        }
    }
}

@Composable
private fun LiveSessionCard(
    latestWebhookItem: TimelineItem,
    liveDiscovery: LiveDiscoveryResponse?,
    liveTrackStatus: TrackStatus,
    liveBaseUrl: String?,
    onOpenFullScreen: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 210.dp,
    liveSnapshot: RaceSnapshot? = null,
    qualifyingPositionsMap: Map<String, Int> = emptyMap()
) {
    var showLiveImageLightbox by remember { mutableStateOf(false) }

    val (trackText, trackBg, trackFg) = when (liveTrackStatus) {
        TrackStatus.SAFETY_CAR -> Triple("SAFETY CAR", Color(0xFFFF9800), Color.White)
        TrackStatus.VIRTUAL_SAFETY_CAR -> Triple("VSC", Color(0xFFFF9800), Color.White)
        TrackStatus.RED_FLAG -> Triple("RED FLAG", Color(0xFFE53935), Color.White)
        TrackStatus.YELLOW -> Triple("YELLOW FLAG", Color(0xFFFFEB3B), Color.Black)
        TrackStatus.FINISHED -> Triple("CHEQUERED FLAG", Color(0xFF37474F), Color.White)
        TrackStatus.GREEN -> Triple("TRACK CLEAR", Color(0xFF4CAF50), Color.White)
    }

    val liveGrandPrixTitle = formatShortGpName(liveDiscovery?.race ?: liveDiscovery?.grandPrix)
    val sessionRaw = liveDiscovery?.session ?: "Live Session"
    val lapDisplay = remember(sessionRaw, latestWebhookItem.lap) {
        extractLapDisplay(sessionRaw, latestWebhookItem.lap)
    }

    val isRaceSession = remember(sessionRaw, lapDisplay, liveSnapshot?.session) {
        val s = (liveSnapshot?.session ?: sessionRaw).lowercase()
        val isExplicitQualyOrPractice = s.contains("qualifying") || s.contains("qualy") ||
            s.contains("practice") || s.contains("fp1") || s.contains("fp2") ||
            s.contains("fp3") || s.contains("shootout") || s.contains("sq")
        !isExplicitQualyOrPractice && (s.contains("race") || (lapDisplay != null && lapDisplay.contains("/")))
    }

    Card(
        onClick = onOpenFullScreen,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = minHeight)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Row: RACING LIVE on left, LAP (and track status if caution) on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRaceSession) "RACE • RACING LIVE" else "RACING LIVE",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (liveTrackStatus != TrackStatus.GREEN) {
                            Text(
                                text = trackText,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(trackBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                color = trackFg,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        if (lapDisplay != null) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = lapDisplay,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = liveGrandPrixTitle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Latest data from the webhook (no timestamp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!latestWebhookItem.title.isNullOrBlank()) {
                        Text(
                            text = latestWebhookItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (latestWebhookItem.text.isNotBlank()) {
                        Text(
                            text = latestWebhookItem.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }

                    if (latestWebhookItem.isQuote && !latestWebhookItem.author.isNullOrBlank()) {
                        Text(
                            text = "— ${latestWebhookItem.author}${if (!latestWebhookItem.authorRole.isNullOrBlank()) ", ${latestWebhookItem.authorRole}" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // In-card playable audio for team radio
                    if (latestWebhookItem.isRadio && !latestWebhookItem.audioUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AudioPlayerBar(
                            itemId = latestWebhookItem.id,
                            audioUrl = latestWebhookItem.audioUrl,
                            baseUrl = liveBaseUrl
                        )
                    }

                    // In-card image preview with tap-to-expand lightbox
                    if (latestWebhookItem.hasImage && !latestWebhookItem.imageUrl.isNullOrBlank()) {
                        val thumbUrl = remember(latestWebhookItem.imageUrl, liveBaseUrl) {
                            upgradeF1LiveImageUrl(latestWebhookItem.imageUrl, liveBaseUrl, isThumbnail = true)
                        }
                        val masterUrl = remember(latestWebhookItem.imageUrl, liveBaseUrl) {
                            upgradeF1LiveImageUrl(latestWebhookItem.imageUrl, liveBaseUrl, isThumbnail = false)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { showLiveImageLightbox = true }
                        ) {
                            AsyncImage(
                                model = thumbUrl,
                                contentDescription = latestWebhookItem.title ?: "F1 Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (showLiveImageLightbox && (masterUrl != null || thumbUrl != null)) {
                            FullScreenImageDialog(
                                imageUrl = masterUrl ?: thumbUrl!!,
                                title = latestWebhookItem.title ?: liveGrandPrixTitle,
                                subtitle = lapDisplay,
                                onDismiss = { showLiveImageLightbox = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom meta row: session + leader / P1 on left, Tap here on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leaderTla = liveSnapshot?.drivers?.firstOrNull()?.tla ?: liveDiscovery?.leader
                val leaderGrid = if (!leaderTla.isNullOrBlank()) qualifyingPositionsMap[leaderTla.lowercase()] else null
                val leaderDelta = if (leaderGrid != null) leaderGrid - 1 else null

                val leaderLabel = if (!leaderTla.isNullOrBlank()) {
                    if (isRaceSession) {
                        if (liveTrackStatus == TrackStatus.FINISHED) {
                            val deltaStr = if (leaderDelta != null) (if (leaderDelta > 0) " (+$leaderDelta)" else if (leaderDelta == 0) " (Pole)" else "") else ""
                            " • WINNER $leaderTla$deltaStr"
                        } else {
                            val deltaStr = if (leaderDelta != null && leaderDelta > 0) " (+$leaderDelta)" else ""
                            " • LEADER $leaderTla$deltaStr"
                        }
                    } else {
                        " • P1 $leaderTla"
                    }
                } else ""
                val sessionDisplayName = if (isRaceSession) "RACE" else sessionRaw.substringBefore(" - ").uppercase()

                Text(
                    text = sessionDisplayName + leaderLabel,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap here ↗",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun NextSessionCard(
    nextSessionInfo: SessionInfo?,
    raceCurrent: CurrentRace?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 210.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 210.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.paddock_next_session),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = stringResource(R.string.live_badge),
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
                        ?.takeIf { it.isNotBlank() } ?: stringResource(R.string.paddock_upcoming_race),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                NextSessionCountdownText(
                    targetInstant = nextSessionInfo?.targetInstant,
                    fallbackCountdown = nextSessionInfo?.countdown ?: "00:00:00"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (nextSessionInfo != null) {
                    stringResource(
                        R.string.paddock_until_session,
                        nextSessionInfo.sessionName,
                        nextSessionInfo.circuitName
                    )
                } else {
                    stringResource(
                        R.string.paddock_no_upcoming_sessions,
                        raceCurrent?.circuitName ?: "N/A"
                    )
                },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatShortGpName(raceName: String?): String {
    if (raceName.isNullOrBlank()) return "Formula 1 GP"
    var name = raceName
        .replace(Regex("""(?i)\bformula\s*(1|one)\b"""), "")
        .replace(Regex("""(?i)\b20\d\d\b"""), "")
        .trim()
    if (name.contains("Grand Prix", ignoreCase = true)) {
        name = name.replace(Regex("""(?i)\bgrand\s+prix\b"""), "GP").trim()
    } else if (!name.endsWith("GP", ignoreCase = true)) {
        name = "$name GP"
    }
    val words = name.split(Regex("""\s+"""))
    val gpIdx = words.indexOfFirst { it.equals("GP", ignoreCase = true) }
    if (gpIdx > 1) {
        val targetWord = words[gpIdx - 1]
        val firstMatch = words.indexOfFirst { it.equals(targetWord, ignoreCase = true) }
        if (firstMatch in 0 until (gpIdx - 1)) {
            name = words.subList(gpIdx - 1, words.size).joinToString(" ")
        }
    }
    return name.replace(Regex("""\s+"""), " ").trim()
}

private fun extractLapDisplay(session: String?, itemLap: String?): String? {
    val sessionStr = session ?: ""
    val fractionMatch = Regex("""(\d+/\d+)""").find(sessionStr)
    return when {
        fractionMatch != null -> "LAP ${fractionMatch.value}"
        !itemLap.isNullOrBlank() -> if (itemLap.contains("/")) "LAP $itemLap" else "LAP $itemLap"
        sessionStr.contains("Lap", ignoreCase = true) -> {
            val numMatch = Regex("""Lap\s+(\d+)""", RegexOption.IGNORE_CASE).find(sessionStr)
            if (numMatch != null) "LAP ${numMatch.groupValues[1]}" else null
        }
        else -> null
    }
}

//P2 / P3 Drivers
@Composable
private fun RunnerUpDriverCard(
    driver: DriverUiModel?,
    positionLabel: String,
    onDriverClick: (String) -> Unit = {}
) {
    Card(
        onClick = {
            driver?.driverNumber?.let { onDriverClick(it.toString()) }
        },
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
                    text = " " + stringResource(R.string.points_pts),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }
        }
    }
}
@Composable
fun StartNofi() {
    val context = LocalContext.current
    val toastMessage = stringResource(R.string.paddock_notifications_toast)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
        NotificationScheduler.scheduleNotificationSync(context)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                permissionLauncher.launch(permission)
            } else {
                NotificationScheduler.scheduleNotificationSync(context)
            }
        } else {
            NotificationScheduler.scheduleNotificationSync(context)
        }
    }
}

@Composable
fun NextSessionCountdownText(
    targetInstant: Instant?,
    fallbackCountdown: String,
    modifier: Modifier = Modifier
) {
    var countdown by remember(targetInstant) {
        mutableStateOf(
            if (targetInstant != null) {
                formatCountdown(targetInstant - Clock.System.now())
            } else {
                fallbackCountdown
            }
        )
    }

    LaunchedEffect(targetInstant) {
        if (targetInstant == null) return@LaunchedEffect
        while (true) {
            val duration = targetInstant - Clock.System.now()
            countdown = formatCountdown(duration)
            delay(1000.milliseconds)
        }
    }

    Text(
        text = countdown,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun NewsSourceFilterRow(
    sources: List<NewsSource>,
    selectedSourceId: String,
    onSelectSource: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sources, key = { it.id }) { source ->
            val isSelected = source.id == selectedSourceId
            val brandColor = remember(source.brandColorHex) {
                try {
                    Color(source.brandColorHex.toColorInt())
                } catch (_: Exception) {
                    Color(0xFFE10600)
                }
            }

            Surface(
                onClick = { onSelectSource(source.id) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                border = if (isSelected) {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (source.id != NewsSources.ALL.id) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(brandColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = source.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
