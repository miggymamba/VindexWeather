package com.miguelrivera.vindexweather.presentation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import com.miguelrivera.vindexweather.presentation.navigation.NavActions

/**
 * Global CompositionLocal for Navigation Actions.
 * Allows deeply nested composables to trigger navigation without prop-drilling callbacks.
 */
val LocalNavActions = staticCompositionLocalOf<NavActions> {
    error("LocalNavActions not provided. Ensure it is wrapped in MainActivity via CompositionLocalProvider.")
}

/**
 * Global CompositionLocal for Adaptive Layouts (WindowSizeClass).
 * * Provides the WindowSizeClass globally to allow leaf composables
 * (like individual weather cards or grids) to adjust their spans or layouts dynamically
 * based on the available screen real estate (e.g., Foldables vs Phones) without altering
 * the signature of every parent composable.
 */
val LocalWindowSize = staticCompositionLocalOf<WindowSizeClass> {
    error("LocalWindowSize not provided. Ensure calculateWindowSize() is provided in MainActivity.")
}