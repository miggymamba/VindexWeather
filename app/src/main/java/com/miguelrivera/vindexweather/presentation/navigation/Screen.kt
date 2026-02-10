package com.miguelrivera.vindexweather.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Defines the type-safe navigation destinations for the application.
 *
 * Utilizes Kotlin Serialization to generate route schemas, ensuring compile-time
 * safety for navigation arguments and deep links.
 */
sealed interface Screen {

    /**
     * The primary dashboard displaying current weather and forecast.
     */
    @Serializable
    data object Dashboard: Screen

    /**
     * The search screen for finding locations.
     */
    @Serializable
    data object Search: Screen
}