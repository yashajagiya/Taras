package com.example.taras.network_calls

import com.example.taras.core.common.TarasApplication
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkModule {

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val okHttpClient: OkHttpClient by lazy {
        val cacheDir = try {
            File(TarasApplication.instance.cacheDir, "http_cache")
        } catch (_: Exception) {
            null
        }
        val builder = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (cacheDir != null) {
            builder.cache(Cache(cacheDir, 25L * 1024 * 1024))
        }
        builder.build()
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val tarasGithubRetrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_TARAS_GITHUB)
    }
}
