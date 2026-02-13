package com.miguelrivera.vindexweather.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Different corner radius values for the application
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),  // Buttons, Text Fields
    medium = RoundedCornerShape(12.dp), // Cards, Dialogs
    large = RoundedCornerShape(16.dp)  // Large containers, Sheets
)