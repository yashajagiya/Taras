package com.example.taras.core.tcg.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.taras.R
import com.example.taras.core.db.AppDatabase
import com.example.taras.core.tcg.repository.TcgRepository
import com.example.taras.view.MainActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WeeklyPackWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(context)
            val repository = TcgRepository(db.tcgCardDao())
            repository.ensureSeeded()

            val mysteryCard = repository.drawMysteryPackCard()
            // Add to user inventory with unrevealed scratch state
            repository.unlockCard(
                cardId = mysteryCard.id,
                isHolo = (mysteryCard.tier.name.startsWith("S")),
                isScratchCompleted = false
            )

            sendPackNotification(mysteryCard.name, mysteryCard.tier.badgeLabel)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun sendPackNotification(cardName: String, tierBadge: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "tcg_weekly_pack_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "F1 GridTCG Weekly Packs",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when your weekly Monday mystery pack is ready to scratch"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("nav_target", "tcg_pack")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Weekly F1 Mystery Pack Dropped!")
            .setContentText("A new mystery card ($tierBadge) is waiting in your binder. Scratch to reveal!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
            notificationManager.notify(2026, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val WORK_NAME = "f1_grid_tcg_weekly_pack_work"

        fun scheduleWeeklyPack(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // Calculate delay until next Monday 00:00 UTC
            val now = Calendar.getInstance()
            val nextMonday = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_YEAR, 7)
                }
            }
            val initialDelay = (nextMonday.timeInMillis - now.timeInMillis).coerceAtLeast(0)

            val periodicRequest = PeriodicWorkRequestBuilder<WeeklyPackWorker>(7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}
