package com.example.taras.view.scaffold.navigation_compose.nav_main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import com.example.taras.navigation.MainNavRoutes
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Stable
class NavState<T : NavKey>(
    val startRoute: T,
    topLevelRoute: MutableState<T>,
    val backStacks: ImmutableMap<T, NavBackStack<T>>
) {
    var topLevelRoute by topLevelRoute

    val stackInUse: List<T>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

val serializerConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MainNavRoutes.Paddock::class)
            subclass(MainNavRoutes.Grid::class)
            subclass(MainNavRoutes.Calendar::class)
            subclass(MainNavRoutes.Result::class)
            subclass(MainNavRoutes.DriverProfile::class)
            subclass(MainNavRoutes.CircuitData::class)
            subclass(MainNavRoutes.TeamsData::class)
        }
    }
}

@Composable
fun <T : NavKey> rememberNavigationState(
    startRoute: T,
    topLevelRoutes: ImmutableSet<T>,
    serializer: KSerializer<T>
): NavState<T> {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializerConfig,
        serializer = MutableStateSerializer(serializer)
    ) {
        mutableStateOf(startRoute)
    }

    @Suppress("UNCHECKED_CAST")
    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = serializerConfig,
            key
        ) as NavBackStack<T>
    }.toPersistentMap()

    return remember(startRoute, topLevelRoutes) {
        NavState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

@Composable
fun <T : NavKey> NavState<T>.toEntries(
    entryProvider: (T) -> NavEntry<T>
): SnapshotStateList<NavEntry<T>> {
    val decoratedEntries = mutableMapOf<T, List<NavEntry<T>>>()
    backStacks.forEach { (key, stack) ->
        val decoratedStack = listOf(
            rememberSaveableStateHolderNavEntryDecorator<T>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        decoratedEntries[key] = rememberDecoratedNavEntries(
            backStack = stack,
            entryProvider = entryProvider,
            entryDecorators = decoratedStack
        )
    }
    return stackInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}