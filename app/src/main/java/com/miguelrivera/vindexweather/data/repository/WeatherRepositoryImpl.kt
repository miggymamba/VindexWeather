package com.miguelrivera.vindexweather.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.data.local.database.WeatherDatabase
import com.miguelrivera.vindexweather.data.mapper.toWeather
import com.miguelrivera.vindexweather.data.mapper.toWeatherEntities
import com.miguelrivera.vindexweather.data.remote.WeatherApi
import com.miguelrivera.vindexweather.data.remote.WeatherRemoteMediator
import com.miguelrivera.vindexweather.di.Dispatcher
import com.miguelrivera.vindexweather.di.VindexDispatchers
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val database: WeatherDatabase,
    @param:Dispatcher(VindexDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    private companion object {
        const val PAGE_SIZE = 40
    }

    override suspend fun syncWeather(
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                // 1. Fetch Fresh Data
                val response = api.getForecast(lat = latitude, lon = longitude)

                // 2. Map DTO -> Entity (Using Shared Mapper)
                val entities = response.toWeatherEntities()

                // 3. Save to DB
                database.withTransaction {
                    database.weatherDao.clearAll()
                    database.weatherDao.upsertWeather(entities)
                }

                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e, "Failed to sync weather data.")
            }
        }
    }

    override fun getPagedWeather(latitude: Double, longitude: Double): Flow<PagingData<Weather>> {
        val pagingSourceFactory = { database.weatherDao.getWeatherPagingSource() }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = WeatherRemoteMediator(
                database = database,
                api = api,
                lat = latitude,
                lon = longitude
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toWeather() }
        }
    }
}