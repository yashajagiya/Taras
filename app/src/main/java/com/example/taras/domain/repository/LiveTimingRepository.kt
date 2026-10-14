package com.example.taras.domain.repository

import com.example.taras.network_calls.live.LiveConnectionState
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TrackStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * Clean Architecture repository contract for Live F1 Timing & Timeline feeds.
 */
interface LiveTimingRepository {
    val discoveryData: StateFlow<LiveDiscoveryResponse?>
    val isLiveSession: StateFlow<Boolean>
    val isLiveTimingActive: StateFlow<Boolean>
    val isLiveTimelineActive: StateFlow<Boolean>
    val overview: StateFlow<TimelineOverview?>
    val timingSnapshot: StateFlow<RaceSnapshot?>
    val timelineItems: StateFlow<ImmutableList<TimelineItem>>
    val timingConnectionState: StateFlow<LiveConnectionState>
    val timelineConnectionState: StateFlow<LiveConnectionState>
    val trackStatus: StateFlow<TrackStatus>

    fun refreshLiveFeed()
}
