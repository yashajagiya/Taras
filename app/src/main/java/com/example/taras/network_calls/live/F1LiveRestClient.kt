package com.example.taras.network_calls.live

import android.util.Log
import com.example.taras.network_calls.ApiConstants
import com.example.taras.network_calls.NetworkModule
import com.example.taras.network_calls.taras.model.live.LiveDiscoveryResponse
import com.example.taras.network_calls.taras.model.live.RaceSnapshot
import com.example.taras.network_calls.taras.model.live.TimelineItem
import com.example.taras.network_calls.taras.model.live.TimelineOverview
import com.example.taras.network_calls.taras.model.live.TimelineSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class F1LiveRestClient(
    private val client: OkHttpClient = NetworkModule.okHttpClient
) {
    private val json = NetworkModule.json
    private val tag = "F1LiveRestClient"

    suspend fun fetchDiscovery(url: String = ApiConstants.BASE_URL_TARAS_RAW + ApiConstants.ENDPOINT_V2_LIVE): Result<LiveDiscoveryResponse> =
        withContext(Dispatchers.IO) {
            val primaryResult = fetchDiscoveryFromUrl(url)
            if (primaryResult.isSuccess) {
                primaryResult
            } else {
                val fallbackUrl = ApiConstants.BASE_URL_TARAS_GITHUB + ApiConstants.ENDPOINT_V2_LIVE
                Log.w(tag, "Discovery from $url failed, falling back to $fallbackUrl: ${primaryResult.exceptionOrNull()?.message}")
                fetchDiscoveryFromUrl(fallbackUrl)
            }
        }

    private fun fetchDiscoveryFromUrl(url: String): Result<LiveDiscoveryResponse> =
        runCatching {
            val request = Request.Builder()
                .url(url)
                .header("Cache-Control", "no-cache, no-store")
                .header("Pragma", "no-cache")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Discovery failed with HTTP ${response.code}")
                }
                val body = response.body?.string()
                    ?: throw IllegalStateException("Empty body from discovery endpoint")
                json.decodeFromString<LiveDiscoveryResponse>(body)
            }
        }.onFailure {
            Log.e(tag, "Failed to fetch discovery from $url: ${it.message}")
        }

    suspend fun fetchTimingState(baseUrl: String): Result<RaceSnapshot> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cleanUrl = baseUrl.trimEnd('/') + "/api/timing/state"
                val request = Request.Builder().url(cleanUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Timing state failed with HTTP ${response.code}")
                    }
                    val body = response.body?.string()
                        ?: throw IllegalStateException("Empty timing state response")
                    json.decodeFromString<RaceSnapshot>(body)
                }
            }
        }

    suspend fun fetchTimeline(baseUrl: String): Result<TimelineSnapshot> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cleanUrl = baseUrl.trimEnd('/') + "/api/timeline"
                val request = Request.Builder().url(cleanUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Timeline fetch failed with HTTP ${response.code}")
                    }
                    val body = response.body?.string()
                        ?: throw IllegalStateException("Empty timeline response")
                    json.decodeFromString<TimelineSnapshot>(body)
                }
            }
        }

    suspend fun fetchTimelineDelta(baseUrl: String, afterId: String): Result<List<TimelineItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cleanUrl = baseUrl.trimEnd('/') + "/api/timeline?afterId=$afterId"
                val request = Request.Builder().url(cleanUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Timeline delta failed with HTTP ${response.code}")
                    }
                    val body = response.body?.string()
                        ?: throw IllegalStateException("Empty timeline delta response")
                    json.decodeFromString<List<TimelineItem>>(body)
                }
            }
        }

    suspend fun fetchTimelineOverview(baseUrl: String): Result<TimelineOverview> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cleanUrl = baseUrl.trimEnd('/') + "/api/timeline/overview"
                val request = Request.Builder().url(cleanUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Timeline overview failed with HTTP ${response.code}")
                    }
                    val body = response.body?.string()
                        ?: throw IllegalStateException("Empty timeline overview response")
                    json.decodeFromString<TimelineOverview>(body)
                }
            }
        }
}
