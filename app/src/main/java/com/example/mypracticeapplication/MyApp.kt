package com.example.mypracticeapplication

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import com.example.mypracticeapplication.utils.DataStoreManager

class MyApp : Application(), SingletonImageLoader.Factory {
    lateinit var dataStoreManager: DataStoreManager
        private set

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = DataStoreManager(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizeBytes(32L * 1024 * 1024)
                    .build()
            }
            .crossfade(false)
            .build()
}
