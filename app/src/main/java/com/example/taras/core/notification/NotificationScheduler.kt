package com.example.taras.core.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object NotificationScheduler {
    fun scheduleNotificationSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Minimum interval enforced by Android is 15 minutes
        val periodicRequest = PeriodicWorkRequestBuilder<GithubNotificationWorker>(
            15.minutes.toJavaDuration()
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "GithubNotificationSync",
            ExistingPeriodicWorkPolicy.KEEP, // don't restart if already enqueued
            periodicRequest
        )
    }
}
