package com.example.taras

import com.example.taras.network_calls.rss.NewsSources
import com.example.taras.network_calls.rss.RssParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RssParserTest {

    @Test
    fun testParsePlanetF1FeedWithUpgradedImage() {
        val planetXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0">
              <channel>
                <title>PlanetF1</title>
                <item>
                  <title>Winners and losers from Qualifying</title>
                  <link>https://www.planetf1.com/features/winners-and-losers</link>
                  <description><![CDATA[Max Verstappen and Mercedes make it onto the list.]]></description>
                  <pubDate>Sat, 03 Oct 2026 17:48:55 +0000</pubDate>
                  <enclosure url="https://d3cm515ijfiu6w.cloudfront.net/wp-content/uploads/2026/10/03181930/winners-losers-120x120.jpg" type="image/jpeg" />
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val items = RssParser.parseXml(planetXml, NewsSources.PLANET_F1)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("Winners and losers from Qualifying", item.title)
        assertEquals("https://www.planetf1.com/features/winners-and-losers", item.link)
        assertEquals("PlanetF1", item.sourceName)
        assertEquals("planetf1", item.sourceId)
        // Verify that the blurry -120x120 thumbnail suffix was automatically removed to load the high-res original!
        assertEquals("https://d3cm515ijfiu6w.cloudfront.net/wp-content/uploads/2026/10/03181930/winners-losers.jpg", item.image)
        assertTrue(item.timestamp > 0L)
    }

    @Test
    fun testUpgradeImageUrl() {
        val planetF1Thumbnail = "https://d3cm515ijfiu6w.cloudfront.net/wp-content/uploads/2026/10/03181930/winners-losers-2026-bahrain-grand-prix-malaysia-qualifying-120x120.jpg"
        val expectedHighRes = "https://d3cm515ijfiu6w.cloudfront.net/wp-content/uploads/2026/10/03181930/winners-losers-2026-bahrain-grand-prix-malaysia-qualifying.jpg"
        assertEquals(expectedHighRes, RssParser.upgradeImageUrl(planetF1Thumbnail))

        val otherThumbnail = "https://example.com/images/photo-300x200.png"
        assertEquals("https://example.com/images/photo.png", RssParser.upgradeImageUrl(otherThumbnail))

        val alreadyHighRes = "https://cdn-5.motorsport.com/images/amp/rain-falls-2.jpg"
        assertEquals(alreadyHighRes, RssParser.upgradeImageUrl(alreadyHighRes))
    }

    @Test
    fun testParseRaceFansWordPressJson() {
        val json = """
            [
              {
                "id": 568007,
                "date_gmt": "2026-10-03T12:19:50",
                "link": "https://www.racefans.net/2026/10/03/hamilton-interview/",
                "title": { "rendered": "&#8220;Frustrating sport&#8221; &#8211; Hamilton" },
                "excerpt": { "rendered": "<p>Lewis Hamilton was elated after qualifying.</p>" },
                "_embedded": {
                  "wp:featuredmedia": [
                    {
                      "source_url": "https://www.racefans.net/wp-content/uploads/2026/10/hamilton-full.jpg",
                      "media_details": {
                        "sizes": {
                          "full": {
                            "source_url": "https://www.racefans.net/wp-content/uploads/2026/10/hamilton-full.jpg"
                          }
                        }
                      }
                    }
                  ]
                }
              }
            ]
        """.trimIndent()

        val items = RssParser.parseWordPressJson(json, NewsSources.RACEFANS)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("“Frustrating sport” – Hamilton", item.title)
        assertEquals("Lewis Hamilton was elated after qualifying.", item.description)
        assertEquals("https://www.racefans.net/wp-content/uploads/2026/10/hamilton-full.jpg", item.image)
        assertEquals("RaceFans", item.sourceName)
        assertEquals("racefans", item.sourceId)
        assertTrue(item.timestamp > 0L)
    }

    @Test
    fun testParseAutosportFeed() {
        val autosportXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss xmlns:content="http://purl.org/rss/1.0/modules/content/" version="2.0">
              <channel>
                <title>Autosport.com - Formula 1</title>
                <item>
                  <title><![CDATA[Why Malaysia would be the perfect place for F1’s first wet race]]></title>
                  <link>https://www.autosport.com/f1/news/why-malaysia/10861397/</link>
                  <description><![CDATA[While Formula 1 has reached round 16...<br>Following safety concerns...<a class='more' href='#'>Keep reading</a>]]></description>
                  <pubDate>Sat, 03 Oct 2026 15:45:03 +0000</pubDate>
                  <enclosure url="https://cdn-5.motorsport.com/images/amp/rain-falls.jpg" type="image/jpeg" length="279028"/>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val items = RssParser.parseXml(autosportXml, NewsSources.AUTOSPORT)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("Why Malaysia would be the perfect place for F1’s first wet race", item.title)
        assertTrue(item.description?.contains("Keep reading") == false)
        assertEquals("https://cdn-5.motorsport.com/images/amp/rain-falls.jpg", item.image)
        assertEquals("Autosport", item.sourceName)
        assertTrue(item.timestamp > 0L)
    }

    @Test
    fun testParseTheGuardianFeed() {
        val guardianXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss xmlns:media="http://search.yahoo.com/mrss/" xmlns:dc="http://purl.org/dc/elements/1.1/" version="2.0">
              <channel>
                <title>Formula One | The Guardian</title>
                <item>
                  <title>Max Verstappen takes pole in Malaysia</title>
                  <link>https://www.theguardian.com/sport/2026/oct/03/max-verstappen-pole</link>
                  <description>&lt;p&gt;Verstappen finished on pole for the first time this season.&lt;/p&gt; &lt;a href="#"&gt;Continue reading...&lt;/a&gt;</description>
                  <pubDate>Sat, 03 Oct 2026 09:48:49 GMT</pubDate>
                  <media:content width="140" url="https://i.guim.co.uk/master/thumb.jpg" />
                  <media:content width="700" url="https://i.guim.co.uk/master/highres.jpg" />
                  <dc:creator>Giles Richards</dc:creator>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val items = RssParser.parseXml(guardianXml, NewsSources.THE_GUARDIAN)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("Max Verstappen takes pole in Malaysia", item.title)
        assertEquals("https://i.guim.co.uk/master/highres.jpg", item.image)
        assertEquals("The Guardian", item.sourceName)
        assertEquals("Giles Richards", item.author)
        assertTrue(item.description?.contains("Continue reading") == false)
    }

    @Test
    fun testParsePubDateAndRelativeTime() {
        val dateStr = "Sat, 03 Oct 2026 15:45:03 +0000"
        val millis = RssParser.parsePubDate(dateStr)
        assertTrue("Parsed millis should be positive", millis > 0L)

        val oneHourLater = millis + 3600_000L
        val relative = RssParser.formatRelativeTime(millis, now = oneHourLater)
        assertEquals("1h ago", relative)

        val thirtyMinsLater = millis + 30 * 60_000L
        assertEquals("30m ago", RssParser.formatRelativeTime(millis, now = thirtyMinsLater))
    }

    @Test
    fun testCleanHtmlDescription() {
        val raw = "<p>Hamilton finished second behind Verstappen.</p><br><a href='http://x.com'>Keep reading this story</a>"
        val clean = RssParser.cleanHtml(raw)
        assertEquals("Hamilton finished second behind Verstappen.", clean)
    }

    @Test
    fun testChronologicalOrdering() {
        val olderItem = RssParser.parseXml("""
            <rss version="2.0">
              <channel>
                <item>
                  <title>Older Article</title>
                  <pubDate>Fri, 02 Oct 2026 10:00:00 +0000</pubDate>
                </item>
              </channel>
            </rss>
        """.trimIndent(), NewsSources.THE_RACE)

        val newerItem = RssParser.parseXml("""
            <rss version="2.0">
              <channel>
                <item>
                  <title>Newer Article</title>
                  <pubDate>Sat, 03 Oct 2026 10:00:00 +0000</pubDate>
                </item>
              </channel>
            </rss>
        """.trimIndent(), NewsSources.AUTOSPORT)

        val combined = (olderItem + newerItem).sortedByDescending { it.timestamp }
        assertEquals("Newer Article", combined[0].title)
        assertEquals("Older Article", combined[1].title)
    }
}
