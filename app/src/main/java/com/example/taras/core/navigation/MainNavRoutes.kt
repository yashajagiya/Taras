package com.example.taras.core.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed class MainNavRoutes : NavKey {

    @Serializable
    data object Paddock : MainNavRoutes()

    @Serializable
    data object Grid : MainNavRoutes()

    @Serializable
    data object Calendar : MainNavRoutes()

    @Serializable
    data object F1Results : MainNavRoutes()
    @Serializable
    data object DrawerSetting : MainNavRoutes()
    @Serializable
    data class DriverProfile(val numberOrName: String) : MainNavRoutes()

    @Serializable
    data class CircuitData(val id: String) : MainNavRoutes()


    @Serializable
    data class TeamsData(val numberOrName: String) : MainNavRoutes()

    @Serializable
    data class Comparison(val initialDriver1: String = "", val initialDriver2: String = "") : MainNavRoutes()

    @Serializable
    data object TcgBinder : MainNavRoutes()

    @Serializable
    data class TcgScratchPack(val cardId: String = "") : MainNavRoutes()

    @Serializable
    data class TcgArena(val card1Id: String = "card_fer_44", val card2Id: String = "card_rbr_03") : MainNavRoutes()
}
