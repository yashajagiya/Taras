package com.example.taras.data.repository

import com.example.taras.domain.repository.NewsRepository
import com.example.taras.network_calls.rss.RssItem
import com.example.taras.network_calls.rss.RssRepository

/**
 * Data layer implementation of NewsRepository wrapping RSS sources.
 */
class NewsRepositoryImpl(
    private val rssRepository: RssRepository = RssRepository()
) : NewsRepository {

    override suspend fun getNews(forceRefresh: Boolean): Result<List<RssItem>> {
        return rssRepository.getF1News()
    }
}
