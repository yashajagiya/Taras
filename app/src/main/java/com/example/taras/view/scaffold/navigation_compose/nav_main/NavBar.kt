package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.taras.core.navigation.MainNavRoutes
import com.example.taras.core.navigation.NAV_BAR_PARAMETER

@Composable
fun MainNavBar(
    selectedItem: MainNavRoutes,
    onSelectedItem: (MainNavRoutes) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NAV_BAR_PARAMETER.forEach { (routeKey, itemData) ->
            NavigationBarItem(
                selected = routeKey == selectedItem,
                onClick = { onSelectedItem(routeKey) },
                icon = {
                    Icon(
                        imageVector = itemData.icon,
                        contentDescription = itemData.title
                    )
                },
                label = {
                    Text(
                        text = itemData.title,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}