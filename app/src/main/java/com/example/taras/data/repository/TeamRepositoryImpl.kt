package com.example.taras.data.repository

import com.example.taras.domain.repository.TeamRepository
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Data layer implementation of TeamRepository.
 */
class TeamRepositoryImpl(
    private val dataService: TarasDataService
) : TeamRepository {

    private val mutex = Mutex()
    private var cachedTeams: List<TeamDetailResponseV2>? = null

    override suspend fun getTeams(forceRefresh: Boolean): List<TeamDetailResponseV2> {
        if (!forceRefresh) {
            cachedTeams?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedTeams?.let { return it }
            }
            val remoteTeams = dataService.getTeams()
            cachedTeams = remoteTeams
            remoteTeams
        }
    }

    override fun clearCache() {
        cachedTeams = null
    }
}
