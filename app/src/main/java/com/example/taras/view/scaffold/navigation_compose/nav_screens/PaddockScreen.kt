package com.example.taras.view.scaffold.navigation_compose.nav_screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import com.example.taras.network_calls.rss.NewsSource
import com.example.taras.network_calls.rss.NewsSources
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.core.helpercore.toComposeColor
import com.example.taras.view.subview.NewsCarousel
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.DriversViewModelFactory
import com.example.taras.viewmodel.NewsViewModel
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.RacesViewModelFactory
import com.example.taras.core.common.UserPreferences
import com.example.taras.viewmodel.SessionInfo
import com.example.taras.viewmodel.TeamUiModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.UserViewModel
import com.example.taras.viewmodel.UserViewModelFactory
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.core.notification.NotificationScheduler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import androidx.core.net.toUri
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlinx.coroutines.delay
import com.example.taras.core.helpercore.formatCountdown


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun NavPaddockScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(UserPreferences(LocalContext.current))
    ),
    driversViewModel: DriversViewModel = viewModel(
        factory = DriversViewModelFactory(
            AppDatabase.getDatabase(LocalContext.current).topThreeDriversDao()
        )
    ),
    teamsViewModel: TeamsViewModel = viewModel(),
    newsViewModel: NewsViewModel = viewModel(),
    racesViewModel: RacesViewModel = viewModel(
        factory = RacesViewModelFactory(CurrentData(LocalContext.current))
    ),
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
        onDismissWelcome = { userViewModel.setHasSeenWelcome(true) },
        onDriverClick = onDriverClick,
        onTeamClick = onTeamClick,
        onUpdateDriverStats = { number, rank, points ->
            userViewModel.updateFavoriteDriverStats(number, rank, points)
        },
        isRefreshing = isDriversRefreshing || isTeamsRefreshing || isNewsRefreshing || isRacesRefreshing,
        onRefresh = {
            driversViewModel.fetchDriverData(isRefresh = true)
            teamsViewModel.fetchTeams(isRefresh = true)
            newsViewModel.fetchNews(isRefresh = true)
            racesViewModel.fetchRacesData(isRefresh = true)
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
    onDismissWelcome: () -> Unit = {},
    onDriverClick: (String) -> Unit = {},
    onTeamClick: (String) -> Unit = {},
    onUpdateDriverStats: (String, String, String) -> Unit = { _, _, _ -> },
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    newsSources: List<NewsSource> = NewsSources.FILTER_OPTIONS,
    selectedNewsSourceId: String = NewsSources.ALL.id,
    onSelectNewsSource: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val pullToRefreshState = rememberPullToRefreshState()

        val isAnyError = driverTopThree is UiState.Error ||
                newsState is UiState.Error ||
                raceCurrentState is UiState.Error ||
                allTeams is UiState.Error

        val isEssentialSuccess =
            driverTopThree is UiState.Success && raceCurrentState is UiState.Success
        val isEssentialLoading =
            driverTopThree is UiState.Loading || raceCurrentState is UiState.Loading

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
                        Text("Loading...")
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

                            LaunchedEffect(favDriver) {
                                if (favDriver != null && !favoriteDriverNumber.isNullOrEmpty()) {
                                    onUpdateDriverStats(
                                        favoriteDriverNumber,
                                        favDriver.rank.toString(),
                                        favDriver.points
                                    )
                                }
                            }

                            // 1. First-time Welcome Card: ONLY shown when hasSeenWelcome is false
                            if (hasSeenWelcome == false) {
                                DisposableEffect(Unit) {
                                    onDispose {
                                        onDismissWelcome()
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
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
                                                text = if (userName.isNotBlank() && userName != "Guest") "Welcome to Taras, $userName! 👋" else "Welcome to Taras! 👋",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            IconButton(
                                                onClick = onDismissWelcome,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Dismiss welcome message",
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Your home for live Formula 1 session countdowns, standings, and news. Tap ⭐ on any driver or team in the Grid to pin them here!",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            FilledTonalButton(
                                                onClick = onDismissWelcome,
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "Got it",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. Favorite Driver or Team Banner: Shown without welcome greeting
                            if (favDriver != null) {
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
                                            Text(
                                                text = "⭐ FAVORITE DRIVER",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${favDriver.fullName ?: favDriver.name} is P${favDriver.rank} with ${favDriver.points} pts",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "P${favDriver.rank}",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
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
                                        if (!favoriteTeam.isNullOrBlank()) {
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
                                                text = "🏎️ FAVORITE TEAM",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (team != null) "${team.teamName} is P${team.rank} with ${team.points} pts"
                                                       else favoriteTeam,
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

                        item {
                            val raceCurrent = (raceCurrentState as? UiState.Success)?.data

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
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
                                    NextSessionCountdownText(
                                        targetInstant = nextSessionInfo?.targetInstant,
                                        fallbackCountdown = nextSessionInfo?.countdown ?: "00:00:00"
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
                                text = "Latest from the Paddock....",
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
                    text = " PTS",
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Notifications enabled for race updates", Toast.LENGTH_SHORT).show()
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
                    Color(android.graphics.Color.parseColor(source.brandColorHex))
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