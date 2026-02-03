package com.miguelrivera.vindexweather.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStore @Inject constructor(
    private val datastore: DataStore<Preferences>
) {
    private val UNIT_KEY = stringPreferencesKey(KEY_TEMPERATURE_UNIT)

    val temperatureUnit: Flow<TemperatureUnit> = datastore.data
        .map { preferences ->
            val value = preferences[UNIT_KEY] ?: TemperatureUnit.METRIC.name
            TemperatureUnit.valueOf(value)
        }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        datastore.edit { preferences ->
            preferences[UNIT_KEY] = unit.name
        }
    }

    companion object {
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
    }
}

enum class TemperatureUnit {
    METRIC, IMPERIAL
}