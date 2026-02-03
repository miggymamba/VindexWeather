package com.miguelrivera.vindexweather.di

import android.content.Context
import androidx.room.Room
import com.miguelrivera.vindexweather.data.local.database.WeatherDatabase
import com.miguelrivera.vindexweather.data.local.database.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            WeatherDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = false)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao
    }
}