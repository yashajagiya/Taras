package com.example.taras.domain.usecase

import com.example.taras.domain.repository.NewsRepository
import com.example.taras.network_calls.rss.RssItem

/**
 * Clean Architecture Use Case to fetch F1 RSS news articles.
 */
class GetNewsUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<RssItem>> {
        return repository.getNews(forceRefresh)
    }
}
