package com.miguelrivera.vindexweather

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

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
     * Eventually there will be a custom OkHttpClient injected here
     * to share the same cache/network logic between API calls and Image loading.
     * For now, we return the default ImageLoader.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .build()
    }
}