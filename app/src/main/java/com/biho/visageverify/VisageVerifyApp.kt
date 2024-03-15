package com.biho.visageverify

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.biho.visageverify.data.dataModule
import com.biho.visageverify.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VisageVerifyApp: Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@VisageVerifyApp)
            modules(
                listOf(
                    appModule(),
                    domainModule(),
                    dataModule()
                )
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(context = this).newBuilder()
            .memoryCachePolicy(policy = CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(context = this)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(enable = true)
                    .build()
            }
            .diskCachePolicy(policy = CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.1)
                    .directory(directory = cacheDir)
                    .build()
            }
            .logger(logger = DebugLogger())
            .build()
    }

}