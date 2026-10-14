package com.example.taras.domain.usecase

import com.example.taras.domain.repository.RaceRepository
import com.example.taras.network_calls.taras.model.v2.CalendarRaceEvent
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture Use Case to retrieve F1 race events and calendar status.
 */
class GetRaceCalendarUseCase(
    private val repository: RaceRepository
) {
    suspend fun getCalendar(forceRefresh: Boolean = false): List<CalendarRaceEvent> {
        return repository.getCalendar(forceRefresh)
    }

    fun getCachedCalendar(): Flow<List<CalendarRaceEvent>?> {
        return repository.getCachedCalendar()
    }

    suspend fun saveCurrentSessionStatus(sessionName: String, sessionTime: String) {
        repository.saveCurrentSessionStatus(sessionName, sessionTime)
    }
}
