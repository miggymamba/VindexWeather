package com.miguelrivera.vindexweather.data.mapper

import com.miguelrivera.vindexweather.data.local.database.entity.WeatherEntity
import com.miguelrivera.vindexweather.data.remote.dto.GeocodingDto
import com.miguelrivera.vindexweather.data.remote.dto.WeatherResponseDto
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.domain.model.Weather

/**
 * Converts a database entity into a clean domain model.
 * * The Data Layer (Entity) is separated from the Domain Layer (Model) to ensure
 * that database schema changes do not ripple through to the UI.
 */
fun WeatherEntity.toWeather(): Weather {
    return Weather(
        id = id,
        cityName = cityName,
        tempCurrent = tempCurrent,
        conditionText = conditionText,
        iconUrl = iconUrl,
        timestamp = timestamp
    )
}

/**
 * Converts the Network DTO into Database Entities.
 *
 * This logic handles flattening the nested JSON structure (City + List Items)
 * into a flat table structure for Room.
 */
fun WeatherResponseDto.toWeatherEntities(): List<WeatherEntity> {
    return this.list.map { dto ->
        WeatherEntity(
            cityName = this.city.name,
            latitude = this.city.coord.lat,
            longitude = this.city.coord.lon,
            timestamp = dto.dt,
            tempCurrent = dto.main.temp,
            tempMin = dto.main.tempMin,
            tempMax = dto.main.tempMax,
            conditionText = dto.weather.firstOrNull()?.main ?: "Unknown",
            iconUrl = "https://openweathermap.org/img/wn/${dto.weather.firstOrNull()?.icon}@2x.png",
            humidity = dto.main.humidity,
            windSpeed = dto.wind.speed
        )
    }
}

/**
 * Maps a list of Geocoding DTOs to Domain City models.
 */

fun List<GeocodingDto>.toCityList(): List<City> {
    return this.map { dto ->
        City(
            name = dto.name,
            latitude = dto.lat,
            longitude = dto.lon,
            country = dto.country,
            state = dto.state
        )
    }
}