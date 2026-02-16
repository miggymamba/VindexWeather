package com.miguelrivera.vindexweather.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.data.local.datastore.SettingsDataStore
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.location.LocationTracker
import com.miguelrivera.vindexweather.domain.model.Coordinates
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.domain.usecase.GetPagedWeatherUseCase
import com.miguelrivera.vindexweather.domain.usecase.SyncWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Presentation layer orchestrator for the Dashboard.
 *
 * Responsibilities:
 * 1. Manages data streams by combining persistent settings with transient operation states.
 * 2. Uses [stateIn] to produce a hot, lifecycle-aware UI state flow.
 * 3. Triggers domain use cases for synchronization and setting updates.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPagedWeatherUseCase: GetPagedWeatherUseCase,
    private val syncWeatherUseCase: SyncWeatherUseCase,
    private val locationTracker: LocationTracker,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val defaultCoordinates = Coordinates(35.6809843, 139.7621861)
    private val _currentCoordinates = MutableStateFlow(defaultCoordinates)

    /**
     * Cold stream of weather data.
     * [cachedIn] ensures the paging state survives configuration changes.
     */
    val weatherPagingDataFlow: Flow<PagingData<Weather>> =
        _currentCoordinates.flatMapLatest { coords ->
            getPagedWeatherUseCase(latitude = coords.latitude, longitude = coords.longitude)
        }.cachedIn(viewModelScope)

    // Internal mutable states for transient sync operations
    private val _isSyncing = MutableStateFlow(false)
    private val _syncError = MutableStateFlow<String?>(null)

    /**
     * The single source of truth for the UI state.
     *
     * Combines transient sync state with the persistent temperature unit preference.
     * Uses [SharingStarted.WhileSubscribed] (5000ms) to stop upstream collection when the UI
     * is not visible, preventing memory leaks and wasted resources.
     */
    val uiState: StateFlow<DashboardState> = combine(
        _isSyncing,
        _syncError,
        settingsDataStore.temperatureUnit
    ) { isSyncing, error, unit ->
        DashboardState(
            isSyncing,
            error,
            unit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    /**
     * Initiates a weather synchronization.
     *
     * Attempts to resolve the current device location. If permission is denied or location
     * is unavailable, falls back to default coordinates (Tokyo). Updates the coordinate flow
     * if the location has changed.
     */
    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null

            val location = locationTracker.getCurrentLocation()
            val targetCoords = location ?: defaultCoordinates

            if (targetCoords != _currentCoordinates.value) {
                _currentCoordinates.value = targetCoords
            }

            val result = syncWeatherUseCase(latitude = targetCoords.latitude,
                longitude = targetCoords.longitude
            )

            when (result) {
                is Result.Success -> _isSyncing.value = false
                is Result.Error -> {
                    _isSyncing.value = false
                    _syncError.value = result.message ?: "Unknown Sync Error."
                }

                is Result.Loading -> _isSyncing.value = true
            }
        }
    }

    /**
     * Updates the user's preferred temperature unit in the persistent store.
     *
     * @param unit The new [TemperatureUnit] to persist.
     */
    fun toggleTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsDataStore.setTemperatureUnit(unit)
        }
    }

    /**
     * Resets the transient error message state.
     * Should be called by the UI after the error has been displayed (e.g., in a Snackbar).
     */
    fun clearError() {
        _syncError.value = null
    }
}