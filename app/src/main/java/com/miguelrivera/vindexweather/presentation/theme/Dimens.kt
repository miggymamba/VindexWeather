package com.miguelrivera.vindexweather.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized source of truth for application layout dimensions.
 *
 * Adopts a semantic naming convention (Small, Medium, Large) to maintain
 * consistency across the Design System and avoid "magic numbers" in UI components.
 */

object Dimens {
    // Spacing & Padding
    val PaddingSmall = 8.dp
    val PaddingMedium = 16.dp
    val PaddingLarge = 24.dp
    val PaddingExtraLarge = 32.dp

    // Component Specific
    val ToggleSwitchSpacing = 8.dp
    val SearchBarHeight = 56.dp
    val SearchItemMinHeight = 72.dp
    val IconSizeSmall = 24.dp
    val IconSizeMedium = 48.dp
    val ShimmerCornerRadius = 4.dp

    // Shimmer Placeholders (Text Simulation)
    val ShimmerCityNameWidth = 150.dp
    val ShimmerCityNameHeight = 20.dp
    val ShimmerSubtitleWidth = 100.dp
    val ShimmerSubtitleHeight = 16.dp

    // Corners
    val CornerRadiusSmall = 8.dp
    val CornerRadiusMedium = 12.dp
    val CornerRadiusLarge = 16.dp
    val CornerRadiusRound = 50.dp // For fully rounded search bars
}