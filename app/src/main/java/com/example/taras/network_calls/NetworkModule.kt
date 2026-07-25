package com.example.taras.network_calls

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

//    val openF1Retrofit: Retrofit by lazy {
//        createRetrofit(ApiConstants.BASE_URL_OPEN_F1)
//    }

    val f1ApiDevRetrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_F1_API_DEV)
    }

    val tarasGithubRetrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_TARAS_GITHUB)
    }
}
