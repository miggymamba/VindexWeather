package com.miguelrivera.vindexweather.domain.model

/**
 * Pure domain representation of a geographical location.
 * Decouples the app logic from the Android Framework's [android.location.Location] object.
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
