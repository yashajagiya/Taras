package com.example.taras.domain.usecase

import com.example.taras.core.db.TopThreeDriversEntity
import com.example.taras.domain.repository.DriverRepository
import com.example.taras.network_calls.taras.model.v2.DriverDetailResponseV2
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture Use Case to retrieve drivers and manage cached standings.
 */
class GetDriversUseCase(
    private val repository: DriverRepository
) {
    suspend fun getDrivers(forceRefresh: Boolean = false): List<DriverDetailResponseV2> {
        return repository.getDrivers(forceRefresh)
    }

    fun getTopThreeDrivers(): Flow<List<TopThreeDriversEntity>> {
        return repository.getTopThreeDrivers()
    }

    suspend fun saveTopThreeDrivers(drivers: List<TopThreeDriversEntity>) {
        repository.saveTopThreeDrivers(drivers)
    }
}
