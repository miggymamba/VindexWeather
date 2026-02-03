package com.miguelrivera.vindexweather.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a cached weather snapshot in the local database.
 *
 * This entity serves as the single source of truth for the UI. Network responses
 * are mapped to this structure before being persisted.
 */
@Entity(tableName = "weather_forecasts")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val tempCurrent: Double,
    val tempMin: Double,
    val tempMax: Double,
    val conditionText: String,
    val iconUrl: String,
    val humidity: Int,
    val windSpeed: Double
)