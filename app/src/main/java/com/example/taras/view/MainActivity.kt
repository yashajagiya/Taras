package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.taras.core.common.UiState
import com.example.taras.navigation.MainNavRoutes
import com.example.taras.navigation.NAV_BAR_PARAMETER
import com.example.taras.navigation.Navigator
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavBar
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavHost
import com.example.taras.view.scaffold.navigation_compose.nav_main.rememberNavigationState
import com.example.taras.view.scaffold.topbar.topbarCompose


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigationState = rememberNavigationState(
                startRoute = MainNavRoutes.Paddock,
                topLevelRoutes = NAV_BAR_PARAMETER.keys
            )
            val navigator = remember { Navigator(navigationState) }

            TarasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { topbarCompose() },

                    bottomBar = {
                        MainNavBar(
                            selectedItem = navigationState.topLevelRoute,
                            onSelectedItem = { navigator.navigate(it) }
                        )

                    }

                ) { innerPadding ->

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        MainNavHost(navigationState, navigator)
                    }
                }
            }


//
//                        is UiState.Success -> {
//                            val news = (newsState as UiState.Success).data
//                            if (news.isNotEmpty()) {
//                                item {
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(horizontal = 16.dp, vertical = 12.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Text(
//                                            text = "📰",
//                                            modifier = Modifier.padding(end = 8.dp)
//                                        )
//                                        Text(
//                                            text = "Latest News",
//                                            style = typography.titleLarge,
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                        Spacer(modifier = Modifier.weight(1f))
//                                        Text(
//                                            text = "LIVE",
//                                            color = Color.Red,
//                                            style = typography.labelSmall,
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier
//                                                .background(
//                                                    Color.Red.copy(alpha = 0.1f),
//                                                    RoundedCornerShape(4.dp)
//                                                )
//                                                .padding(horizontal = 6.dp, vertical = 2.dp)
//                                        )
//                                    }
//                                }
//
//                                item {
//                                    val pagerState = rememberPagerState(pageCount = { news.size })
//                                    HorizontalPager(
//                                        state = pagerState,
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(bottom = 24.dp),
//                                        contentPadding = PaddingValues(horizontal = 16.dp),
//                                        pageSpacing = 12.dp
//                                    ) { page ->
//                                        val newsItem = news[page]
//                                        val defaultColor = colorScheme.surfaceVariant
//
//                                        val driversList =
//                                            (driverDetailsState as? UiState.Success)?.data
//                                                ?: emptyList()
//                                        val teamsList = (teamsImagesState as? UiState.Success)?.data
//                                            ?: emptyList()
//
//                                        val newsColor = getNewsColor(
//                                            newsItem = newsItem,
//                                            drivers = driversList,
//                                            teams = teamsList,
//                                            defaultColor = defaultColor
//                                        )
//                                        NewsCard(
//                                            item = newsItem,
//                                            containerColor = newsColor
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
        }
    }
}

