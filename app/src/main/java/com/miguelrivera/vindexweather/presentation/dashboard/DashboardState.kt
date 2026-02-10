package com.miguelrivera.vindexweather.presentation.dashboard

/**
 * Represents the immutable UI state for the Dashboard screen.
 * Used for non-paging data (like transient errors or loading status of the sync operation).
 */
sealed interface DashboardState {
    /** The default state when data is loaded and no operations are pending. */
    data object Idle : DashboardState

    /** Indicates a background synchronization (pull-to-refresh) is in progress. */
    data object Syncing : DashboardState

    /** Represents a transient error (e.g., network failure during sync). */
    data class Error(val message: String) : DashboardState
}