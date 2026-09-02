package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.taras.core.navigation.MainNavRoutes
import com.example.taras.core.navigation.NAV_BAR_PARAMETER
import com.example.taras.core.navigation.Navigator
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavBar
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavHost
import com.example.taras.view.scaffold.navigation_compose.nav_main.rememberNavigationState
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.SettingDrawer
import com.example.taras.view.scaffold.topbar.TarasTopBar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taras.core.common.OfflineDataStoreAppearance
import com.example.taras.core.common.UserPreferences
import com.example.taras.viewmodel.AppearanceViewModel
import com.example.taras.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val appearanceViewModel: AppearanceViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AppearanceViewModel(OfflineDataStoreAppearance(context.applicationContext)) as T
                    }
                }
            )
            val userViewModel: UserViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return UserViewModel(UserPreferences(context.applicationContext)) as T
                    }
                }
            )
            val appearance by appearanceViewModel.appearanceData.collectAsStateWithLifecycle()
            val darkTheme = when (appearance) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            val navigationState = rememberNavigationState(
                startRoute = MainNavRoutes.Paddock,
                topLevelRoutes = NAV_BAR_PARAMETER.keys,
                serializer = MainNavRoutes.serializer()
            )
            val navigator = remember { Navigator(navigationState) }

            TarasTheme(darkTheme = darkTheme) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var appearanceExpanded by remember { mutableStateOf(false) }

                SettingDrawer(
                    drawerState = drawerState,
                    appearanceViewModel = appearanceViewModel,
                    userViewModel = userViewModel,
                    appearanceExpanded = appearanceExpanded,
                    onAppearanceExpandChange = { appearanceExpanded = it }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TarasTopBar(
                                onProfileClick = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        },

                        bottomBar = {
                            MainNavBar(
                                selectedItem = navigationState.topLevelRoute,
                                onSelectedItem = { navigator.navigate(it) }
                            )

                        }

                    ) { innerPadding ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            MainNavHost(
                                navigationState = navigationState,
                                navigator = navigator,
                                appearanceViewModel = appearanceViewModel,
                                userViewModel = userViewModel
                            )
                        }
                    }
                }
            }

        }
    }
}