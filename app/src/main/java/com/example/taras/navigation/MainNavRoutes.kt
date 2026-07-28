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
    data object F1drivers : MainNavRoutes()

    @Serializable
    data class DriverProfile(val driverNumber: String) : MainNavRoutes()

    @Serializable
    data class CircuitData(val name: String) : MainNavRoutes()
}