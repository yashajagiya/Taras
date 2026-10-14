package com.example.taras.core.repository

import android.util.Log
import com.example.taras.network_calls.live.F1LiveRestClient
import com.example.taras.network_calls.live.F1TimelineWebSocketClient
import com.example.taras.network_calls.live.F1TimingWebSocketClient
import com.example.taras.network_calls.live.LiveConnectionState
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TrackStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class F1LiveRepository private constructor(
    private val restClient: F1LiveRestClient = F1LiveRestClient(),
    private val timingWsClient: F1TimingWebSocketClient = F1TimingWebSocketClient(),
    private val timelineWsClient: F1TimelineWebSocketClient = F1TimelineWebSocketClient(restClient = restClient)
) {
    companion object {
        val instance: F1LiveRepository by lazy { F1LiveRepository() }
        const val DATA_STALENESS_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
    }

    private val tag = "F1LiveRepository"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var discoveryPollingJob: Job? = null
    private var freshnessMonitorJob: Job? = null

    private val _discoveryData = MutableStateFlow<LiveDiscoveryResponse?>(null)
    val discoveryData = _discoveryData.asStateFlow()

    private val _isLiveSession = MutableStateFlow(false)
    val isLiveSession = _isLiveSession.asStateFlow()

    private val _overview = MutableStateFlow<TimelineOverview?>(null)
    val overview = _overview.asStateFlow()

    val timingSnapshot: StateFlow<RaceSnapshot?> = timingWsClient.snapshot
    val timelineItems: StateFlow<ImmutableList<TimelineItem>> = timelineWsClient.itemsFlow

    val timingConnectionState: StateFlow<LiveConnectionState> = timingWsClient.connectionState
    val timelineConnectionState: StateFlow<LiveConnectionState> = timelineWsClient.connectionState

    val lastTimingTimestamp: StateFlow<Long> = timingWsClient.lastMessageTimestamp
    val lastTimelineTimestamp: StateFlow<Long> = timelineWsClient.lastMessageTimestamp

    private val _isLiveTimingActive = MutableStateFlow(false)
    val isLiveTimingActive = _isLiveTimingActive.asStateFlow()

    private val _isLiveTimelineActive = MutableStateFlow(false)
    val isLiveTimelineActive = _isLiveTimelineActive.asStateFlow()

    val trackStatus: StateFlow<TrackStatus> = timingSnapshot
        .map { it?.trackStatus ?: parseTrackStatus(_discoveryData.value?.trackStatus) }
        .stateIn(scope, SharingStarted.Eagerly, TrackStatus.GREEN)

    init {
        startDiscoveryLoop()
        startFreshnessMonitor()

        scope.launch {
            timingWsClient.connectionState.collect { state ->
                if (state == LiveConnectionState.CONNECTED) {
                    if (_isLiveSession.value) {
                        _isLiveTimingActive.value = true
                    }
                } else if (state != LiveConnectionState.RECONNECTING && _isLiveTimingActive.value) {
                    _isLiveTimingActive.value = false
                    Log.i(tag, "Timing WebSocket state changed to $state. Deactivated live timing.")
                }
            }
        }

        scope.launch {
            timelineWsClient.connectionState.collect { state ->
                if (state == LiveConnectionState.CONNECTED) {
                    if (_isLiveSession.value) {
                        _isLiveTimelineActive.value = true
                    }
                } else if (state != LiveConnectionState.RECONNECTING && _isLiveTimelineActive.value) {
                    _isLiveTimelineActive.value = false
                    Log.i(tag, "Timeline WebSocket state changed to $state. Deactivated live timeline.")
                }
            }
        }
    }

    private fun startFreshnessMonitor() {
        if (freshnessMonitorJob?.isActive == true) return
        freshnessMonitorJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val isSessionLive = _isLiveSession.value

                // Timing WebSocket: must be live session, connected, and data updated within 5 minutes
                val timingConnected = timingWsClient.connectionState.value == LiveConnectionState.CONNECTED
                val timingLast = timingWsClient.lastMessageTimestamp.value
                val timingUpdatedRecently = timingLast > 0L && (now - timingLast <= DATA_STALENESS_TIMEOUT_MS)
                val timingActive = isSessionLive && timingConnected && timingUpdatedRecently
                if (_isLiveTimingActive.value != timingActive) {
                    if (!timingActive) {
                        Log.i(tag, "Timing WebSocket inactive or stale (>5 min without updates). Falling back to github.io.")
                    } else {
                        Log.i(tag, "Timing WebSocket active and receiving live updates.")
                    }
                    _isLiveTimingActive.value = timingActive
                }

                // Timeline WebSocket: must be live session, connected, and data updated within 5 minutes
                val timelineConnected = timelineWsClient.connectionState.value == LiveConnectionState.CONNECTED
                val timelineLast = timelineWsClient.lastMessageTimestamp.value
                val timelineUpdatedRecently = timelineLast > 0L && (now - timelineLast <= DATA_STALENESS_TIMEOUT_MS)
                val timelineActive = isSessionLive && timelineConnected && timelineUpdatedRecently
                if (_isLiveTimelineActive.value != timelineActive) {
                    _isLiveTimelineActive.value = timelineActive
                }

                delay(5000L.milliseconds)
            }
        }
    }

    fun startDiscoveryLoop(pollIntervalMs: Long = 20000L) {
        if (discoveryPollingJob?.isActive == true) return

        discoveryPollingJob = scope.launch {
            while (isActive) {
                checkDiscovery()
                delay(pollIntervalMs.milliseconds)
            }
        }
    }

    suspend fun checkDiscovery() {
        val result = restClient.fetchDiscovery()
        result.onSuccess { discovery ->
            _discoveryData.value = discovery
            val wasLive = _isLiveSession.value
            val isNowLive = discovery.isLive

            _isLiveSession.value = isNowLive

            if (isNowLive) {
                val restBase = discovery.webUrl.ifBlank {
                    discovery.timelineRestUrl.substringBefore("/api/")
                }

                // 1. If becoming live or first start, fetch REST initial state
                if (!wasLive || timingSnapshot.value == null) {
                    bootstrapRestState(restBase)
                }

                // 2. Connect WebSockets
                val timingWs = discovery.wsTimingUrl.ifBlank { discovery.localWsUrl.orEmpty() }
                if (timingWs.isNotBlank()) {
                    timingWsClient.connect(timingWs)
                }
                val timelineWs = discovery.wsTimelineUrl.ifBlank { discovery.localWsTimelineUrl.orEmpty() }
                if (timelineWs.isNotBlank()) {
                    timelineWsClient.connect(timelineWs, restBase)
                }
            } else {
                if (wasLive) {
                    Log.d(tag, "Session concluded. Disconnecting sockets.")
                    timingWsClient.disconnect()
                    timelineWsClient.disconnect()
                }
            }
        }.onFailure {
            Log.w(tag, "Discovery check failed: ${it.message}")
        }
    }

    private suspend fun bootstrapRestState(baseUrl: String) {
        if (baseUrl.isBlank()) return

        // Fetch timing state
        restClient.fetchTimingState(baseUrl).onSuccess { snapshot ->
            timingWsClient.updateSnapshotDirectly(snapshot)
            if (_isLiveSession.value) {
                _isLiveTimingActive.value = true
            }
        }

        // Fetch timeline state
        restClient.fetchTimeline(baseUrl).onSuccess { snapshot ->
            timelineWsClient.populateInitialSnapshot(snapshot.items)
            if (_isLiveSession.value) {
                _isLiveTimelineActive.value = true
            }
        }

        // Fetch overview
        restClient.fetchTimelineOverview(baseUrl).onSuccess { overviewData ->
            _overview.value = overviewData
        }
    }

    fun refreshLiveFeed() {
        scope.launch {
            checkDiscovery()
        }
    }

    private fun parseTrackStatus(raw: String?): TrackStatus {
        return when (raw?.uppercase()) {
            "YELLOW" -> TrackStatus.YELLOW
            "SAFETY_CAR", "SC" -> TrackStatus.SAFETY_CAR
            "VIRTUAL_SAFETY_CAR", "VSC" -> TrackStatus.VIRTUAL_SAFETY_CAR
            "RED_FLAG", "RED" -> TrackStatus.RED_FLAG
            "FINISHED" -> TrackStatus.FINISHED
            else -> TrackStatus.GREEN
        }
    }
}
