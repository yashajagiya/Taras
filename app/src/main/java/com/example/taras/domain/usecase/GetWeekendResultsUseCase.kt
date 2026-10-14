package com.example.taras.domain.usecase

import com.example.taras.domain.repository.ResultsRepository
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse

/**
 * Clean Architecture Use Case to fetch weekend race session results.
 */
class GetWeekendResultsUseCase(
    private val repository: ResultsRepository
) {
    suspend fun getResultsByRound(round: Int): WeekendResultsResponse {
        return repository.getResultsByRound(round)
    }

    suspend fun getLatestResults(): WeekendResultsResponse {
        return repository.getLatestResults()
    }
}
