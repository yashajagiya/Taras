package com.example.taras.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class NavBarItemData(
    val icon: ImageVector,
    val title: String
)

val NAV_BAR_PARAMETER = persistentMapOf(
    MainNavRoutes.Paddock to NavBarItemData(
        icon = Icons.Default.Home,
        title = "Paddock"
    ),
    MainNavRoutes.Grid to NavBarItemData(
        icon = Icons.Default.GridView,
        title = "Grid"
    ),
    MainNavRoutes.Calendar to NavBarItemData(
        icon = Icons.Default.CalendarMonth,
        title = "Calendar"
    ),
    MainNavRoutes.F1Results to NavBarItemData(
        icon = Icons.Default.DirectionsCar,
        title = "Result"
    )
)