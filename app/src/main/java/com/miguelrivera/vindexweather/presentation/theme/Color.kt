package com.miguelrivera.vindexweather.presentation.theme

import androidx.compose.ui.graphics.Color

// Vindex Brand Palette
// Design System: "Atmospheric Clarity"

// Brand Primaries
val WeatherBlue = Color(0xFF0061A4)
val WeatherBlueContainer = Color(0xFFD1E4FF)
val DeepNavy = Color(0xFF001D36)

val SoftBlue = Color(0xFF9ECAFF)
val SoftBlueContainer = Color(0xFF00497D)
val DarkNavy = Color(0xFF003258)

// Neutrals
val StormGrey = Color(0xFF535F70)
val MistGrey = Color(0xFFBBC7DB)
val CloudWhite = Color(0xFFFFFFFF)
val NightSky = Color(0xFF101C2B)
val Carbon = Color(0xFF1A1C1E)
val Steel = Color(0xFFE2E2E6)
val ShadowGrey = Color(0xFF253140)
val DeepStorm = Color(0xFF3B4858)
val LightMist = Color(0xFFD7E3F7)
val PaperWhite = Color(0xFFFDFCFF)

// Functional
val AlertRed = Color(0xFFBA1A1A)
val SoftRed = Color(0xFFFFB4AB)
val DarkRed = Color(0xFF690005)

// --- Material 3 Mapping ---

// Light Colors
val md_theme_light_primary = WeatherBlue
val md_theme_light_onPrimary = CloudWhite
val md_theme_light_primaryContainer = WeatherBlueContainer
val md_theme_light_onPrimaryContainer = DeepNavy
val md_theme_light_secondary = StormGrey
val md_theme_light_onSecondary = CloudWhite
val md_theme_light_secondaryContainer = LightMist
val md_theme_light_onSecondaryContainer = NightSky
val md_theme_light_error = AlertRed
val md_theme_light_onError = CloudWhite
val md_theme_light_background = PaperWhite
val md_theme_light_onBackground = Carbon
val md_theme_light_surface = PaperWhite
val md_theme_light_onSurface = Carbon

// Dark Colors
val md_theme_dark_primary = SoftBlue
val md_theme_dark_onPrimary = DarkNavy
val md_theme_dark_primaryContainer = SoftBlueContainer
val md_theme_dark_onPrimaryContainer = WeatherBlueContainer
val md_theme_dark_secondary = MistGrey
val md_theme_dark_onSecondary = ShadowGrey
val md_theme_dark_secondaryContainer = DeepStorm
val md_theme_dark_onSecondaryContainer = LightMist
val md_theme_dark_error = SoftRed
val md_theme_dark_onError = DarkRed
val md_theme_dark_background = Carbon
val md_theme_dark_onBackground = Steel
val md_theme_dark_surface = Carbon
val md_theme_dark_onSurface = Steel