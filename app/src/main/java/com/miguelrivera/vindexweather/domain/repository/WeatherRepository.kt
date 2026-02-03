package com.miguelrivera.vindexweather.domain.repository

import androidx.paging.PagingData
import com.miguelrivera.vindexweather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for accessing weather data.
 *
 * This interface resides in the Domain layer to invert the dependency rule.
 * Implementations (Data layer) must adhere to this contract.
 */
interface WeatherRepository {

    /**
     * Triggers a remote sync of weather data for the specific location.
     * Returns a Result to indicate success/failure of the *sync* operation.
     */
    suspend fun syncWeather(latitude: Double, longitude: Double): Result<Unit>

    /**
     * Exposes a stream of paged weather data from the local database.
     * The UI collects this Flow to display the list.
     */
    fun getPagedWeather(): Flow<PagingData<Weather>>
}