package com.miguelrivera.vindexweather.data.remote


import com.miguelrivera.vindexweather.data.remote.dto.GeocodingDto
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
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): WeatherResponseDto

    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<GeocodingDto>
}