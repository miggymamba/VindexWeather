package com.miguelrivera.vindexweather.core.common

/**
 * A generic container for holding a value with its loading status.
 *
 * This wrapper is used across the Domain and Data layers to communicate
 * success or failure states without throwing exceptions.
 */
sealed interface Result<out T> {

    /**
     * Represents a successful operation containing data.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * Represents a failed operation with an exception and optional message.
     */
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>

    /**
     * Represents a loading state, typically used in Flow emissions.
     */
    data object Loading : Result<Nothing>
}