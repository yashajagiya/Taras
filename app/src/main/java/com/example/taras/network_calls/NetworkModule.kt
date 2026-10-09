package com.example.taras.network_calls

import com.example.taras.core.common.TarasApplication
import com.example.taras.network_calls.taras.KtorTarasDataService
import com.example.taras.network_calls.taras.TarasDataService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import okio.Path.Companion.toOkioPath
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient

object NetworkModule {

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val okHttpClient: OkHttpClient by lazy {
        val cachePath = try {
            TarasApplication.instance.cacheDir.toOkioPath() / "http_cache"
        } catch (_: Exception) {
            null
        }
        val dispatcher = okhttp3.Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 24
        }
        val builder = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(24, 5.minutes.inWholeMinutes, TimeUnit.MINUTES))
            .connectTimeout(15.seconds.toJavaDuration())
            .readTimeout(20.seconds.toJavaDuration())
            .retryOnConnectionFailure(true)

        if (cachePath != null) {
            builder.cache(Cache(cachePath.toFile(), 50L * 1024 * 1024))
        }
        builder.build()
    }

    val ktorHttpClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            expectSuccess = true

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 20.seconds.inWholeMilliseconds
                connectTimeoutMillis = 15.seconds.inWholeMilliseconds
                socketTimeoutMillis = 20.seconds.inWholeMilliseconds
            }

            defaultRequest {
                url(ApiConstants.BASE_URL_TARAS_GITHUB)
            }
        }
    }

    val tarasDataService: TarasDataService by lazy {
        KtorTarasDataService(ktorHttpClient)
    }
}
