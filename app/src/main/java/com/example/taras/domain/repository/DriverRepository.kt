package com.example.taras.domain.repository

import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture repository contract for Driver data.
 */
interface DriverRepository {
    suspend fun getDrivers(forceRefresh: Boolean = false): List<DriverDetailResponseV2>
    fun getTopThreeDrivers(): Flow<List<TopThreeDriversEntity>>
    suspend fun saveTopThreeDrivers(drivers: List<TopThreeDriversEntity>)
    fun clearCache()
}
