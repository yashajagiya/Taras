package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import com.example.taras.navigation.MainNavRoutes
import com.example.taras.navigation.NAV_BAR_PARAMETER
import com.example.taras.navigation.Navigator
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavBar
import com.example.taras.view.scaffold.navigation_compose.nav_main.MainNavHost
import com.example.taras.view.scaffold.navigation_compose.nav_main.rememberNavigationState
import com.example.taras.view.scaffold.topbar.TarasTopBar


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigationState = rememberNavigationState(
                startRoute = MainNavRoutes.Paddock,
                topLevelRoutes = NAV_BAR_PARAMETER.keys,
                serializer = MainNavRoutes.serializer()
            )
            val navigator = remember { Navigator(navigationState) }

            TarasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TarasTopBar() },

                    bottomBar = {
                        MainNavBar(
                            selectedItem = navigationState.topLevelRoute,
                            onSelectedItem = { navigator.navigate(it) }
                        )

                    }

                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainNavHost(
                            navigationState = navigationState,
                            navigator = navigator
                        )
                    }
                }
            }

        }
    }
}