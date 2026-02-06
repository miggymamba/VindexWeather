package com.miguelrivera.vindexweather.domain.usecase

import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Synchronizes weather data from the remote API to the local database for the specified location.
 */
class SyncWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Unit> {
        return repository.syncWeather(latitude, longitude)
    }
}