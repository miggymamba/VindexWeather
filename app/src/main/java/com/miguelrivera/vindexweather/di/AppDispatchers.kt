package com.miguelrivera.vindexweather.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Dispatcher(val dispatcher: VindexDispatchers)

/**
 * Enumeration of the available Coroutine Dispatchers in the application.
 * Used as a key for Dependency Injection.
 */
enum class VindexDispatchers {
    /**
     * Optimized for CPU-intensive work.
     * Use this for sorting lists, parsing JSON, or complex calculations.
     * Backed by a thread pool roughly equal to the number of CPU cores.
     */
    Default,

    /**
     * Optimized for blocking I/O operations.
     * Use this for Network requests (Retrofit), Database queries (Room), or File read/writes.
     * Backed by a large thread pool (64+) to handle waiting threads efficiently.
     */
    IO
}