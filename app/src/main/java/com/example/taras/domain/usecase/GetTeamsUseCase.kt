package com.example.taras.domain.usecase

import com.example.taras.domain.repository.TeamRepository
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2

/**
 * Clean Architecture Use Case to retrieve constructor/team details.
 */
class GetTeamsUseCase(
    private val repository: TeamRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): List<TeamDetailResponseV2> {
        return repository.getTeams(forceRefresh)
    }
}
