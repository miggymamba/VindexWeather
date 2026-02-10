package com.miguelrivera.vindexweather.presentation.search

import com.miguelrivera.vindexweather.domain.model.City

/**
 * Represents the immutable UI state for the City Search screen.
 *
 * Follows the MVI pattern to ensure a unidirectional data flow for search results.
 */
sealed interface SearchState {
    /** Initial state; waiting for user input. */
    data object Idle: SearchState

    /** Search query is being executed. */
    data object Loading: SearchState

    /** Search completed successfully but returned no results. */
    data object Empty : SearchState

    /** Search completed with results. */
    data class Success(val results: List<City>) : SearchState

    /** Search failed due to network or other errors. */
    data class Error(val message: String) : SearchState
}