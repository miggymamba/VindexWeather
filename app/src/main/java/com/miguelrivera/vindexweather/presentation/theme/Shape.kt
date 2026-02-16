package com.miguelrivera.vindexweather.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * Material 3 Shape definitions mapped to the application's Design System.
 *
 * Uses [Dimens] to ensure consistency across all component corners.
 */
val Shapes = Shapes(
    small = RoundedCornerShape(Dimens.CornerRadiusSmall),  // Buttons, Text Fields
    medium = RoundedCornerShape(Dimens.CornerRadiusMedium), // Cards, Dialogs
    large = RoundedCornerShape(Dimens.CornerRadiusLarge)  // Large containers, Sheets
)