package com.example.taras.domain.repository

import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture repository contract for Race and Calendar data.
 */
interface RaceRepository {
    suspend fun getCalendar(forceRefresh: Boolean = false): List<CalendarRaceEvent>
    fun getCachedCalendar(): Flow<List<CalendarRaceEvent>?>
    suspend fun saveCurrentSessionStatus(sessionName: String, sessionTime: String)
}
