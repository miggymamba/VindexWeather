package com.miguelrivera.vindexweather.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.miguelrivera.vindexweather.data.local.database.WeatherDatabase
import com.miguelrivera.vindexweather.data.local.database.entity.WeatherEntity
import com.miguelrivera.vindexweather.data.mapper.toWeatherEntities
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Orchestrates the data sync between the OpenWeatherMap API and the local Room Database.
 *
 * This implementation follows the "Single Source of Truth" principle. The UI observes
 * the Database; this Mediator decides when to fetch more data from the Network to
 * refill the Database.
 */
@OptIn(ExperimentalPagingApi::class)
class WeatherRemoteMediator @Inject constructor(
    private val database: WeatherDatabase,
    private val api: WeatherApi,
    private val lat: Double,
    private val lon: Double
) : RemoteMediator<Int, WeatherEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WeatherEntity>
    ): MediatorResult {
        return try {
            // Determine if we should load data based on the loadType.
            // REFRESH: Falls through to the API call below.
            // PREPEND: Returns EndOfPagination (no history).
            // APPEND: Returns EndOfPagination (no infinite scroll for this MVP).
            when (loadType) {
                LoadType.PREPEND -> {
                    // We never prepend data (no "past" weather in this endpoint).
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // If we have existing data, we stop because the 5-day forecast is a single-shot list.
                    // We do not support "infinite scrolling" to future dates beyond the initial fetch.
                    val lastItem = state.lastItemOrNull()
                    if (lastItem != null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }

                LoadType.REFRESH -> {
                    // Explicitly do nothing to let it fall through to the API call.
                    // This comment prevents "Empty Body" warnings.
                }
            }

            // Fetch Data
            val response = api.getForecast(lat = lat, lon = lon)

            // Map DTO to Entity
            val entities = response.toWeatherEntities()

            // Transaction: Clear old data (if refreshing) and insert new
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.weatherDao.clearAll()
                }

                database.weatherDao.upsertWeather(entities)
            }

            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}