package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.taras.core.navigation.MainNavRoutes
import com.example.taras.core.navigation.Navigator
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.CircuitData
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.DriverProfile
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavPaddockScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavCalendarScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavF1DriversScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavGridScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.SettingDrawer
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.TeamsData

import com.example.taras.viewmodel.AppearanceViewModel
import com.example.taras.viewmodel.UserViewModel

@Composable
fun MainNavHost(
    navigationState: NavState<MainNavRoutes>,
    navigator: Navigator<MainNavRoutes>,
    appearanceViewModel: AppearanceViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        modifier = modifier.fillMaxSize(),
        onBack = { navigator.goBack() },
        entries = navigationState.toEntries(
            entryProvider {
                entry<MainNavRoutes.Paddock> {
                    NavPaddockScreen(modifier = Modifier.fillMaxSize())
                }
                entry<MainNavRoutes.Grid> {
                    NavGridScreen(
                        modifier = Modifier.fillMaxSize(),
                        onDriverClick = { data ->
                            navigator.navigate(MainNavRoutes.DriverProfile(data))
                        },
                        onTeamClick = { teamName ->
                            navigator.navigate(MainNavRoutes.TeamsData(teamName))
                        })
                }
                entry<MainNavRoutes.Calendar> {
                    NavCalendarScreen(
                        onCircuitClick = { circuitId ->
                            navigator.navigate(MainNavRoutes.CircuitData(circuitId))
                        }
                    )
                }
                entry<MainNavRoutes.F1Results> {
                    NavF1DriversScreen(
                    )
                }
                entry<MainNavRoutes.DrawerSetting> {
                    SettingDrawer(
                        appearanceViewModel = appearanceViewModel,
                        userViewModel = userViewModel
                    ) { }
                }
                entry<MainNavRoutes.DriverProfile> { route ->
                    DriverProfile(driverNumber = route.numberOrName)
                }
                entry<MainNavRoutes.CircuitData> { route ->
                    CircuitData(circuitId = route.id)
                }
                entry<MainNavRoutes.TeamsData> { route ->
                    TeamsData(route.numberOrName)
                }
            }
        )
    )
}