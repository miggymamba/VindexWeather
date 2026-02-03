package com.miguelrivera.vindexweather.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.miguelrivera.vindexweather.data.local.database.entity.WeatherEntity

/**
 * Data Access Object for Weather entities.
 *
 * Utilizes [Upsert] to simplify insert-or-update logic and provides a [PagingSource]
 * for integration with the Paging 3 library.
 */
@Dao
interface WeatherDao {

    @Upsert
    suspend fun upsertWeather(weather: List<WeatherEntity>)

    /**
     * Returns a PagingSource for the Paging 3 library.
     * Room automatically handles the invalidation of this source when data changes.
     */
    @Query("SELECT * FROM weather_forecasts ORDER BY timestamp ASC")
    fun getWeatherPagingSource(): PagingSource<Int, WeatherEntity>

    @Query("DELETE FROM weather_forecasts")
    suspend fun clearAll()
}