package com.miguelrivera.vindexweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module that maps the [VindexDispatchers] enum keys to the actual [CoroutineDispatcher] implementations.
 *
 * This abstraction allows us to swap these dispatchers for [TestDispatcher]s during Unit/Integration tests,
 * ensuring tests run deterministically without race conditions.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    /**
     * Provides the standard IO dispatcher for blocking operations.
     */
    @Provides
    @Dispatcher(VindexDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the standard Default dispatcher for CPU-bound tasks.
     */
    @Provides
    @Dispatcher(VindexDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}