package com.miguelrivera.vindexweather.domain.usecase

import androidx.paging.PagingData
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves a stream of weather data from the local database, paginated for UI consumption.
 */
class GetPagedWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(latitude: Double, longitude: Double): Flow<PagingData<Weather>> {
        return repository.getPagedWeather(latitude, longitude)
    }
}