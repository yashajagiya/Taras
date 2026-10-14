package com.example.taras.domain.usecase

import com.example.taras.domain.repository.LiveTimingRepository

/**
 * Clean Architecture Use Case providing reactive live timing & timeline streams.
 */
class GetLiveTimingUseCase(
    private val repository: LiveTimingRepository
) {
    val liveTimingRepository: LiveTimingRepository get() = repository

    fun refreshLiveFeed() {
        repository.refreshLiveFeed()
    }
}
