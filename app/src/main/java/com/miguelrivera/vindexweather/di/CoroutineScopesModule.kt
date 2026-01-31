 package com.miguelrivera.vindexweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

/**
 * Provides a global [CoroutineScope] bound to the application lifecycle.
 * Useful for WorkManager or operations that must survive configuration changes.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {

    @Singleton
    @Provides
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(VindexDispatchers.Default) dispatcher: CoroutineDispatcher
    ): CoroutineScope  = CoroutineScope(SupervisorJob() + dispatcher)
}