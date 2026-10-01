package com.example.taras.core.repository

import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.F1DriversInfoResponse
import com.example.taras.network_calls.taras.model.F1TeamsInfoResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class F1InfoRepository(
    private val tarasDataService: TarasDataService = NetworkModule.tarasGithubRetrofit.create(TarasDataService::class.java)
) {
    private val mutex = Mutex()
    private var cachedDriversInfo: List<F1DriversInfoResponse>? = null
    private var cachedTeamsInfo: List<F1TeamsInfoResponse>? = null

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
    }

    companion object {
        val instance: F1InfoRepository by lazy { F1InfoRepository() }
    }
}
