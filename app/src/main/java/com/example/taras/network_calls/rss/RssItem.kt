package com.example.taras.network_calls.rss

import androidx.compose.runtime.Immutable

@Immutable
data class RssItem(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val image: String? = null,
    val pubDate: String? = null
)