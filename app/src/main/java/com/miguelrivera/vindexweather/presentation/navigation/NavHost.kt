package com.miguelrivera.vindexweather.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miguelrivera.vindexweather.domain.model.Coordinates
import com.miguelrivera.vindexweather.presentation.dashboard.DashboardScreen
import com.miguelrivera.vindexweather.presentation.dashboard.DashboardViewModel
import com.miguelrivera.vindexweather.presentation.search.SearchScreen

/**
 * Top-level navigation graph for the application.
 *
 * Maps [Screen] destinations to their respective UI implementations.
 */
@Composable
fun VindexNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen = Screen.Dashboard
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable<Screen.Dashboard> { entry ->
            val viewModel = hiltViewModel<DashboardViewModel>()
            // Listen for results from the Search Screen (SavedStateHandle)
            // `getStateFlow` is used to observe the value reactively without LiveData dependencies
            // The key matches NavActions.KEY_SELECTED_COORDINATES

            val selectedCoordinates by entry.savedStateHandle
                .getStateFlow<Coordinates?>(NavActions.KEY_SELECTED_COORDINATES, null)
                .collectAsStateWithLifecycle()

            LaunchedEffect(selectedCoordinates) {
                selectedCoordinates?.let { coordinates ->
                    viewModel.updateLocation(coordinates)
                    // Clear the result so it doesn't re-trigger on configuration change
                    entry.savedStateHandle.remove<Coordinates>(NavActions.KEY_SELECTED_COORDINATES)
                }
            }

            DashboardScreen(viewModel = viewModel)
        }

        composable<Screen.Search> {
            // SearchScreen consumes LocalNavActions internally
            SearchScreen()
        }
    }
}