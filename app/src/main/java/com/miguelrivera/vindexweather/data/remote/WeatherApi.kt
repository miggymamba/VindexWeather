package com.miguelrivera.vindexweather.data.remote


import com.miguelrivera.vindexweather.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for OpenWeatherMap 2.5 Standard API.
 */
interface WeatherApi {

    /**
     * Fetches the 5-day / 3-hour forecast data.
     * Note: "appid" is added automatically by the ApiKeyInterceptor.
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): WeatherResponseDto
}