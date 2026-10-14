package com.example.taras.network_calls.live

import android.util.Log
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.time.Duration.Companion.milliseconds

enum class LiveConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING
}

class F1TimingWebSocketClient(
    private val client: OkHttpClient = NetworkModule.okHttpClient
) {
    private val tag = "F1TimingWS"
    private val json = NetworkModule.json
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var activeWebSocket: WebSocket? = null
    private var activeUrl: String? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0
    private var isIntentionalDisconnect = false

    private val _connectionState = MutableStateFlow(LiveConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _snapshot = MutableStateFlow<RaceSnapshot?>(null)
    val snapshot = _snapshot.asStateFlow()

    private val _lastMessageTimestamp = MutableStateFlow<Long>(0L)
    val lastMessageTimestamp = _lastMessageTimestamp.asStateFlow()

    fun connect(url: String) {
        if (url.isBlank()) {
            disconnect()
            return
        }

        if (activeWebSocket != null && activeUrl == url && _connectionState.value == LiveConnectionState.CONNECTED) {
            return
        }

        disconnect()
        isIntentionalDisconnect = false
        activeUrl = url
        _connectionState.value = LiveConnectionState.CONNECTING
        reconnectAttempts = 0

        initiateConnection(url)
    }

    private fun initiateConnection(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        activeWebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "Timing WebSocket connected to $url")
                _connectionState.value = LiveConnectionState.CONNECTED
                reconnectAttempts = 0
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val parsedSnapshot = json.decodeFromString<RaceSnapshot>(text)
                    _snapshot.value = parsedSnapshot
                    _lastMessageTimestamp.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.w(tag, "Failed to decode timing snapshot: ${e.message}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "Timing WebSocket closed: $code $reason")
                _connectionState.value = LiveConnectionState.DISCONNECTED
                if (!isIntentionalDisconnect) {
                    scheduleReconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "Timing WebSocket error: ${t.message}")
                _connectionState.value = LiveConnectionState.DISCONNECTED
                if (!isIntentionalDisconnect) {
                    scheduleReconnect()
                }
            }
        })
    }

    private fun scheduleReconnect() {
        val targetUrl = activeUrl ?: return
        reconnectJob?.cancel()
        _connectionState.value = LiveConnectionState.RECONNECTING

        reconnectJob = scope.launch {
            reconnectAttempts++
            val backoffMs = (1000L * (1 shl (reconnectAttempts.coerceAtMost(4)))).coerceAtMost(15000L)
            Log.d(tag, "Reconnecting timing socket in ${backoffMs}ms (attempt $reconnectAttempts)...")
            delay(backoffMs.milliseconds)
            if (!isIntentionalDisconnect && activeUrl == targetUrl) {
                initiateConnection(targetUrl)
            }
        }
    }

    fun updateSnapshotDirectly(newSnapshot: RaceSnapshot) {
        _snapshot.value = newSnapshot
        _lastMessageTimestamp.value = System.currentTimeMillis()
    }

    fun disconnect() {
        isIntentionalDisconnect = true
        reconnectJob?.cancel()
        reconnectJob = null
        activeWebSocket?.close(1000, "Normal Closure")
        activeWebSocket = null
        activeUrl = null
        _connectionState.value = LiveConnectionState.DISCONNECTED
    }
}
