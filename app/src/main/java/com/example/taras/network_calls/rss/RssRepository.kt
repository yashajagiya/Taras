package com.example.taras.network_calls.rss

import android.util.Log
import com.example.taras.network_calls.NetworkModule
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.parser.Parser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.Request

class RssRepository {

    private val logTag = "RssRepository"

    suspend fun getF1News(sources: List<NewsSource> = NewsSources.ALL_SOURCES): Result<List<RssItem>> =
        withContext(Dispatchers.IO) {
            val deferreds = sources.map { source ->
                async {
                    fetchSourceNews(source)
                }
            }

            val results = deferreds.awaitAll()
            val combined = results.flatten()

            if (combined.isEmpty()) {
                Result.failure(Exception("Failed to load news from all sources"))
            } else {
                val deduplicated = combined.distinctBy { it.link ?: it.title }
                val sorted = deduplicated.sortedByDescending { it.timestamp }
                Result.success(sorted)
            }
        }

    private suspend fun fetchSourceNews(source: NewsSource): List<RssItem> {
        return try {
            if (source.url.contains("wp-json")) {
                // WordPress REST API (e.g. RaceFans)
                val request = Request.Builder()
                    .url(source.url)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                    .build()
                val response = NetworkModule.okHttpClient.newCall(request).execute()
                val jsonString = response.body?.string() ?: ""
                if (response.isSuccessful && jsonString.isNotBlank()) {
                    val items = RssParser.parseWordPressJson(jsonString, source)
                    if (items.isNotEmpty()) return items
                }
                // Fallback to RSS feed if REST fails
                val fallbackUrl = "https://www.racefans.net/feed/"
                val doc = Ksoup.parseGetRequest(url = fallbackUrl, parser = Parser.xmlParser())
                enrichItemsWithArticleImages(RssParser.parseDocument(doc, source))
            } else {
                val doc = Ksoup.parseGetRequest(
                    url = source.url,
                    parser = Parser.xmlParser()
                )
                val items = RssParser.parseDocument(doc, source)
                enrichItemsWithArticleImages(items)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.w(logTag, "Failed to fetch RSS from ${source.name} (${source.url}): ${e.message}")
            emptyList()
        }
    }

    private suspend fun enrichItemsWithArticleImages(items: List<RssItem>): List<RssItem> =
        coroutineScope {
            items.map { item ->
                async {
                    if (item.link != null && (item.image == null || item.image.contains("logo") || item.image.contains("favicon"))) {
                        val ogImage = fetchOgImage(item.link)
                        if (!ogImage.isNullOrBlank()) {
                            item.copy(image = ogImage)
                        } else {
                            item
                        }
                    } else {
                        item
                    }
                }
            }.awaitAll()
        }

    private fun fetchOgImage(articleUrl: String): String? {
        return try {
            val request = Request.Builder()
                .url(articleUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                .build()
            val response = NetworkModule.okHttpClient.newCall(request).execute()
            val html = response.body?.string() ?: return null
            val doc = Ksoup.parse(html)
            val ogImage = doc.selectFirst("meta[property=og:image]")?.attr("content")
                ?: doc.selectFirst("meta[name=twitter:image]")?.attr("content")
            RssParser.upgradeImageUrl(ogImage)
        } catch (_: Exception) {
            null
        }
    }
}