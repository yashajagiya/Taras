package com.example.taras.view.scaffold.navigation_compose.nav_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.view.subview.RacesCards
import com.example.taras.viewmodel.CurrentRound
import com.example.taras.viewmodel.RaceClearData
import com.example.taras.viewmodel.RacesViewModel
import com.example.taras.viewmodel.RacesViewModelFactory
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavCalendarScreen(
    onCircuitClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    racesViewModel: RacesViewModel = viewModel(
        factory = RacesViewModelFactory(CurrentData(LocalContext.current))
    )
) {
    val racesState by racesViewModel.combinedRaces.collectAsStateWithLifecycle()
    val racesCurrentState by racesViewModel.upcomingRoundInfo.collectAsStateWithLifecycle()

    CalanderComposble(
        racesState,
        racesCurrentState,
        onCircuitClick,
        onRefresh = {
            racesViewModel.fetchRacesData()
        },
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalanderComposble(
    racesState: UiState<ImmutableList<RaceClearData>>,
    racesCurrentState: CurrentRound?,
    onCircuitClick: (String) -> Unit,
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
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Surface(color = Color.Transparent) {
                val isAnyLoading = racesState is UiState.Loading
                val isAnyError = racesState is UiState.Error

                if (isAnyLoading && !isRefreshing) {
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
                    val errorMessage = racesState.message

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
                    val racesList = (racesState as? UiState.Success)?.data
                    Box(
                        Modifier.fillMaxSize(),
                    ) {
                        RacesCards(
                            racesList,
                            racesCurrentState,
                            onCircuitClick,
                            Modifier
                        )

                    }
                }
            }
        }
    }
}