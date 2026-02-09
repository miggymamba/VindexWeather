package com.miguelrivera.vindexweather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for the Geocoding API response.
 *
 * @property name Name of the found location.
 * @property lat Geographical latitude.
 * @property lon Geographical longitude.
 * @property country Country code (e.g., "GB", "US").
 * @property state State or region name (optional, e.g., "Texas").
 */
@Serializable
data class GeocodingDto(
    @SerialName("name") val name: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("country") val country: String,
    @SerialName("state") val state: String? = null
)
