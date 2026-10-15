package com.example.taras.core.helpercore

import android.content.Context
import android.util.Log
import com.example.taras.core.common.CurrentData
import com.example.taras.core.common.UiState
import com.example.taras.core.common.toAppError
import com.example.taras.core.engine.RaceStateEngine
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import com.example.taras.viewmodel.CurrentRace
import com.example.taras.viewmodel.SessionInfo
import kotlinx.coroutines.flow.firstOrNull

class RaceRepository(
    private val racesDataService: TarasDataService = NetworkModule.tarasDataService
) {
    suspend fun getNextRaceData(context: Context): Pair<UiState<CurrentRace?>, SessionInfo?> {
        return try {
            val calendarData = racesDataService.getCalendar()

            // Save full season to cache
            try {
                val currentData = CurrentData(context)
                val json = NetworkModule.json.encodeToString(calendarData)
                currentData.saveRacesData(json)
            } catch (e: Exception) {
                Log.e("RaceRepository", "Error saving to cache", e)
            }

            processRacesData(calendarData)
        } catch (e: Exception) {
            Log.e("RaceRepository", "Error fetching from API, trying cache", e)
            try {
                val currentData = CurrentData(context)
                val cachedJson = currentData.racesData.firstOrNull()
                if (cachedJson != null) {
                    val cachedData = NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(cachedJson)
                    processRacesData(cachedData)
                } else {
                    Pair(UiState.Error(e.toAppError()), null)
                }
            } catch (cacheEx: Exception) {
                Log.e("RaceRepository", "Error fetching from cache", cacheEx)
                Pair(UiState.Error(e.toAppError()), null)
            }
        }
    }

    private fun processRacesData(calendarData: List<CalendarRaceEvent>): Pair<UiState<CurrentRace?>, SessionInfo?> {
        val upcomingRaces = RaceStateEngine.findCurrentOrUpcomingRaces(calendarData)
        val currentRace = upcomingRaces.firstOrNull()
        val nextSessionInfo = RaceStateEngine.findNextSessionInfo(upcomingRaces, forWidget = true)
        return Pair(UiState.Success(currentRace), nextSessionInfo)
    }

    /**
     * Retrieves calendar data with 3-tier offline resilience:
     * 1. Persistent DataStore cache (instant 0ms)
     * 2. Returns empty state if app has never been opened (triggers default onboarding screen)
     * 3. Opportunistic background network refresh if app has been opened
     */
    suspend fun getOfflineResilientCalendar(context: Context): Triple<UiState<CurrentRace?>, SessionInfo?, List<CalendarRaceEvent>> {
        var calendarData: List<CalendarRaceEvent>? = try {
            val currentData = CurrentData(context)
            val cachedJson = currentData.racesData.firstOrNull()
            if (!cachedJson.isNullOrBlank()) {
                NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(cachedJson)
            } else null
        } catch (e: Exception) {
            Log.e("RaceRepository", "Error reading DataStore cache", e)
            null
        }

        // If app has never been opened or synced by the user, return empty to display default onboarding screen
        if (calendarData.isNullOrEmpty()) {
            return Triple(UiState.Success(null), null, emptyList())
        }

        // Opportunistic background network refresh if app has synced before
        try {
            val liveData = racesDataService.getCalendar()
            if (liveData.isNotEmpty()) {
                calendarData = liveData
                try {
                    val currentData = CurrentData(context)
                    val json = NetworkModule.json.encodeToString(liveData)
                    currentData.saveRacesData(json)
                } catch (e: Exception) {
                    Log.e("RaceRepository", "Error saving updated calendar to cache", e)
                }
            }
        } catch (_: Exception) {
            // Offline or network error: gracefully keep cached calendar
        }

        val upcomingRaces = RaceStateEngine.findCurrentOrUpcomingRaces(calendarData)
        val currentRace = upcomingRaces.firstOrNull()
        val nextSessionInfo = RaceStateEngine.findNextSessionInfo(upcomingRaces, forWidget = true)

        return Triple(UiState.Success(currentRace), nextSessionInfo, calendarData)
    }
}