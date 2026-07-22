package com.example.taras.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavRoutes : NavKey {
    @Serializable
    data object Paddock : MainNavRoutes() {
//        override val icon = Icons.Default.Home
//        override val title = "Paddock"
    }

    @Serializable
    data object Grid : MainNavRoutes() {
//        override val icon = Icons.Default.GridView
//        override val title = "Grid"
    }

    @Serializable
    data object Calendar : MainNavRoutes() {
//        override val icon = Icons.Default.CalendarMonth
//        override val title = "Calendar"
    }

    @Serializable
    data object F1drivers : MainNavRoutes() {
//        override val icon = Icons.Default.Person
//        override val title = "Drivers"
    }
    @Serializable
    data class Driversprofile(val id : String) : MainNavRoutes() {
//        override val icon = Icons.Default.Person
//        override val title = "Drivers"
    }


    @Serializable
    data object GridDrivers : MainNavRoutes() {

    }
    @Serializable
    data object GridTeams : MainNavRoutes() {

    }
    @Serializable
    data class CircitData(val name : String) : MainNavRoutes() {
//        override val icon = Icons.Default.Person
//        override val title = "Drivers"
    }





}

