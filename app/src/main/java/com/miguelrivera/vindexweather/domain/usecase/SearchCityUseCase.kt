package com.miguelrivera.vindexweather.domain.usecase

import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Executes a city search query.
 */
class SearchCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(query: String): Result<List<City>> {
        if (query.isBlank()) return Result.Success(emptyList())
        return repository.searchCity(query)
    }
}