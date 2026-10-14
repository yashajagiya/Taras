package com.example.taras.network_calls.taras.model.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveDiscoveryResponse(
    @SerialName("season")
    val season: Int = 2026,
    @SerialName("round")
    val round: Int? = null,
    @SerialName("race")
    val race: String = "",
    @SerialName("grand_prix")
    val grandPrix: String = "",
    @SerialName("session")
    val session: String = "",
    @SerialName("track_status")
    val trackStatus: String = "GREEN",
    @SerialName("is_live")
    val isLive: Boolean = false,
    @SerialName("driver_count")
    val driverCount: Int = 22,
    @SerialName("leader")
    val leader: String = "",
    @SerialName("web_url")
    val webUrl: String = "",
    @SerialName("ws_timing_url")
    val wsTimingUrl: String = "",
    @SerialName("local_ws_url")
    val localWsUrl: String? = null,
    @SerialName("ws_timeline_url")
    val wsTimelineUrl: String = "",
    @SerialName("timeline_rest_url")
    val timelineRestUrl: String = "",
    @SerialName("local_ws_timeline_url")
    val localWsTimelineUrl: String? = null,
    @SerialName("updated_at")
    val updatedAt: String = ""
)
