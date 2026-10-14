package com.example.taras.data.repository

import android.util.Log
import com.example.taras.core.common.CurrentData
import com.example.taras.domain.repository.RaceRepository
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Data layer implementation of RaceRepository with offline-first DataStore caching.
 */
class RaceRepositoryImpl(
    private val dataService: TarasDataService,
    private val currentData: CurrentData
) : RaceRepository {

    private val tag = "RaceRepositoryImpl"

    override suspend fun getCalendar(forceRefresh: Boolean): List<CalendarRaceEvent> {
        return try {
            val remoteCalendar = dataService.getCalendar()
            try {
                val json = NetworkModule.json.encodeToString(remoteCalendar)
                currentData.saveRacesData(json)
            } catch (e: Exception) {
                Log.e(tag, "Error saving calendar to cache", e)
            }
            remoteCalendar
        } catch (e: Exception) {
            Log.e(tag, "Error fetching calendar from API, fallback to cache", e)
            val cachedJson = currentData.racesData.firstOrNull()
            if (!cachedJson.isNullOrBlank()) {
                NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(cachedJson)
            } else {
                throw e
            }
        }
    }

    override fun getCachedCalendar(): Flow<List<CalendarRaceEvent>?> {
        return currentData.racesData.map { json ->
            if (!json.isNullOrBlank()) {
                try {
                    NetworkModule.json.decodeFromString<List<CalendarRaceEvent>>(json)
                } catch (_: Exception) {
                    null
                }
            } else null
        }
    }

    override suspend fun saveCurrentSessionStatus(sessionName: String, sessionTime: String) {
        currentData.saveCurrentSessionStatus(sessionName, sessionTime)
    }
}
