package com.miguelrivera.vindexweather.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result as WorkerResult
import androidx.work.WorkerParameters
import com.miguelrivera.vindexweather.core.common.Result as VindexResult
import com.miguelrivera.vindexweather.domain.location.LocationTracker
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker to sync weather data periodically.
 *
 * Uses [HiltWorker] for dependency injection. This worker is responsible for
 * keeping the offline cache fresh without user interaction.
 */
@HiltWorker
class SyncWeatherWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): WorkerResult {
        // 1. Get current location (if permission granted)
        // If we don't have location permissions or GPS is off, we can't sync local weather.
        val location = locationTracker.getCurrentLocation() ?: return WorkerResult.failure()

        // 2. Trigger Sync
        return when (repository.syncWeather(location.latitude, location.longitude)) {
            is VindexResult.Success -> WorkerResult.success()
            is VindexResult.Error -> {
                // We treat all errors as transient and retry using WorkManager's backoff policy.
                // In a production app, we would distinguish between permanent failures (4xx)
                // and transient network issues to avoid unnecessary retries.
                WorkerResult.retry()
            }
            else -> WorkerResult.failure()
        }
    }
}