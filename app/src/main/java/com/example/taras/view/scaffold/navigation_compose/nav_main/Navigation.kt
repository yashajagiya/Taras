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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainNavHost(
    navigationState: NavState<MainNavRoutes>,
    navigator: Navigator<MainNavRoutes>,
    appearanceViewModel: AppearanceViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    NavDisplay(
        modifier = modifier.fillMaxSize(),
        onBack = {
            if (!navigator.goBack()) {
                (context as? android.app.Activity)?.finish()
            }
        },
        entries = navigationState.toEntries(
            entryProvider {
                entry<MainNavRoutes.Paddock> {
                    NavPaddockScreen(
                        userViewModel = userViewModel,
                        onDriverClick = { driverNum ->
                            navigator.navigate(MainNavRoutes.DriverProfile(driverNum))
                        },
                        onTeamClick = { teamName ->
                            navigator.navigate(MainNavRoutes.TeamsData(teamName))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                entry<MainNavRoutes.Grid> {
                    NavGridScreen(
                        modifier = Modifier.fillMaxSize(),
                        userViewModel = userViewModel,
                        onDriverClick = { data ->
                            navigator.navigate(MainNavRoutes.DriverProfile(data))
                        },
                        onTeamClick = { teamName ->
                            navigator.navigate(MainNavRoutes.TeamsData(teamName))
                        },
                        onCompareClick = {
                            navigator.navigate(MainNavRoutes.Comparison())
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
                    DriverProfile(
                        driverNumber = route.numberOrName,
                        userViewModel = userViewModel,
                        onCompareTeammatesClick = { d1, d2 ->
                            navigator.navigate(MainNavRoutes.Comparison(initialDriver1 = d1, initialDriver2 = d2))
                        },
                        onDriverClick = { teammateNum ->
                            navigator.navigate(MainNavRoutes.DriverProfile(teammateNum))
                        },
                        onNavigateToTcgBinder = {
                            navigator.navigate(MainNavRoutes.TcgBinder)
                        }
                    )
                }
                entry<MainNavRoutes.CircuitData> { route ->
                    CircuitData(circuitId = route.id)
                }
                entry<MainNavRoutes.TeamsData> { route ->
                    TeamsData(
                        teamName = route.numberOrName,
                        userViewModel = userViewModel,
                        onCompareTeammatesClick = { d1, d2 ->
                            navigator.navigate(MainNavRoutes.Comparison(initialDriver1 = d1, initialDriver2 = d2))
                        },
                        onDriverClick = { driverNum ->
                            navigator.navigate(MainNavRoutes.DriverProfile(driverNum))
                        }
                    )
                }
                entry<MainNavRoutes.Comparison> { route ->
                    com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens.ComparisonScreen(
                        initialDriver1 = route.initialDriver1,
                        initialDriver2 = route.initialDriver2,
                        onBackClick = { navigator.goBack() },
                        onBattleArenaClick = { d1, d2 ->
                            val c1 = com.example.taras.core.tcg.seed.RosterSeedData.allCards.find { it.code.equals(d1, true) || it.name.contains(d1, true) }?.id ?: "card_fer_44"
                            val c2 = com.example.taras.core.tcg.seed.RosterSeedData.allCards.find { it.code.equals(d2, true) || it.name.contains(d2, true) }?.id ?: "card_rbr_03"
                            navigator.navigate(MainNavRoutes.TcgArena(card1Id = c1, card2Id = c2))
                        }
                    )
                }
                entry<MainNavRoutes.TcgBinder> {
                    val tcgViewModel: com.example.taras.viewmodel.TcgViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                val db = com.example.taras.core.db.AppDatabase.getDatabase(context)
                                val repo = com.example.taras.core.tcg.repository.TcgRepository(db.tcgCardDao())
                                @Suppress("UNCHECKED_CAST")
                                return com.example.taras.viewmodel.TcgViewModel(repo) as T
                            }
                        }
                    )
                    com.example.taras.view.tcg.screens.BinderGridScreen(
                        viewModel = tcgViewModel,
                        onBackClick = { navigator.goBack() },
                        onOpenPackClick = {
                            tcgViewModel.openMysteryPack { drawnCard ->
                                navigator.navigate(MainNavRoutes.TcgScratchPack(cardId = drawnCard.id))
                            }
                        },
                        onBattleClick = { card1, card2 ->
                            navigator.navigate(MainNavRoutes.TcgArena(card1Id = card1, card2Id = card2))
                        }
                    )
                }
                entry<MainNavRoutes.TcgScratchPack> { route ->
                    val tcgViewModel: com.example.taras.viewmodel.TcgViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                val db = com.example.taras.core.db.AppDatabase.getDatabase(context)
                                val repo = com.example.taras.core.tcg.repository.TcgRepository(db.tcgCardDao())
                                @Suppress("UNCHECKED_CAST")
                                return com.example.taras.viewmodel.TcgViewModel(repo) as T
                            }
                        }
                    )
                    val uiState by tcgViewModel.uiState.collectAsStateWithLifecycle()
                    val cardToScratch = uiState.allBinderItems.find { it.card.id == route.cardId }?.card
                        ?: uiState.unrevealedCard
                        ?: com.example.taras.core.tcg.seed.RosterSeedData.allCards.first()

                    com.example.taras.view.tcg.screens.PackOpeningScreen(
                        card = cardToScratch,
                        onScratchFinished = { cardId ->
                            tcgViewModel.completeScratch(cardId)
                        },
                        onBackClick = { navigator.goBack() },
                        onBattleClick = { cardId ->
                            val opponent = if (cardId == "card_fer_44") "card_rbr_03" else "card_fer_44"
                            navigator.navigate(MainNavRoutes.TcgArena(card1Id = cardId, card2Id = opponent))
                        }
                    )
                }
                entry<MainNavRoutes.TcgArena> { route ->
                    com.example.taras.view.tcg.screens.BattleArenaScreen(
                        initialCard1Id = route.card1Id,
                        initialCard2Id = route.card2Id,
                        onBackClick = { navigator.goBack() }
                    )
                }
            }
        )
    )
}