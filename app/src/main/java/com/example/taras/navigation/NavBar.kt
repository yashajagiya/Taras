package com.example.taras.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun NavBar(modifier: Modifier = Modifier) {
    val navItems = listOf(
        NavItem("paddock", Icons.Default.Home),
        NavItem("grid", Icons.Default.GridView),
        NavItem("calendar", Icons.Default.DateRange),
        NavItem("f1drivers", Icons.Default.Person)
    )

}

data class NavItem(val title: String, val icon: ImageVector)