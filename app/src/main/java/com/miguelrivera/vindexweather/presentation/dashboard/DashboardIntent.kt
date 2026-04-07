package com.miguelrivera.vindexweather.presentation.dashboard

import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.model.Coordinates

/**
 * Defines the strict interaction contract for the Dashboard screen.
 *
 * Represents all possible user actions and system events. By routing all interactions
 * through this sealed interface, the UI remains fully decoupled from business logic,
 * ensuring predictable state mutations and a Unidirectional Data Flow (UDF).
 */
sealed interface DashboardIntent {
    /** Triggers a manual synchronization of the weather data. */
    data object Refresh : DashboardIntent

    /** * Updates the target coordinates for the weather feed.
     * @param coordinates The new location to fetch data for.
     */
    data class UpdateLocation(val coordinates: Coordinates) : DashboardIntent

    /** * Modifies the user's preferred temperature display unit.
     * @param unit The requested temperature unit.
     */
    data class ToggleUnit(val unit: TemperatureUnit) : DashboardIntent

    /** Acknowledges and dismisses any active transient error states in the UI. */
    data object ClearError : DashboardIntent
}