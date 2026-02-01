package com.miguelrivera.vindexweather.core.network

import com.miguelrivera.vindexweather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Intercepts outgoing requests to append the API Key query parameter.
 *
 * This ensures all API calls are authenticated without requiring the API key
 * to be passed as an explicit argument in the Retrofit service definitions.
 */
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    private companion object {
        const val QUERY_PARAM_APP_ID = "appId"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val newUrl = originalHttpUrl.newBuilder()
            .addQueryParameter(QUERY_PARAM_APP_ID, BuildConfig.WEATHER_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}