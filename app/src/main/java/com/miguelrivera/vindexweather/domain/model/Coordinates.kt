package com.miguelrivera.vindexweather.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * Pure domain representation of a geographical location.
 * Decouples the app logic from the Android Framework's [android.location.Location] object.
 *
 * Implements [Parcelable] to be passable via Navigation Arguments or SavedStateHandle.
 */
@Parcelize
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) : Parcelable
