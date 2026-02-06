package com.miguelrivera.vindexweather.data.location

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.miguelrivera.vindexweather.domain.location.LocationTracker
import com.miguelrivera.vindexweather.domain.model.Coordinates
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Concrete implementation of [LocationTracker] using Google Play Services.
 *
 * Uses [FusedLocationProviderClient] with the [await] extension for structured concurrency.
 * Ensures strict battery optimization by passing a CancellationToken that is triggered
 * if the coroutine is cancelled.
 */
class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Coordinates? {
        val hasFine = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCoarse && !hasFine) {
            return null
        }

        // Explicit CancellationTokenSource to ensure hardware request is stopped
        // if the coroutine is cancelled. Passing null to getCurrentLocation is risky.
        val cts = CancellationTokenSource()

        return try {
            val priority = if (hasFine) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }

            val location = locationClient.getCurrentLocation(priority, cts.token).await()

            location?.let {
                Coordinates(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            // Catches generic exceptions (e.g., GPS disabled, timeout, service missing)
            null
        } finally {
            cts.cancel()
        }
    }
}