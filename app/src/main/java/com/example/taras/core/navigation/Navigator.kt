package com.example.taras.core.navigation

import com.example.taras.view.scaffold.navigation_compose.nav_main.NavState
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey

@Stable
class Navigator<T : NavKey>(val state: NavState<T>) {
    fun navigate(route: T) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            @Suppress("UNCHECKED_CAST")
            (state.backStacks[state.topLevelRoute] as? MutableList<NavKey>)?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return
        val currentRoute = currentStack.lastOrNull() ?: return

        if (currentRoute == state.topLevelRoute) {
            if (state.topLevelRoute != state.startRoute) {
                state.topLevelRoute = state.startRoute
            }
        } else {
            currentStack.removeAt(currentStack.size - 1)
        }
    }
}