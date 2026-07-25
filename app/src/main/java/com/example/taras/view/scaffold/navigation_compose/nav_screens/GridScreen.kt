package com.example.taras.view.scaffold.navigation_compose.nav_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.UiState
import com.example.taras.view.subview.DriverCard
import com.example.taras.view.subview.TeamCard
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.TeamsViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NaveGridScreen() {

    val driversViewModel: DriversViewModel = viewModel()
    val driversState by driversViewModel.combinedDrivers.collectAsStateWithLifecycle()

    val teamsViewModel: TeamsViewModel = viewModel()
    val teamsState by teamsViewModel.combinedTeams.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Drivers", "Teams")

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()


    // Reset isRefreshing when data is loaded
    LaunchedEffect(driversState, teamsState) {
        if (driversState !is UiState.Loading && teamsState !is UiState.Loading) {
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            driversViewModel.fetchDriverData()
            teamsViewModel.fetchTeams()
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
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
                                                Text("loading......")
                                            }
                                        }
                                    }
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
                                    val drivers = (driversState as UiState.Success).data
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
                                            key = { it.driverNumber }
                                        ) { driver ->
                                            DriverCard(driver = driver)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
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
                                                Text("Set loading progress:")
                                            }
                                        }
                                    }
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

                                    if (teamsResponse.isEmpty()) {
                                        item {
                                            Text(
                                                "No Teams Found",
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    } else {
                                        items(teamsResponse) { teamData ->
                                            TeamCard(
                                                teamData = teamData
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
