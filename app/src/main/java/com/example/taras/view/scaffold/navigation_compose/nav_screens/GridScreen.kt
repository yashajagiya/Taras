package com.example.taras.view.scaffold.navigation_compose.nav_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.NetworkObserver
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.common.UiState
import com.example.taras.view.subview.DriverCard
import com.example.taras.view.subview.TeamCard
import com.example.taras.viewmodel.DriverUiModel
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.DriversViewModelFactory
import com.example.taras.viewmodel.TeamUiModel
import com.example.taras.viewmodel.TeamsViewModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun NavGridScreen(
    onDriverClick: (String) -> Unit,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    driversViewModel: DriversViewModel = viewModel(
        factory = DriversViewModelFactory(
            AppDatabase.getDatabase(LocalContext.current).topThreeDriversDao()
        )
    ),
    teamsViewModel: TeamsViewModel = viewModel()
) {
    val driversState by driversViewModel.combinedLowDrivers.collectAsStateWithLifecycle()
    val teamsState by teamsViewModel.combinedTeams.collectAsStateWithLifecycle()
    val isDriversRefreshing by driversViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isTeamsRefreshing by teamsViewModel.isRefreshing.collectAsStateWithLifecycle()

    GridContent(
        driversState = driversState,
        teamsState = teamsState,
        isRefreshing = isDriversRefreshing || isTeamsRefreshing,
        onRefresh = {
            driversViewModel.fetchDriverData(isRefresh = true)
            teamsViewModel.fetchTeams(isRefresh = true)
        },
        onDriverClick = onDriverClick,
        onTeamClick = onTeamClick,
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun GridContent(
    driversState: UiState<ImmutableList<DriverUiModel>>,
    teamsState: UiState<ImmutableList<TeamUiModel>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDriverClick: (String) -> Unit,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
        val tabs = listOf("Drivers", "Teams")

        val pullToRefreshState = rememberPullToRefreshState()

        val context = LocalContext.current
        val networkObserver = remember { NetworkObserver(context) }
        val isConnected by networkObserver.isConnected.collectAsStateWithLifecycle(initialValue = true)

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
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                when (driversState) {
                                    is UiState.Loading -> {
                                        if (!isRefreshing) {
                                            item {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(32.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    LoadingIndicator()
                                                    Spacer(Modifier.requiredHeight(30.dp))
                                                    Text("Loading drivers...")
                                                }
                                            }
                                        }
                                    }

                                    is UiState.Error -> {
                                        if (!isConnected) {
                                            item(contentType = "Error") {
                                                Column (
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally) {
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
                                        val drivers = driversState.data
                                        if (drivers.isEmpty()) {
                                            item {
                                                Text(
                                                    "No Drivers Found",
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                        } else {
                                            items(
                                                items = drivers,
                                                key = { it.driverNumber ?: it.name }
                                            ) { driver ->
                                                DriverCard(
                                                    driver = driver,
                                                    onDriverClick,
                                                    modifier = Modifier
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                when (teamsState) {
                                    is UiState.Loading -> {
                                        if (!isRefreshing) {
                                            item {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(32.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    LoadingIndicator()
                                                    Spacer(Modifier.requiredHeight(30.dp))
                                                    Text("Loading teams...")
                                                }
                                            }
                                        }
                                    }

                                    is UiState.Error -> {
                                        item {
                                            Text(
                                                text = "Error: ${teamsState.message}",
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }

                                    is UiState.Success -> {
                                        val teamsResponse = teamsState.data
                                        if (teamsResponse.isEmpty()) {
                                            item {
                                                Text(
                                                    "No Teams Found",
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                        } else {
                                            items(
                                                items = teamsResponse,
                                                key = { it.teamName }
                                            ) { teamData ->
                                                TeamCard(
                                                    teamData = teamData,
                                                    onTeamClick = onTeamClick,
                                                    modifier = Modifier
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
        }
    }
}