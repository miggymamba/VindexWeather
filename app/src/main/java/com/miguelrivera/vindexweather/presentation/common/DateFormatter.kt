package com.miguelrivera.vindexweather.presentation.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility for formatting Unix timestamps into readable date strings.
 */
object DateFormatter {

    /**
     * Formats a Unix timestamp (in seconds) to a pattern like "EEE, h:mm a" (e.g., "Mon, 2:00 PM").
     *
     * @param timestampSeconds Unix timestamp in seconds (standard OpenWeatherMap format).
     * @param locale The locale to format the date for. Defaults to system default.
     */
    fun formatDayTime(timestampSeconds: Long, locale: Locale = Locale.getDefault()): String {
        val date = Date(timestampSeconds * 1000L)
        val formatter = SimpleDateFormat("EEE, h:mm a", locale)
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }
}