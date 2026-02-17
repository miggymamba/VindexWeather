package com.miguelrivera.vindexweather.presentation.navigation

import androidx.navigation.NavHostController
import com.miguelrivera.vindexweather.domain.model.Coordinates

/**
 * Encapsulates navigation logic to prevent leaking [NavHostController] into UI screens.
 * This pattern allows screens to be previewable and testable without mocking the controller.
 */
class NavActions(private val navController: NavHostController) {

    fun navigateToSearch() {
        navController.navigate(Screen.Search) {
            // Avoid building up a huge stack of search screens if user clicks multiple times
            launchSingleTop = true
        }
    }

    /**
     * Navigates back to the previous screen and passes a result (Selected City Coordinates).
     * Used by the Search Screen to return the user's selection.
     */
    fun navigateBackBackWithResult(coordinates: Coordinates) {
        val previousBackstackEntry = navController.previousBackStackEntry
        previousBackstackEntry?.savedStateHandle?.set(KEY_SELECTED_COORDINATES, coordinates)
        navController.popBackStack()
    }

    companion object {
        const val KEY_SELECTED_COORDINATES = "selected_coordinates"
    }
}