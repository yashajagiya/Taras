package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation3.runtime.NavKey
import com.example.taras.navigation.MainNavRoutes
import com.example.taras.navigation.NAV_BAR_PARAMETER

@Composable
fun MainNavBar(
    modifier: Modifier = Modifier,
    selectedItem: NavKey,
    onSelectedItem: (NavKey) -> Unit
) {

    NavigationBar() {
        NAV_BAR_PARAMETER.forEach { (navbarparameter, data) ->
            NavigationBarItem(
                selected = navbarparameter == selectedItem,
                onClick = { onSelectedItem(navbarparameter) },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.title
                    )
                },
                label = {
                    Text(
                        text = data.title,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    }

}