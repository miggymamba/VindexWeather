package com.miguelrivera.vindexweather.domain.location

import com.miguelrivera.vindexweather.domain.model.Coordinates

/**
 * Interface for retrieving the device's current location.
 *
 * Implementations should handle the necessary permission checks and
 * location provider resolution (GPS vs Network) internally.
 */
interface LocationTracker {

    /**
     * Retrieves the current location of the device.
     * Returns null if permissions are not granted or location is disabled/unavailable.
     */
    suspend fun getCurrentLocation(): Coordinates?
}