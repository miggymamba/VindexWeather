package com.miguelrivera.vindexweather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miguelrivera.vindexweather.data.local.database.dao.WeatherDao
import com.miguelrivera.vindexweather.data.local.database.entity.WeatherEntity

/**
 * Main database definition for the application.
 */
@Database(
    entities = [WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao

    companion object {
        const val DATABASE_NAME = "vindex_weather_db"
    }
}