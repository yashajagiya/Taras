package com.example.taras.core.repository

import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.network_calls.taras.model.F1TeamsInfoResponse
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import com.example.taras.network_calls.taras.model.v2.TeamDetailResponseV2
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class F1InfoRepository(
    private val tarasDataService: TarasDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)
) {
    private val mutex = Mutex()
    private var cachedDriversInfo: List<F1DriversInfoResponse>? = null
    private var cachedTeamsInfo: List<F1TeamsInfoResponse>? = null

    private var cachedDriversV2: List<DriverDetailResponseV2>? = null
    private var cachedTeamsV2: List<TeamDetailResponseV2>? = null

    // v2 Repository Methods
    suspend fun getDriversV2(forceRefresh: Boolean = false): List<DriverDetailResponseV2> {
        if (!forceRefresh) {
            cachedDriversV2?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedDriversV2?.let { return it }
            }
            val data = tarasDataService.getDrivers()
            cachedDriversV2 = data
            data
        }
    }

    suspend fun getTeamsV2(forceRefresh: Boolean = false): List<TeamDetailResponseV2> {
        if (!forceRefresh) {
            cachedTeamsV2?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedTeamsV2?.let { return it }
            }
            val data = tarasDataService.getTeams()
            cachedTeamsV2 = data
            data
        }
    }

    // Legacy v1 Repository Methods (maintained during migration)
    suspend fun getDriverInfoData(forceRefresh: Boolean = false): List<F1DriversInfoResponse> {
        if (!forceRefresh) {
            cachedDriversInfo?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedDriversInfo?.let { return it }
            }
            val data = tarasDataService.getDriverInfoData()
            cachedDriversInfo = data
            data
        }
    }

    suspend fun getTeamsInfoData(forceRefresh: Boolean = false): List<F1TeamsInfoResponse> {
        if (!forceRefresh) {
            cachedTeamsInfo?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedTeamsInfo?.let { return it }
            }
            val data = tarasDataService.getTeamsInfoData()
            cachedTeamsInfo = data
            data
        }
    }

    fun clearCache() {
        cachedDriversInfo = null
        cachedTeamsInfo = null
        cachedDriversV2 = null
        cachedTeamsV2 = null
    }

    companion object {
        val instance: F1InfoRepository by lazy { F1InfoRepository() }
    }
}
