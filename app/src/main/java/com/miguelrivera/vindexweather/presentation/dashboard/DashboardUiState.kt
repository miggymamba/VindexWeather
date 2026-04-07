package com.miguelrivera.vindexweather.presentation.dashboard

import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.model.Coordinates

/**
 * Immutable representation of the Dashboard screen's UI state.
 *
 * Aggregates transient network operation status (syncing/error) with persistent
 * user preferences (temperature units) to ensure a Unidirectional Data Flow (UDF).
 *
 * @property coordinates The current coordinates which the dashboard displays the weather forecast.
 * @property isSyncing True when a background data synchronization is active.
 * @property error Non-null string message if the last operation failed; null otherwise.
 * @property temperatureUnit The user's preferred unit for display logic.
 */
data class DashboardUiState(
    val coordinates: Coordinates,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.METRIC
)
