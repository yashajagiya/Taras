package com.example.taras.domain.repository

import com.example.taras.network_calls.rss.RssItem

/**
 * Clean Architecture repository contract for News operations.
 */
interface NewsRepository {
    suspend fun getNews(forceRefresh: Boolean = false): Result<List<RssItem>>
}
