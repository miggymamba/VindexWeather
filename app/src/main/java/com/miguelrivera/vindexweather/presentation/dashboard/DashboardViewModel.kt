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
import kotlinx.coroutines.flow.first
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

    private val defaultCoordinates = Coordinates(35.6809843, 139.7621861) // Tokyo
    private val _currentCoordinates = MutableStateFlow(defaultCoordinates)

    init {
        // Initialize from DataStore. If a location was saved, use it.
        viewModelScope.launch {
            settingsDataStore.selectedLocation.collect { savedCoords ->
                if (savedCoords != null && savedCoords != _currentCoordinates.value) {
                    _currentCoordinates.value = savedCoords
                }
            }
        }
    }

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
     * Attempts to resolve the current device location ONLY if we don't have a saved location
     * or explicit user intent to "use current location".
     *
     * For this MVP: Pull-to-refresh syncs the *currently displayed* location.
     */
    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null

            // Use the current coordinates (restored from DataStore or Default)
            var targetCoords = _currentCoordinates.value

            // If we are still at default coordinates (and haven't loaded from DataStore yet),
            // try to get GPS. This handles the "First Run" scenario.
            // We check if DataStore is empty to decide if we should override with GPS.
            val savedLocation = settingsDataStore.selectedLocation.first()
            if (savedLocation == null && targetCoords == defaultCoordinates) {
                locationTracker.getCurrentLocation()?.let { gpsCoords ->
                    targetCoords = gpsCoords
                    // Save GPS location as the selected location so it persists
                    updateLocation(gpsCoords)
                }
            }

            val result = syncWeatherUseCase(
                latitude = targetCoords.latitude,
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
     * Updates the current location coordinates for the weather feed and persists it.
     * This triggers the Pager to fetch new data from the DB/API automatically via [flatMapLatest].
     *
     * @param coordinates The new location selected by the user.
     */
    fun updateLocation(coordinates: Coordinates) {
        if (_currentCoordinates.value != coordinates) {
            _currentCoordinates.value = coordinates
            // Persist to DataStore
            viewModelScope.launch {
                settingsDataStore.saveLocation(coordinates)
            }
            triggerSync()
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