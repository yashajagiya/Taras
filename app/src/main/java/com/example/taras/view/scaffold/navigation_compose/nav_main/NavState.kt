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
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Stable
class NavState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: ImmutableMap<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    val stackInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: ImmutableSet<NavKey>
): NavState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializerConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class))
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks =
        topLevelRoutes.associateWith { key ->
            rememberNavBackStack(
                configuration = serializerConfig,
                key
            )
        }.toPersistentMap()

    return remember(startRoute, topLevelRoutes) {
        NavState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

val serializerConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MainNavRoutes.Paddock::class, MainNavRoutes.Paddock.serializer())
            subclass(MainNavRoutes.Grid::class, MainNavRoutes.Grid.serializer())
            subclass(MainNavRoutes.Calendar::class, MainNavRoutes.Calendar.serializer())
            subclass(MainNavRoutes.F1drivers::class, MainNavRoutes.F1drivers.serializer())
            subclass(MainNavRoutes.Driversprofile::class, MainNavRoutes.Driversprofile.serializer())
//            subclass(MainNavRoutes.GridDrivers::class, MainNavRoutes.GridDrivers.serializer())
            subclass(MainNavRoutes.GridTeams::class, MainNavRoutes.GridTeams.serializer())
            subclass(MainNavRoutes.CircitData::class, MainNavRoutes.CircitData.serializer())
        }
    }
}

@Composable
fun NavState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = mutableMapOf<NavKey, List<NavEntry<NavKey>>>()
    backStacks.forEach { (key, stack) ->
        val decoratedStack = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
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