package com.miguelrivera.vindexweather

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

/**
 * [VindexWeatherApp] serves as the Hilt Entry Point for the app.
 *
 * * @HiltAndroidApp: Triggers Dagger's code generation, creating a base class
 * that serves as the application-level dependency container.
 * * ImageLoaderFactory: Configures Coil globally, allowing us to inject custom
 * OkHttp clients (e.g., with caching or auth interceptors) into the image loader later.
 */
@HiltAndroidApp
class VindexWeatherApp : Application(), ImageLoaderFactory {

    /**
     * A [Provider] is injected here to lazy-load the [ImageLoader].
     * This prevents instantiating the entire network stack instantly at app startup
     * if it's not strictly needed immediately (startup performance optimization).
     */
    @Inject
    lateinit var imageLoader: Provider<ImageLoader>

    override fun newImageLoader(): ImageLoader = imageLoader.get()
}