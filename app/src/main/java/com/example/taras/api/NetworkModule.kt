package com.example.taras.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openF1Retrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_OPEN_F1)
    }

    val f1ApiDevRetrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_F1_API_DEV)
    }

    val tarasGithubRetrofit: Retrofit by lazy {
        createRetrofit(ApiConstants.BASE_URL_TARAS_GITHUB)
    }
}
