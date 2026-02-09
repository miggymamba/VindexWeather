package com.miguelrivera.vindexweather.domain.model

/**
 * Domain representation of a city location.
 * Used to present search results to the user.
 */
data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val state: String?
)