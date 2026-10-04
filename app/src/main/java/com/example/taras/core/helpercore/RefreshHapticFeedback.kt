package com.example.taras.core.helpercore

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import android.util.Log
import androidx.compose.ui.platform.LocalView

object RefreshHapticFeedback {
    private const val TAG = "RefreshHaptic"

    /**
     * Crisp single tactile tick when refresh is triggered / pulled past threshold.
     */
    fun triggerStartHaptic(
        context: Context,
        view: View? = null,
        hapticFeedback: HapticFeedback? = null
    ) {
        Log.d(TAG, "Triggering start/threshold haptic tick (55ms)")
        vibrateSingle(
            context = context,
            view = view,
            hapticFeedback = hapticFeedback,
            durationMs = 55L,
            amplitude = 220
        )
    }

    /**
     * Satisfying double-pulse confirmation when refresh completes successfully.
     */
    fun triggerSuccessHaptic(
        context: Context,
        view: View? = null,
        hapticFeedback: HapticFeedback? = null
    ) {
        Log.d(TAG, "Triggering success haptic double pulse (50ms+70ms)")
        vibrateDoublePulse(
            context = context,
            view = view,
            hapticFeedback = hapticFeedback
        )
    }

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun vibrateSingle(
        context: Context,
        view: View?,
        hapticFeedback: HapticFeedback?,
        durationMs: Long,
        amplitude: Int
    ) {
        var hardwareSuccess = false

        // 1. Hardware Vibrator with AudioAttributes / VibrationAttributes
        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator.hasAmplitudeControl()) {
                        VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                    } else {
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            val vibrationAttributes = VibrationAttributes.Builder()
                                .setUsage(VibrationAttributes.USAGE_COMMUNICATION_REQUEST)
                                .build()
                            vibrator.vibrate(effect, vibrationAttributes)
                            hardwareSuccess = true
                        } catch (_: Exception) {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(effect, audioAttributes)
                            hardwareSuccess = true
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(effect, audioAttributes)
                        hardwareSuccess = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                    hardwareSuccess = true
                }
            }
        } catch (_: Exception) {}

        // 2. View Haptics with FLAG_IGNORE_VIEW_SETTING and FLAG_IGNORE_GLOBAL_SETTING
        try {
            if (view != null) {
                view.isHapticFeedbackEnabled = true
                @Suppress("DEPRECATION")
                val flags = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING

                val feedbackConstant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
                view.performHapticFeedback(feedbackConstant, flags)
            }
        } catch (_: Exception) {}

        // 3. Jetpack Compose Fallback
        if (!hardwareSuccess) {
            try {
                hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (_: Exception) {}
        }
    }

    private fun vibrateDoublePulse(
        context: Context,
        view: View?,
        hapticFeedback: HapticFeedback?
    ) {
        var hardwareSuccess = false

        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build()

                val timing = longArrayOf(0, 50, 60, 70)
                val amplitudes = intArrayOf(0, 190, 0, 255)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator.hasAmplitudeControl()) {
                        VibrationEffect.createWaveform(timing, amplitudes, -1)
                    } else {
                        VibrationEffect.createWaveform(timing, -1)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            val vibrationAttributes = VibrationAttributes.Builder()
                                .setUsage(VibrationAttributes.USAGE_COMMUNICATION_REQUEST)
                                .build()
                            vibrator.vibrate(effect, vibrationAttributes)
                            hardwareSuccess = true
                        } catch (_: Exception) {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(effect, audioAttributes)
                            hardwareSuccess = true
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(effect, audioAttributes)
                        hardwareSuccess = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(timing, -1)
                    hardwareSuccess = true
                }
            }
        } catch (_: Exception) {}

        try {
            if (view != null) {
                view.isHapticFeedbackEnabled = true
                @Suppress("DEPRECATION")
                val flags = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                val constant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
                view.performHapticFeedback(constant, flags)
            }
        } catch (_: Exception) {}

        if (!hardwareSuccess) {
            try {
                hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (_: Exception) {}
        }
    }
}

/**
 * Composable that observes [isRefreshing] (and optional [state]) and triggers:
 * 1. A crisp tactile tick when pulled past the threshold (distanceFraction >= 1.0f).
 * 2. A satisfying double-pulse confirmation when refreshing finishes (true -> false).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshHapticEffect(
    isRefreshing: Boolean,
    state: PullToRefreshState? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    var wasRefreshing by remember { mutableStateOf(false) }
    var isInitial by remember { mutableStateOf(true) }
    var hasTriggeredThresholdForCycle by remember { mutableStateOf(false) }

    // 1. Continuous tracking of drag distance fraction past the 1.0 threshold
    if (state != null) {
        LaunchedEffect(state) {
            snapshotFlow { state.distanceFraction }
                .collect { fraction ->
                    if (fraction >= 1.0f && !hasTriggeredThresholdForCycle && !isRefreshing) {
                        hasTriggeredThresholdForCycle = true
                        RefreshHapticFeedback.triggerStartHaptic(context, view, haptic)
                    } else if (fraction < 0.2f && !isRefreshing) {
                        hasTriggeredThresholdForCycle = false
                    }
                }
        }
    }

    // 2. Trigger on refresh start (if not already triggered at threshold) and refresh complete
    LaunchedEffect(isRefreshing) {
        if (isInitial) {
            isInitial = false
            wasRefreshing = isRefreshing
            return@LaunchedEffect
        }

        if (!wasRefreshing && isRefreshing) {
            if (!hasTriggeredThresholdForCycle) {
                hasTriggeredThresholdForCycle = true
                RefreshHapticFeedback.triggerStartHaptic(context, view, haptic)
            }
        } else if (wasRefreshing && !isRefreshing) {
            hasTriggeredThresholdForCycle = false
            RefreshHapticFeedback.triggerSuccessHaptic(context, view, haptic)
        }
        wasRefreshing = isRefreshing
    }
}
