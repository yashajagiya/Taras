package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.taras.navigation.MainNavRoutes
import com.example.taras.navigation.Navigator
import com.example.taras.view.scaffold.navigation_compose.nav_screens.CircitData
import com.example.taras.view.scaffold.navigation_compose.nav_screens.DriverProfile
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NavPaddockScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.naveCalendarScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NaveF1DriversScreen
import com.example.taras.view.scaffold.navigation_compose.nav_screens.NaveGridScreen

@Composable
fun MainNavHost(
    navigationState: NavState,
    navigator: Navigator
) {
    NavDisplay(
        modifier = Modifier
            .fillMaxSize(),
        onBack = { navigator.goBack() },
        entries = navigationState.toEntries(
            entryProvider {
                entry<MainNavRoutes.Paddock> {
                    NavPaddockScreen()
                }
                entry<MainNavRoutes.Grid> {
                    NaveGridScreen()
                }
                entry<MainNavRoutes.Calendar> {
                    naveCalendarScreen(
                        onCircitclick = { circitname ->
                            navigator.navigate(MainNavRoutes.CircitData(circitname))
                        }
                    )
                }
                entry<MainNavRoutes.F1drivers> {
                    NaveF1DriversScreen(
                        onDriverclick = { drievrNumber ->
                            navigator.navigate(MainNavRoutes.Driversprofile(drievrNumber))
                        }
                    )
                }
                entry<MainNavRoutes.Driversprofile> {
                    DriverProfile(
                        drievrNumber = it.id
                    )
                }
//                entry<MainNavRoutes.GridDrivers> {
//                    DriverCard(
//                        driver = TODO()
//                    )
//                }
//                entry<MainNavRoutes.GridTeams> {
//                    naveCalendarScreen()
//                }
                entry<MainNavRoutes.CircitData> {
                    CircitData(circitname = it.name)
                }
            }
        )
    )
}


