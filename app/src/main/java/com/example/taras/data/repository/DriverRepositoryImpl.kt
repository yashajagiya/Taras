package com.example.taras.data.repository

import com.example.taras.core.db.TopThreeDriversDAO
import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.domain.repository.DriverRepository
import com.example.taras.network_calls.taras.TarasDataService
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Data layer implementation of DriverRepository.
 * Manages caching, remote API communication, and Room DB persistence.
 */
class DriverRepositoryImpl(
    private val dataService: TarasDataService,
    private val topThreeDriversDao: TopThreeDriversDAO
) : DriverRepository {

    private val mutex = Mutex()
    private var cachedDrivers: List<DriverDetailResponseV2>? = null

    override suspend fun getDrivers(forceRefresh: Boolean): List<DriverDetailResponseV2> {
        if (!forceRefresh) {
            cachedDrivers?.let { return it }
        }
        return mutex.withLock {
            if (!forceRefresh) {
                cachedDrivers?.let { return it }
            }
            val remoteData = dataService.getDrivers()
            cachedDrivers = remoteData
            remoteData
        }
    }

    override fun getTopThreeDrivers(): Flow<List<TopThreeDriversEntity>> {
        return topThreeDriversDao.getAll()
    }

    override suspend fun saveTopThreeDrivers(drivers: List<TopThreeDriversEntity>) {
        if (drivers.isNotEmpty()) {
            topThreeDriversDao.insertAll(*drivers.toTypedArray())
        }
    }

    override fun clearCache() {
        cachedDrivers = null
    }
}
