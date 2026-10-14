package com.example.taras.network_calls.live

import android.util.Log
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineWebSocketEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.time.Duration.Companion.milliseconds

class F1TimelineWebSocketClient(
    private val client: OkHttpClient = NetworkModule.okHttpClient,
    private val restClient: F1LiveRestClient = F1LiveRestClient(client)
) {
    private val tag = "F1TimelineWS"
    private val json = NetworkModule.json
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var activeWebSocket: WebSocket? = null
    private var activeWsUrl: String? = null
    private var activeRestBaseUrl: String? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0
    private var isIntentionalDisconnect = false

    private val itemsMap = LinkedHashMap<String, TimelineItem>()

    private val _itemsFlow = MutableStateFlow<ImmutableList<TimelineItem>>(persistentListOf())
    val itemsFlow = _itemsFlow.asStateFlow()

    private val _latestItemEvent = MutableSharedFlow<TimelineItem>(extraBufferCapacity = 64)
    val latestItemEvent = _latestItemEvent.asSharedFlow()

    private val _connectionState = MutableStateFlow(LiveConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _lastMessageTimestamp = MutableStateFlow(0L)
    val lastMessageTimestamp = _lastMessageTimestamp.asStateFlow()

    fun connect(wsUrl: String, restBaseUrl: String?) {
        if (wsUrl.isBlank()) {
            disconnect()
            return
        }

        if (activeWebSocket != null && activeWsUrl == wsUrl && _connectionState.value == LiveConnectionState.CONNECTED) {
            return
        }

        disconnect()
        isIntentionalDisconnect = false
        activeWsUrl = wsUrl
        activeRestBaseUrl = restBaseUrl
        _connectionState.value = LiveConnectionState.CONNECTING
        reconnectAttempts = 0

        initiateConnection(wsUrl)
    }

    private fun initiateConnection(wsUrl: String) {
        val request = Request.Builder().url(wsUrl).build()

        activeWebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "Timeline WebSocket connected to $wsUrl")
                _connectionState.value = LiveConnectionState.CONNECTED
                val hadPreviousItems = itemsMap.isNotEmpty()
                val lastId = itemsMap.keys.lastOrNull()
                reconnectAttempts = 0

                // If reconnecting with existing items, perform delta catch-up via REST
                if (hadPreviousItems && lastId != null && !activeRestBaseUrl.isNullOrBlank()) {
                    performDeltaCatchUp(activeRestBaseUrl!!, lastId)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val event = json.decodeFromString<TimelineWebSocketEvent>(text)
                    handleEvent(event)
                } catch (e: Exception) {
                    Log.w(tag, "Failed to decode timeline event: ${e.message}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "Timeline WebSocket closed: $code $reason")
                _connectionState.value = LiveConnectionState.DISCONNECTED
                if (!isIntentionalDisconnect) {
                    scheduleReconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "Timeline WebSocket error: ${t.message}")
                _connectionState.value = LiveConnectionState.DISCONNECTED
                if (!isIntentionalDisconnect) {
                    scheduleReconnect()
                }
            }
        })
    }

    private fun handleEvent(event: TimelineWebSocketEvent) {
        synchronized(itemsMap) {
            when (event.event) {
                "INIT_SNAPSHOT" -> {
                    event.snapshot?.items?.let { newItems ->
                        itemsMap.clear()
                        newItems.forEach { itemsMap[it.id] = it }
                    }
                }
                "NEW_ITEM" -> {
                    event.item?.let { newItem ->
                        itemsMap[newItem.id] = newItem
                        _latestItemEvent.tryEmit(newItem)
                    }
                }
                "UPDATE_ITEM" -> {
                    event.item?.let { updated ->
                        itemsMap[updated.id] = updated
                    }
                }
                "DELETE_ITEM" -> {
                    event.item?.id?.let { id ->
                        itemsMap.remove(id)
                    }
                }
            }
            // Sort by timestamp descending (newest first for live commentary feed)
            val sortedList = itemsMap.values
                .sortedByDescending { it.timestampMs }
                .toImmutableList()
            _itemsFlow.value = sortedList
            _lastMessageTimestamp.value = System.currentTimeMillis()
        }
    }

    private fun performDeltaCatchUp(baseUrl: String, lastId: String) {
        scope.launch {
            try {
                val deltaResult = restClient.fetchTimelineDelta(baseUrl, lastId)
                deltaResult.onSuccess { deltaList ->
                    if (deltaList.isNotEmpty()) {
                        Log.d(tag, "Caught up on ${deltaList.size} missed timeline items")
                        synchronized(itemsMap) {
                            deltaList.forEach { itemsMap[it.id] = it }
                            _itemsFlow.value = itemsMap.values
                                .sortedByDescending { it.timestampMs }
                                .toImmutableList()
                            _lastMessageTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Delta catch-up failed: ${e.message}")
            }
        }
    }

    private fun scheduleReconnect() {
        val targetUrl = activeWsUrl ?: return
        reconnectJob?.cancel()
        _connectionState.value = LiveConnectionState.RECONNECTING

        reconnectJob = scope.launch {
            reconnectAttempts++
            val backoffMs = (1000L * (1 shl (reconnectAttempts.coerceAtMost(4)))).coerceAtMost(15000L)
            Log.d(tag, "Reconnecting timeline socket in ${backoffMs}ms (attempt $reconnectAttempts)...")
            delay(backoffMs.milliseconds)
            if (!isIntentionalDisconnect && activeWsUrl == targetUrl) {
                initiateConnection(targetUrl)
            }
        }
    }

    fun populateInitialSnapshot(items: List<TimelineItem>) {
        synchronized(itemsMap) {
            items.forEach { itemsMap[it.id] = it }
            _itemsFlow.value = itemsMap.values
                .sortedByDescending { it.timestampMs }
                .toImmutableList()
            _lastMessageTimestamp.value = System.currentTimeMillis()
        }
    }

    fun clearItems() {
        synchronized(itemsMap) {
            itemsMap.clear()
            _itemsFlow.value = persistentListOf()
        }
    }

    fun disconnect() {
        isIntentionalDisconnect = true
        reconnectJob?.cancel()
        reconnectJob = null
        activeWebSocket?.close(1000, "Normal Closure")
        activeWebSocket = null
        activeWsUrl = null
        activeRestBaseUrl = null
        _connectionState.value = LiveConnectionState.DISCONNECTED
    }
}
