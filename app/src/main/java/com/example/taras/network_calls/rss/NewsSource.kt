package com.example.taras.network_calls.rss

import androidx.compose.runtime.Immutable

@Immutable
data class NewsSource(
    val id: String,
    val name: String,
    val url: String,
    val brandColorHex: String,
    val defaultImageUrl: String? = null
)

object NewsSources {
    val ALL = NewsSource(
        id = "all",
        name = "All Sources",
        url = "",
        brandColorHex = "#E10600"
    )

    val AUTOSPORT = NewsSource(
        id = "autosport",
        name = "Autosport",
        url = "https://www.autosport.com/rss/f1/news/",
        brandColorHex = "#D32F2F",
        defaultImageUrl = "https://cdn-1.motorsport.com/images/amp/2wBoX7j0/s6/autosport-logo.jpg"
    )

    val MOTORSPORT = NewsSource(
        id = "motorsport",
        name = "Motorsport.com",
        url = "https://www.motorsport.com/rss/f1/news/",
        brandColorHex = "#FF1801",
        defaultImageUrl = "https://cdn-1.motorsport.com/images/amp/2wBoX7j0/s6/motorsport-logo.jpg"
    )

    val THE_GUARDIAN = NewsSource(
        id = "theguardian",
        name = "The Guardian",
        url = "https://www.theguardian.com/sport/formulaone/rss",
        brandColorHex = "#052962",
        defaultImageUrl = "https://assets.guim.co.uk/images/guardian-logo-rss.c45beb1bafa34b347ac333af2e6fe23f.png"
    )

    val PLANET_F1 = NewsSource(
        id = "planetf1",
        name = "PlanetF1",
        url = "https://www.planetf1.com/rss",
        brandColorHex = "#FF8F00",
        defaultImageUrl = "https://d3cm515ijfiu6w.cloudfront.net/wp-content/uploads/2023/03/01150000/planetf1-default.jpg"
    )

    val RACEFANS = NewsSource(
        id = "racefans",
        name = "RaceFans",
        url = "https://www.racefans.net/wp-json/wp/v2/posts?_embed&per_page=12",
        brandColorHex = "#1565C0",
        defaultImageUrl = "https://www.racefans.net/wp-content/themes/racefans/images/logo.png"
    )

    val THE_RACE = NewsSource(
        id = "the_race",
        name = "The Race",
        url = "https://the-race.com/feed/",
        brandColorHex = "#D81B60",
        defaultImageUrl = "https://the-race.com/wp-content/themes/the-race/assets/images/the-race-logo-dark.png"
    )

    val ALL_SOURCES: List<NewsSource> = listOf(
        AUTOSPORT,
        MOTORSPORT,
        THE_GUARDIAN,
        PLANET_F1,
        RACEFANS,
        THE_RACE
    )

    val FILTER_OPTIONS: List<NewsSource> = listOf(ALL) + ALL_SOURCES
}
