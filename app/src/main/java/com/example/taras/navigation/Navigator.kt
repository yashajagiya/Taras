package com.example.taras.navigation

import com.example.taras.view.scaffold.navigation_compose.nav_main.NavState
import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavState) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack =
            state.backStacks[state.topLevelRoute] ?: error("backstack error")
        val currentRoute = currentStack.last()
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
