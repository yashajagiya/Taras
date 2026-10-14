package com.example.taras.data.repository

import com.example.taras.core.repository.F1LiveRepository
import com.example.taras.domain.repository.LiveTimingRepository
import com.example.taras.network_calls.live.LiveConnectionState
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TrackStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * Data layer implementation of LiveTimingRepository backed by F1LiveRepository.
 */
class LiveTimingRepositoryImpl(
    private val liveRepository: F1LiveRepository = F1LiveRepository.instance
) : LiveTimingRepository {

    override val discoveryData: StateFlow<LiveDiscoveryResponse?>
        get() = liveRepository.discoveryData

    override val isLiveSession: StateFlow<Boolean>
        get() = liveRepository.isLiveSession

    override val isLiveTimingActive: StateFlow<Boolean>
        get() = liveRepository.isLiveTimingActive

    override val isLiveTimelineActive: StateFlow<Boolean>
        get() = liveRepository.isLiveTimelineActive

    override val overview: StateFlow<TimelineOverview?>
        get() = liveRepository.overview

    override val timingSnapshot: StateFlow<RaceSnapshot?>
        get() = liveRepository.timingSnapshot

    override val timelineItems: StateFlow<ImmutableList<TimelineItem>>
        get() = liveRepository.timelineItems

    override val timingConnectionState: StateFlow<LiveConnectionState>
        get() = liveRepository.timingConnectionState

    override val timelineConnectionState: StateFlow<LiveConnectionState>
        get() = liveRepository.timelineConnectionState

    override val trackStatus: StateFlow<TrackStatus>
        get() = liveRepository.trackStatus

    override fun refreshLiveFeed() {
        liveRepository.refreshLiveFeed()
    }
}
