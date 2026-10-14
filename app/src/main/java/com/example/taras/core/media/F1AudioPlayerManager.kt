package com.example.taras.core.media

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

enum class AudioPlaybackState {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

class F1AudioPlayerManager private constructor() {
    companion object {
        val instance: F1AudioPlayerManager by lazy { F1AudioPlayerManager() }
    }

    private val tag = "F1AudioPlayer"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var mediaPlayer: MediaPlayer? = null
    private var progressTrackerJob: Job? = null

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId = _currentlyPlayingId.asStateFlow()

    private val _playbackState = MutableStateFlow(AudioPlaybackState.IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _progressMs = MutableStateFlow(0L)
    val progressMs = _progressMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs = _durationMs.asStateFlow()

    fun playOrPause(itemId: String, audioUrl: String, baseUrl: String? = null) {
        val fullUrl = resolveAudioUrl(audioUrl, baseUrl)
        if (fullUrl.isBlank()) {
            Log.w(tag, "Cannot play audio: resolved URL is empty")
            return
        }

        if (_currentlyPlayingId.value == itemId) {
            val player = mediaPlayer
            if (player != null && player.isPlaying) {
                player.pause()
                _playbackState.value = AudioPlaybackState.PAUSED
                stopProgressTracker()
            } else if (player != null) {
                player.start()
                _playbackState.value = AudioPlaybackState.PLAYING
                startProgressTracker()
            }
            return
        }

        // Start new audio playback
        stop()
        _currentlyPlayingId.value = itemId
        _playbackState.value = AudioPlaybackState.BUFFERING
        _progressMs.value = 0L

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(fullUrl)
                setOnPreparedListener { mp ->
                    _durationMs.value = mp.duration.toLong()
                    mp.start()
                    _playbackState.value = AudioPlaybackState.PLAYING
                    startProgressTracker()
                }
                setOnCompletionListener {
                    stop()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(tag, "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = AudioPlaybackState.ERROR
                    stop()
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(tag, "Failed to start media player for $fullUrl: ${e.message}")
            _playbackState.value = AudioPlaybackState.ERROR
            stop()
        }
    }

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _progressMs.value = player.currentPosition.toLong()
                    }
                }
                delay(200L.milliseconds)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.w(tag, "Error releasing media player: ${e.message}")
        }
        mediaPlayer = null
        _currentlyPlayingId.value = null
        _playbackState.value = AudioPlaybackState.IDLE
        _progressMs.value = 0L
    }

    private fun resolveAudioUrl(url: String, baseUrl: String?): String {
        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            !baseUrl.isNullOrBlank() -> baseUrl.trimEnd('/') + "/" + url.trimStart('/')
            else -> url
        }
    }
}
