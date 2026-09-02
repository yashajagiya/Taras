package com.example.taras.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import androidx.core.content.edit
import com.example.taras.core.common.UserPreferences
import kotlinx.coroutines.flow.first

/**
 * Data model for the notification JSON fetched from GitHub.
 * @Serializable allows Kotlinx Serialization to convert JSON text into this object.
 */
@Serializable
private data class GithubNotification(
    val id: Int,       // Unique ID to track if we've already shown this notification
    val title: String, // Headline of the notification
    val message: String // Detailed content of the notification
)

/**
 * GithubNotificationWorker is a background task managed by Android WorkManager.
 * It periodically checks for new "broadcast" notifications from a static JSON file
 * hosted on GitHub Pages.
 */
class GithubNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "GHNotifWorker"
        
        // SharedPreferences keys for persisting state
        private const val PREFS_NAME = "notification_tracker_prefs"
        private const val KEY_LAST_SEEN_ID = "last_seen_notification_id"
        
        // Android Notification Channel ID (required for Android 8.0+)
        private const val CHANNEL_ID = "github_broadcast_channel"

        // URL where the notification metadata is hosted
        private const val JSON_ENDPOINT =
            "https://yashajagiya.github.io/Taras/notification/notification.json"

        // Reusable JSON parser configured to be resilient to extra fields in JSON
        private val json = Json { ignoreUnknownKeys = true }
    }

    /**
     * Network client configured with timeouts using modern Kotlin Duration.
     */
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15.seconds.toJavaDuration())
        .readTimeout(15.seconds.toJavaDuration())
        .build()

    /**
     * This method runs in the background. WorkManager calls this based on the schedule.
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Prepare the network request
            val request = Request.Builder()
                .url(JSON_ENDPOINT)
                .header("Cache-Control", "no-cache") // Ensure we don't get a stale cached version
                .get()
                .build()

            // 2. Execute the network call
            val response = client.newCall(request).execute()

            // Handle network errors (Result.retry() tells WorkManager to try again later)
            if (!response.isSuccessful) {
                Log.w(TAG, "HTTP ${response.code}")
                return@withContext Result.retry()
            }

            // 3. Parse the JSON body into our GithubNotification data class
            val body = response.body?.string() ?: return@withContext Result.retry()
            val notification = json.decodeFromString<GithubNotification>(body)

            val currentId = notification.id
            val rawTitle = notification.title
            val rawMessage = notification.message

            // Fetch username from DataStore
            val userPreferences = UserPreferences(applicationContext)
            val userName = userPreferences.userNameFlow.first()

            // Personalize the notification by replacing $username placeholder
            val title = rawTitle.replace("\$username", userName, ignoreCase = true)
            val message = rawMessage.replace("\$username", userName, ignoreCase = true)

            // 4. Persistence Check: Compare current ID with the last one we successfully showed
            val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastSeenId = prefs.getInt(KEY_LAST_SEEN_ID, 0)

            // If the ID on GitHub is greater than our local ID, it's a "new" broadcast
            if (currentId > lastSeenId) {
                // 5. Trigger the system UI notification
                showLocalNotification(title, message, currentId)
                
                // 6. Save the new ID so we don't show the same message twice
                prefs.edit { putInt(KEY_LAST_SEEN_ID, currentId) }
                Log.i(TAG, "Notification $currentId shown")
            } else {
                Log.d(TAG, "No new notification (current=$currentId, lastSeen=$lastSeenId)")
            }

            // Successfully finished the background task
            Result.success()
        } catch (e: Exception) {
            // Log any unexpected crashes (network loss, timeout, etc.) and retry
            Log.e(TAG, "Worker failed", e)
            Result.retry()
        }
    }

    /**
     * Handles the boilerplate of creating a Notification Channel (if needed)
     * and posting the notification to the System UI.
     */
    private fun showLocalNotification(title: String, message: String, notificationId: Int) {
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Since Android 8.0 (Oreo), notifications MUST have a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GitHub Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pushed via GitHub Pages"
            }
            manager.createNotificationChannel(channel)
        }

        // Build the actual notification appearance
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // The icon shown in the status bar
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Visual priority
            .setAutoCancel(true) // Dismiss when the user taps it
            .build()

        // Send the notification to the system
        manager.notify(notificationId, notification)
    }
}
