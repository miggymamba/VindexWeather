package com.miguelrivera.vindexweather.presentation.navigation

import androidx.navigation.NavHostController

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

    fun navigateBack() {
        navController.popBackStack()
    }

}