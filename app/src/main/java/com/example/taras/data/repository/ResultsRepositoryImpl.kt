package com.example.taras.data.repository

import com.example.taras.domain.repository.ResultsRepository
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.OverviewResponse
import com.example.taras.network_calls.taras.model.v2.WeekendResultsResponse

/**
 * Data layer implementation of ResultsRepository.
 */
class ResultsRepositoryImpl(
    private val dataService: TarasDataService
) : ResultsRepository {

    override suspend fun getResultsByRound(round: Int): WeekendResultsResponse {
        return dataService.getResultsByRound(round)
    }

    override suspend fun getLatestResults(): WeekendResultsResponse {
        return dataService.getLatestResults()
    }

    override suspend fun getOverview(): OverviewResponse {
        return dataService.getOverview()
    }
}
