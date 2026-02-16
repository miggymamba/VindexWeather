package com.miguelrivera.vindexweather.presentation.common

import android.content.Context
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit

/**
 * Utility object responsible for domain-agnostic temperature formatting.
 *
 * Handles the conversion of raw temperature data from the canonical Metric source (Celsius)
 * to the target display unit using Android's resource system for locale-aware formatting.
 */

object TemperatureFormatter {

    /**
     * Formats a raw Celsius value into a localized string with the appropriate unit suffix.
     *
     * @param context Context required to fetch localized string resources.
     * @param celsiusTemp The temperature value in Celsius (Source of Truth).
     * @param unit The target unit preference (Metric/Imperial).
     * @return A localized, formatted string (e.g., "24°C" or "75°F").
     */
    fun format(context: Context, celsiusTemp: Double, unit: TemperatureUnit): String {
        val convertedTemp = when (unit) {
            TemperatureUnit.METRIC -> celsiusTemp
            TemperatureUnit.IMPERIAL -> (celsiusTemp * 9 / 5) + 32
        }

        val resId = when (unit) {
            TemperatureUnit.METRIC -> R.string.format_temperature_celsius
            TemperatureUnit.IMPERIAL -> R.string.format_temperature_fahrenheit
        }

        return context.getString(resId, convertedTemp.toString())
    }
}