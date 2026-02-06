package com.miguelrivera.vindexweather.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the scheduling of background tasks.
 *
 * Configures [WorkManager] requests with defined constraints (Network/Battery)
 * and periodic execution intervals.
 */
@Singleton
class WorkManagerScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private companion object {
        const val SYNC_WORK_NAME = "sync_weather_work"
        const val SYNC_INTERVAL_HOURS = 4L
    }

    /**
     * Enqueues the periodic weather sync worker.
     *
     * Configuration details:
     * * **Constraints:** Requires an unmetered network connection and sufficient battery level
     * to optimize resource consumption.
     * * **Policy:** [ExistingPeriodicWorkPolicy.KEEP] is used to maintain the original schedule
     * and avoid resetting the execution window.
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWeatherWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}