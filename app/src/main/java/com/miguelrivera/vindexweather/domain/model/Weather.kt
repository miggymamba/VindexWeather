package com.miguelrivera.vindexweather.domain.model

/**
 * Pure Domain Model representing weather data.
 * independent of Android/Room/Network annotations.
 */
data class Weather(
    val id: Long,
    val cityName: String,
    val tempCurrent: Double,
    val conditionText: String,
    val iconUrl: String,
    val timestamp: Long
)