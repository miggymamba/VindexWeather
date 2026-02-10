package com.miguelrivera.vindexweather.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.miguelrivera.vindexweather.presentation.navigation.NavActions

/**
 * Global CompositionLocal for Navigation Actions.
 * Allows deeply nested composables to trigger navigation without prop-drilling callbacks.
 */
val LocalNavActions = staticCompositionLocalOf<NavActions> {
    error("LocalNavActions not provided. Ensure it is wrapped in MainActivity via CompositionLocalProvider.")
}