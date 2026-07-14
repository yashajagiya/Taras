package com.example.taras.rss

data class RssItem(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val image: String? = null,
    val pubDate: String? = null
)