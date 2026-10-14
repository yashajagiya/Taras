package com.example.taras.domain.repository

import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse

/**
 * Clean Architecture repository contract for Race & Weekend Results.
 */
interface ResultsRepository {
    suspend fun getResultsByRound(round: Int): WeekendResultsResponse
    suspend fun getLatestResults(): WeekendResultsResponse
    suspend fun getOverview(): OverviewResponse
}
