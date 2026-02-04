package com.miguelrivera.vindexweather.di

import com.miguelrivera.vindexweather.data.repository.WeatherRepositoryImpl
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the implementation to the interface.
     * Using @Binds is more efficient than @Provides because it generates less boilerplate code.
     */
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImp: WeatherRepositoryImpl
    ): WeatherRepository
}