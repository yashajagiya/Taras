package com.example.taras.network_calls.rss

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.parser.Parser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object RssParser {

    private val rfc822Patterns = listOf(
        "EEE, dd MMM yyyy HH:mm:ss Z",
        "EEE, dd MMM yyyy HH:mm:ss zzz",
        "EEE, d MMM yyyy HH:mm:ss Z",
        "EEE, d MMM yyyy HH:mm:ss zzz",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss"
    )

    fun parsePubDate(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return 0L
        val trimmed = dateStr.trim()
        for (pattern in rfc822Patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.US).apply {
                    if (pattern.endsWith("'Z'")) {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                }
                val date = format.parse(trimmed)
                if (date != null) return date.time
            } catch (_: Exception) {
            }
        }
        return 0L
    }

    fun formatRelativeTime(millis: Long, now: Long = System.currentTimeMillis()): String {
        if (millis <= 0L) return ""
        val diff = now - millis
        return when {
            diff < 0L -> "Just now"
            diff < 60_000L -> "Just now"
            diff < 3600_000L -> "${diff / 60_000L}m ago"
            diff < 86400_000L -> "${diff / 3600_000L}h ago"
            diff < 7 * 86400_000L -> "${diff / 86400_000L}d ago"
            else -> {
                try {
                    val fmt = SimpleDateFormat("MMM d", Locale.getDefault())
                    fmt.format(Date(millis))
                } catch (_: Exception) {
                    ""
                }
            }
        }
    }

    fun cleanHtml(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        val text = try {
            Ksoup.parse(raw).text()
        } catch (_: Exception) {
            raw
        }
        return text
            .replace(Regex("""\s*(Keep reading|Continue reading\.{0,3}|Read more\.{0,3}|Read the full article).*${'$'}""", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    /**
     * Strips thumbnail size suffixes (e.g. -120x120, -150x150, -300x200, -768x432, -1024x576)
     * so that feeds like PlanetF1 (which default to 120x120 thumbnails in RSS) load the crystal clear original full HD photo.
     */
    fun upgradeImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url.replace(Regex("""-\d+x\d+(\.[a-zA-Z]+)$"""), "$1")
    }

    fun parseXml(xmlContent: String, source: NewsSource): List<RssItem> {
        val doc = Ksoup.parse(xmlContent, parser = Parser.xmlParser())
        return parseDocument(doc, source)
    }

    fun parseDocument(doc: Document, source: NewsSource): List<RssItem> {
        val channelImage = doc.selectFirst("channel > image > url")?.text()?.takeIf { it.isNotBlank() }
            ?: doc.selectFirst("image > url")?.text()?.takeIf { it.isNotBlank() }
        val channelPubDate = doc.selectFirst("channel > pubDate")?.text()
            ?: doc.selectFirst("channel > dc|date")?.text()
            ?: doc.getElementsByTag("dc:date").firstOrNull()?.text()
        val channelTimestamp = parsePubDate(channelPubDate)

        val items = doc.select("item")
        val now = System.currentTimeMillis()

        return items.mapIndexed { index, item ->
            parseItem(item, source, channelImage, channelTimestamp, now, index)
        }
    }

    private fun parseItem(
        item: Element,
        source: NewsSource,
        channelImage: String?,
        channelTimestamp: Long,
        baseTime: Long,
        index: Int
    ): RssItem {
        val title = item.selectFirst("title")?.text()?.trim()

        val link = item.selectFirst("link")?.text()?.takeIf { it.isNotBlank() }
            ?: item.selectFirst("link")?.attr("href")?.takeIf { it.isNotBlank() }
            ?: item.selectFirst("guid")?.text()?.takeIf { it.startsWith("http") }

        val rawDesc = item.selectFirst("description")?.text()
        val cleanDesc = cleanHtml(rawDesc)

        val rawImage = extractRawImage(item, channelImage, source.defaultImageUrl)
        val image = upgradeImageUrl(rawImage) ?: source.defaultImageUrl

        val rawPubDate = item.selectFirst("pubDate")?.text()
            ?: item.selectFirst("dc|date")?.text()
            ?: item.getElementsByTag("dc:date").firstOrNull()?.text()

        var timestamp = parsePubDate(rawPubDate)
        if (timestamp == 0L) {
            timestamp = if (channelTimestamp > 0L) {
                channelTimestamp - (index * 60_000L)
            } else {
                baseTime - (index * 60_000L)
            }
        }

        val relativeTime = formatRelativeTime(timestamp)

        val author = item.selectFirst("dc|creator")?.text()
            ?: item.getElementsByTag("dc:creator").firstOrNull()?.text()
            ?: item.selectFirst("author")?.text()

        return RssItem(
            title = title,
            link = link,
            description = cleanDesc,
            image = image,
            pubDate = rawPubDate,
            sourceId = source.id,
            sourceName = source.name,
            sourceColor = source.brandColorHex,
            timestamp = timestamp,
            relativeTime = relativeTime,
            author = author?.trim()
        )
    }

    private fun extractRawImage(
        item: Element,
        channelImage: String?,
        sourceDefaultImage: String?
    ): String? {
        // 1. Enclosure
        val enclosureUrl = item.selectFirst("enclosure")?.attr("url")?.takeIf { it.isNotBlank() }
        if (enclosureUrl != null) return enclosureUrl

        // 2. Media content (take the one with largest width or first available)
        val mediaElements = item.select("media|content, content") + item.getElementsByTag("media:content")
        var bestMediaUrl: String? = null
        var maxMediaWidth = 0
        for (el in mediaElements) {
            val url = el.attr("url").takeIf { it.isNotBlank() }
            if (url != null) {
                val width = el.attr("width").toIntOrNull() ?: 0
                if (bestMediaUrl == null || width > maxMediaWidth) {
                    bestMediaUrl = url
                    maxMediaWidth = width
                }
            }
        }
        if (bestMediaUrl != null) return bestMediaUrl

        // 3. Media thumbnail
        val thumbUrl = item.selectFirst("media|thumbnail")?.attr("url")?.takeIf { it.isNotBlank() }
            ?: item.getElementsByTag("media:thumbnail").firstOrNull()?.attr("url")?.takeIf { it.isNotBlank() }
        if (thumbUrl != null) return thumbUrl

        // 4. Direct image tag inside item
        val itemImage = item.selectFirst("image")?.text()?.takeIf { it.startsWith("http") }
            ?: item.selectFirst("image > url")?.text()?.takeIf { it.isNotBlank() }
        if (itemImage != null) return itemImage

        // 5. Embedded <img> tag inside <content:encoded> or <description>
        val encoded = item.selectFirst("content|encoded")?.text()
            ?: item.getElementsByTag("content:encoded").firstOrNull()?.text()
            ?: item.selectFirst("description")?.text()
        if (!encoded.isNullOrBlank()) {
            val imgMatch = Regex("""<img[^>]+src=["']([^"']+)["']""", RegexOption.IGNORE_CASE).find(encoded)
            val embeddedUrl = imgMatch?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
            if (embeddedUrl != null) return embeddedUrl
        }

        // 6. Channel fallback image
        if (!channelImage.isNullOrBlank()) return channelImage

        // 7. Source default image
        return sourceDefaultImage
    }

    /**
     * Parses WordPress REST API JSON (wp/v2/posts?_embed), extracting full-resolution featured media photos,
     * clean rendered titles, and excerpts. Used for outlets like RaceFans whose RSS XML omits photos.
     */
    fun parseWordPressJson(jsonString: String, source: NewsSource): List<RssItem> {
        val items = mutableListOf<RssItem>()
        try {
            val jsonArray = Json.parseToJsonElement(jsonString).jsonArray
            val now = System.currentTimeMillis()
            for ((i, element) in jsonArray.withIndex()) {
                val obj = element.jsonObject
                val titleRaw = obj["title"]?.jsonObject?.get("rendered")?.jsonPrimitive?.content
                val title = cleanHtml(titleRaw)
                val link = obj["link"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
                val rawDesc = obj["excerpt"]?.jsonObject?.get("rendered")?.jsonPrimitive?.content
                    ?: obj["content"]?.jsonObject?.get("rendered")?.jsonPrimitive?.content
                val cleanDesc = cleanHtml(rawDesc)

                // Extract featured image from _embedded -> wp:featuredmedia
                var imageUrl: String? = null
                val embedded = obj["_embedded"]?.jsonObject
                val mediaArray = embedded?.get("wp:featuredmedia")?.jsonArray
                if (mediaArray != null && mediaArray.isNotEmpty()) {
                    val mediaObj = mediaArray[0].jsonObject
                    val mediaDetails = mediaObj["media_details"]?.jsonObject
                    val sizes = mediaDetails?.get("sizes")?.jsonObject
                    imageUrl = sizes?.get("full")?.jsonObject?.get("source_url")?.jsonPrimitive?.content
                        ?: sizes?.get("post-thumbnail")?.jsonObject?.get("source_url")?.jsonPrimitive?.content
                        ?: sizes?.get("medium_large")?.jsonObject?.get("source_url")?.jsonPrimitive?.content
                        ?: mediaObj["source_url"]?.jsonPrimitive?.content
                }

                val finalImage = upgradeImageUrl(imageUrl) ?: source.defaultImageUrl

                val dateGmt = obj["date_gmt"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
                    ?: obj["date"]?.jsonPrimitive?.content
                val dateForParsing = if (dateGmt != null && dateGmt.endsWith("Z")) dateGmt else "${dateGmt}Z"
                val timestamp = parsePubDate(dateForParsing).takeIf { it > 0L }
                    ?: (now - i * 60_000L)
                val relativeTime = formatRelativeTime(timestamp)

                val author = embedded?.get("author")?.jsonArray?.firstOrNull()?.jsonObject?.get("name")?.jsonPrimitive?.content
                    ?: "RaceFans"

                items.add(
                    RssItem(
                        title = title,
                        link = link,
                        description = cleanDesc,
                        image = finalImage,
                        pubDate = dateGmt,
                        sourceId = source.id,
                        sourceName = source.name,
                        sourceColor = source.brandColorHex,
                        timestamp = timestamp,
                        relativeTime = relativeTime,
                        author = author
                    )
                )
            }
        } catch (_: Exception) {
        }
        return items
    }
}
