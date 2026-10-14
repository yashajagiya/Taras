package com.example.taras.domain.usecase

import com.example.taras.domain.repository.ResultsRepository

/**
 * Clean Architecture Use Case to calculate qualifying position map for live delta comparison.
 */
class GetLatestQualifyingPositionsUseCase(
    private val resultsRepository: ResultsRepository
) {
    suspend operator fun invoke(): Map<String, Int> {
        return try {
            val latestResults = resultsRepository.getLatestResults()
            val qualyList = latestResults.sessions.qualifying ?: emptyList()
            val map = mutableMapOf<String, Int>()
            qualyList.forEach { q ->
                val pos = q.position.toIntOrNull() ?: 0
                if (pos > 0) {
                    q.driverNumber?.let { map[it.toString()] = pos }
                    if (q.driverName.isNotBlank()) {
                        map[q.driverName.lowercase()] = pos
                        val lastName = q.driverName.substringAfterLast(" ").lowercase()
                        map[lastName] = pos
                    }
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
