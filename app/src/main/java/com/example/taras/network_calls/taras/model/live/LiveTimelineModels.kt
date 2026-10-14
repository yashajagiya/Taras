package com.example.taras.network_calls.taras.model.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TimelineItemType {
    @SerialName("EDITORIAL")
    EDITORIAL,
    @SerialName("COMMENTARY")
    COMMENTARY,
    @SerialName("QUOTE")
    QUOTE,
    @SerialName("TEAM_RADIO")
    TEAM_RADIO,
    @SerialName("INCIDENT")
    INCIDENT,
    @SerialName("SAFETY_CAR")
    SAFETY_CAR,
    @SerialName("RED_FLAG")
    RED_FLAG,
    @SerialName("CHEQUERED_FLAG")
    CHEQUERED_FLAG,
    @SerialName("WEATHER")
    WEATHER,
    @SerialName("POLL_OR_STAT")
    POLL_OR_STAT
}

@Serializable
data class TimelineItem(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: TimelineItemType = TimelineItemType.COMMENTARY,
    @SerialName("kind")
    val kind: String = "general",
    @SerialName("uiType")
    val uiType: String = "commentary",
    @SerialName("isRadio")
    val isRadio: Boolean = false,
    @SerialName("isQuote")
    val isQuote: Boolean = false,
    @SerialName("isGeneral")
    val isGeneral: Boolean = true,
    @SerialName("isFlag")
    val isFlag: Boolean = false,
    @SerialName("hasAudio")
    val hasAudio: Boolean = false,
    @SerialName("hasImage")
    val hasImage: Boolean = false,
    @SerialName("title")
    val title: String? = null,
    @SerialName("text")
    val text: String = "",
    @SerialName("author")
    val author: String? = null,
    @SerialName("authorRole")
    val authorRole: String? = null,
    @SerialName("driverTla")
    val driverTla: String? = null,
    @SerialName("teamColor")
    val teamColor: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("audioUrl")
    val audioUrl: String? = null,
    @SerialName("lap")
    val lap: String? = null,
    @SerialName("timestampFormatted")
    val timestampFormatted: String? = null,
    @SerialName("timestampMs")
    val timestampMs: Long = 0L,
    @SerialName("tags")
    val tags: List<String> = emptyList()
)

@Serializable
data class TimelineSnapshot(
    @SerialName("type")
    val type: String = "TIMELINE_SNAPSHOT",
    @SerialName("grandPrix")
    val grandPrix: String = "",
    @SerialName("session")
    val session: String = "",
    @SerialName("totalCount")
    val totalCount: Int = 0,
    @SerialName("items")
    val items: List<TimelineItem> = emptyList(),
    @SerialName("updatedAt")
    val updatedAt: Long = 0L
)

@Serializable
data class TimelineWebSocketEvent(
    @SerialName("event")
    val event: String = "NEW_ITEM",
    @SerialName("itemKind")
    val itemKind: String? = null,
    @SerialName("uiType")
    val uiType: String? = null,
    @SerialName("item")
    val item: TimelineItem? = null,
    @SerialName("snapshot")
    val snapshot: TimelineSnapshot? = null
)

@Serializable
data class TimelineOverview(
    @SerialName("grandPrix")
    val grandPrix: String = "",
    @SerialName("session")
    val session: String = "",
    @SerialName("isSessionActive")
    val isSessionActive: Boolean = true,
    @SerialName("totalUpdates")
    val totalUpdates: Int = 0,
    @SerialName("totalRadioMessages")
    val totalRadioMessages: Int = 0,
    @SerialName("totalIncidents")
    val totalIncidents: Int = 0,
    @SerialName("totalQuotes")
    val totalQuotes: Int = 0,
    @SerialName("totalGeneral")
    val totalGeneral: Int = 0,
    @SerialName("podiumOrLeader")
    val podiumOrLeader: List<String> = emptyList(),
    @SerialName("keyHighlights")
    val keyHighlights: List<TimelineItem> = emptyList(),
    @SerialName("latestUpdateTimestamp")
    val latestUpdateTimestamp: Long = 0L
)
