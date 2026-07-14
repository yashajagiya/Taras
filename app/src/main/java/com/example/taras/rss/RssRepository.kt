package com.example.taras.rss

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.parser.Parser

class RssRepository {
    private val url = "https://www.motorsport.com/rss/f1/news/"

    suspend fun getF1News(): List<RssItem> {
        return try {
            val doc = Ksoup.parseGetRequest(
                url = url,
                parser = Parser.xmlParser()
            )

            val items = doc.select("item")
            items.map { item ->
                RssItem(
                    title = item.selectFirst("title")?.text(),
                    link = item.selectFirst("link")?.text(),
                    description = item.selectFirst("description")?.text(),
                    image = item.selectFirst("enclosure")?.attr("url") 
                        ?: item.selectFirst("media|content")?.attr("url")
                        ?: item.selectFirst("image")?.text(),
                    pubDate = item.selectFirst("pubDate")?.text()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}