package com.example.taras.rss

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem

class RssRepository {
    private val parser = RssParser()
    private val url = "https://www.motorsport.com/rss/f1/news/"

    suspend fun getF1News(): List<RssItem> {
        return try {
            val channel = parser.getRssChannel(url)
            channel.items
        } catch (e: Exception) {
            emptyList()
        }
    }
}