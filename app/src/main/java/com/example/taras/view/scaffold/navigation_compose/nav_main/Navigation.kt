package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.taras.navigation.MainNavRoutes
import com.example.taras.navigation.Navigator
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.CircuitData
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.DriverProfile
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavPaddockScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavCalendarScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NaveF1DriversScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NaveGridScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.TeamsData

@Composable
fun MainNavHost(
    navigationState: NavState<MainNavRoutes>,
    navigator: Navigator<MainNavRoutes>,
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
                    NaveGridScreen(
                        modifier = Modifier.fillMaxSize(),
                        onDriverClick = { driverNumber ->
                            navigator.navigate(MainNavRoutes.DriverProfile(driverNumber))
                        })
                }
                entry<MainNavRoutes.Calendar> {
                    NavCalendarScreen(
                        onCircuitClick = { circuitName ->
                            navigator.navigate(MainNavRoutes.CircuitData(circuitName))
                        }
                    )
                }
                entry<MainNavRoutes.Result> {
                    NaveF1DriversScreen(
                    )
                }
                entry<MainNavRoutes.DriverProfile> { route ->
                    DriverProfile(driverNumber = route.numberOrName)
                }
                entry<MainNavRoutes.CircuitData> { route ->
                    CircuitData(circuitName = route.name)
                }
                entry<MainNavRoutes.TeamsData> { route ->
                    TeamsData(circuitName = route.numberOrName)
                }
            }
        )
    )
}