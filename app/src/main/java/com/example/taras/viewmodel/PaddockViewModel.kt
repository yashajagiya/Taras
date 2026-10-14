package com.example.taras.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taras.core.common.TarasApplication
import com.example.taras.domain.repository.LiveTimingRepository
import com.example.taras.domain.usecase.GetLatestQualifyingPositionsUseCase
import com.example.taras.network_calls.live.LiveConnectionState
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TrackStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Presentation ViewModel for Paddock screen.
 * Follows Clean Architecture & MVVM by mediating live telemetry and qualifying baseline state.
 */
@Stable
class PaddockViewModel(
    private val getLatestQualifyingPositionsUseCase: GetLatestQualifyingPositionsUseCase =
        TarasApplication.instance.appContainer.getLatestQualifyingPositionsUseCase,
    private val liveTimingRepository: LiveTimingRepository =
        TarasApplication.instance.appContainer.liveTimingRepository
) : ViewModel() {

    val isLiveSession: StateFlow<Boolean> = liveTimingRepository.isLiveSession
    val isLiveTimingActive: StateFlow<Boolean> = liveTimingRepository.isLiveTimingActive
    val isLiveTimelineActive: StateFlow<Boolean> = liveTimingRepository.isLiveTimelineActive
    val timelineConnectionState: StateFlow<LiveConnectionState> = liveTimingRepository.timelineConnectionState
    val liveDiscovery: StateFlow<LiveDiscoveryResponse?> = liveTimingRepository.discoveryData
    val liveOverview: StateFlow<TimelineOverview?> = liveTimingRepository.overview
    val liveTimelineItems: StateFlow<ImmutableList<TimelineItem>> = liveTimingRepository.timelineItems
    val liveTrackStatus: StateFlow<TrackStatus> = liveTimingRepository.trackStatus
    val liveSnapshot: StateFlow<RaceSnapshot?> = liveTimingRepository.timingSnapshot

    val driverTyres: StateFlow<Map<String, String>?> = liveSnapshot
        .map { snapshot ->
            snapshot?.drivers?.associate { it.tla.uppercase() to it.tyre }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _qualifyingPositionsMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val qualifyingPositionsMap: StateFlow<Map<String, Int>> = _qualifyingPositionsMap.asStateFlow()

    init {
        loadQualifyingPositions()
    }

    fun loadQualifyingPositions() {
        viewModelScope.launch(Dispatchers.IO) {
            _qualifyingPositionsMap.value = getLatestQualifyingPositionsUseCase()
        }
    }

    fun refreshLiveFeed() {
        liveTimingRepository.refreshLiveFeed()
    }
}
