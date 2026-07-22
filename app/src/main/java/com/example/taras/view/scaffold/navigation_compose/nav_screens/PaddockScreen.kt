package com.example.taras.view.scaffold.navigation_compose.nav_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.UiState
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavePaddockScreen() {

    val driversViewModel: DriversViewModel = viewModel()
    val drivertopthrree by driversViewModel.topthree.collectAsStateWithLifecycle()
    val driverDetailsState by driversViewModel.driverDetails.collectAsStateWithLifecycle()

    val newsViewModel: NewsViewModel = viewModel()
    val newsState by newsViewModel.news.collectAsStateWithLifecycle()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(drivertopthrree, newsState) {
        if (drivertopthrree !is UiState.Loading && newsState !is UiState.Loading) {
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            driversViewModel.fetchDriverData()
            newsViewModel.fetchNews()
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
            if (drivertopthrree is UiState.Loading && newsState is UiState.Loading && !isRefreshing) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator()
                    Spacer(Modifier.requiredHeight(30.dp))
                    Text("loading......")
                }
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "navigate to PaddockScreen", modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}
