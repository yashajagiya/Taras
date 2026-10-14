package com.example.taras.domain.repository

import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2

/**
 * Clean Architecture repository contract for Team data.
 */
interface TeamRepository {
    suspend fun getTeams(forceRefresh: Boolean = false): List<TeamDetailResponseV2>
    fun clearCache()
}
