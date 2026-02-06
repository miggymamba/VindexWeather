package com.miguelrivera.vindexweather.di

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.miguelrivera.vindexweather.data.location.DefaultLocationTracker
import com.miguelrivera.vindexweather.domain.location.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        locationClient: FusedLocationProviderClient,
        application: Application
    ): LocationTracker {
        return DefaultLocationTracker(locationClient, application)
    }
}