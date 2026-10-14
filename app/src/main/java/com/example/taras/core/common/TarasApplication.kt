package com.example.taras.core.common

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.taras.core.di.AppContainer
import com.example.taras.core.di.DefaultAppContainer
import com.example.taras.core.di.TarasViewModelFactory
import com.example.taras.network_calls.NetworkModule

class TarasApplication : Application(), SingletonImageLoader.Factory {
    companion object {
        lateinit var instance: TarasApplication
            private set
    }

    lateinit var appContainer: AppContainer
        private set

    val viewModelFactory: TarasViewModelFactory by lazy {
        TarasViewModelFactory(appContainer)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = DefaultAppContainer(this)
        SingletonImageLoader.setSafe { newImageLoader(it) }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { NetworkModule.okHttpClient }))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(150L * 1024 * 1024) 
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .allowHardware(true)
            .crossfade(true)
            .build()
    }
}