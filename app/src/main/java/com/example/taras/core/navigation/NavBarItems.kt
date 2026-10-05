package com.example.taras.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.taras.R
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class NavBarItemData(
    val icon: ImageVector,
    val title: String,
    @StringRes val titleRes: Int = 0
)

val NAV_BAR_PARAMETER = persistentMapOf(
    MainNavRoutes.Paddock to NavBarItemData(
        icon = Icons.Default.Home,
        title = "Paddock",
        titleRes = R.string.nav_paddock
    ),
    MainNavRoutes.Grid to NavBarItemData(
        icon = Icons.Default.GridView,
        title = "Grid",
        titleRes = R.string.nav_grid
    ),
    MainNavRoutes.Calendar to NavBarItemData(
        icon = Icons.Default.CalendarMonth,
        title = "Calendar",
        titleRes = R.string.nav_calendar
    ),
    MainNavRoutes.F1Results to NavBarItemData(
        icon = Icons.Default.DirectionsCar,
        title = "Result",
        titleRes = R.string.nav_results
    )
)