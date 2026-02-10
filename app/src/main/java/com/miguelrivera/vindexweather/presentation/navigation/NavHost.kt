package com.miguelrivera.vindexweather.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miguelrivera.vindexweather.presentation.dashboard.DashboardScreen
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

        composable<Screen.Dashboard> {
            // DashboardScreen consumes LocalNavActions internally
            DashboardScreen()
        }

        composable<Screen.Search> {
            // SearchScreen consumes LocalNavActions internally
            SearchScreen()
        }
    }
}