package com.miguelrivera.vindexweather.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.miguelrivera.vindexweather.domain.model.Coordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStore @Inject constructor(
    private val datastore: DataStore<Preferences>
) {
    private val UNIT_KEY = stringPreferencesKey(KEY_TEMPERATURE_UNIT)
    private val LAT_KEY = doublePreferencesKey(KEY_LATITUDE)
    private val LON_KEY = doublePreferencesKey(KEY_LONGITUDE)

    val temperatureUnit: Flow<TemperatureUnit> = datastore.data
        .map { preferences ->
            val value = preferences[UNIT_KEY] ?: TemperatureUnit.METRIC.name
            TemperatureUnit.valueOf(value)
        }

    val selectedLocation: Flow<Coordinates?> = datastore.data
        .map { preferences ->
            val lat = preferences[LAT_KEY]
            val lon = preferences[LON_KEY]

            if (lat != null && lon != null) {
                Coordinates(lat, lon)
            } else {
                null
            }
        }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        datastore.edit { preferences ->
            preferences[UNIT_KEY] = unit.name
        }
    }

    suspend fun saveLocation(coordinates: Coordinates) {
        datastore.edit { preferences ->
            preferences[LAT_KEY] = coordinates.latitude
            preferences[LON_KEY] = coordinates.longitude
        }
    }

    companion object {
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        const val KEY_LATITUDE = "selected_latitude"
        const val KEY_LONGITUDE = "selected_longitude"
    }
}

enum class TemperatureUnit {
    METRIC, IMPERIAL
}