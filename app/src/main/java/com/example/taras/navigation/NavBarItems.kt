package com.example.taras.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentMapOf

data class NavBarPra(
    val icon: ImageVector,
    val title: String
)

val NAV_BAR_PARAMETER = persistentMapOf(
    MainNavRoutes.Paddock to NavBarPra(
        icon = Icons.Default.Home,
        title = "Paddock"
    ),
    MainNavRoutes.Grid to NavBarPra(
        icon = Icons.Default.GridView,
        title = "Grid"
    ),
    MainNavRoutes.Calendar to NavBarPra(
        icon = Icons.Default.CalendarMonth,
        title = "Calendar"
    ),
    MainNavRoutes.F1drivers to NavBarPra(
        icon = Icons.Default.Person,
        title = "Drivers"
    )
)