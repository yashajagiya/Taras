package com.example.taras.navigation

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
    data class DriverProfile(val numberOrName: String) : MainNavRoutes()

    @Serializable
    data class CircuitData(val id: String) : MainNavRoutes()


    @Serializable
    data class TeamsData(val numberOrName: String) : MainNavRoutes()}
