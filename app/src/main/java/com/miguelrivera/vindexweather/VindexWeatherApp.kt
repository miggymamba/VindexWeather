package com.miguelrivera.vindexweather

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.miguelrivera.vindexweather.data.worker.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

/**
 * [VindexWeatherApp] serves as the Hilt Entry Point for the app.
 *
 * Responsible for initializing global configurations:
 * * [HiltAndroidApp]: Generates the Dagger/Hilt dependency graph.
 * * [ImageLoaderFactory]: Configures Coil with a shared OkHttp client.
 * * [Configuration.Provider]: Configures WorkManager to support Hilt-injected workers.
 */
@HiltAndroidApp
class VindexWeatherApp : Application(), ImageLoaderFactory, Configuration.Provider {

    /**
     * Lazy-loaded provider for the [ImageLoader].
     * Delays initialization of the network stack until the first image request
     * to optimize application startup performance.
     */
    @Inject
    lateinit var imageLoader: Provider<ImageLoader>

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManagerScheduler: WorkManagerScheduler

    override fun onCreate() {
        super.onCreate()
        // Triggers the periodic background sync schedule immediately upon app launch.
        workManagerScheduler.schedulePeriodicSync()
    }

    override fun newImageLoader(): ImageLoader = imageLoader.get()

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}