package com.example.taras.core.tcg.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object TcgSoundManager {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    /**
     * Synthesizes and plays a short scratch friction click.
     */
    fun playScratchTick() {
        audioScope.launch {
            playTone(frequency = 1200.0, durationMs = 15, volume = 0.25f)
        }
    }

    /**
     * Synthesizes and plays a celebratory metallic chime chord when a card is unlocked.
     */
    fun playRevealChime() {
        audioScope.launch {
            // Ascending major chord (C6, E6, G6, C7)
            val frequencies = doubleArrayOf(1046.50, 1318.51, 1567.98, 2093.00)
            for (freq in frequencies) {
                playTone(frequency = freq, durationMs = 120, volume = 0.5f)
            }
        }
    }

    /**
     * Synthesizes and plays a team radio transmission confirmation beep.
     */
    fun playRadioBeep() {
        audioScope.launch {
            playTone(frequency = 1760.0, durationMs = 80, volume = 0.4f)
        }
    }

    private suspend fun playTone(frequency: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                // Generate sine wave with linear fade-out envelope to avoid audio clicks
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val angle = 2.0 * Math.PI * i / (sampleRate / frequency)
                sample[i] = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, numSamples)
            audioTrack.play()
            kotlinx.coroutines.delay(durationMs + 60L)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        } catch (e: Exception) {
            // Audio failure should not crash gameplay
            e.printStackTrace()
        }
    }
}
