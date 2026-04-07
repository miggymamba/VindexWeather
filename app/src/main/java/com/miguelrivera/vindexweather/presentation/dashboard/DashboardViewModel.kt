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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(DashboardUiState(coordinates = defaultCoordinates))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    /**
     * Cold stream of weather data.
     * [cachedIn] ensures the paging state survives configuration changes.
     */
    val weatherPagingDataFlow: Flow<PagingData<Weather>> =
        _uiState
            .map { it.coordinates }
            .distinctUntilChanged()
            .flatMapLatest { coords ->
                getPagedWeatherUseCase(latitude = coords.latitude, longitude = coords.longitude)
            }
            .cachedIn(viewModelScope)

    init {
        // Initialize from DataStore. If a location was saved, use it.
        viewModelScope.launch {
            settingsDataStore.selectedLocation.collect { savedCoords ->
                if (savedCoords != null && savedCoords != _uiState.value.coordinates) {
                    _uiState.update { it.copy(coordinates = savedCoords) }
                }
            }
        }
        viewModelScope.launch {
            settingsDataStore.temperatureUnit.collect { unit ->
                _uiState.update { it.copy(temperatureUnit = unit) }
            }
        }
    }

    /**
     * Single entry point for all UI events and system interactions.
     *
     * Routes incoming [DashboardIntent]s to their respective private handlers. This guarantees
     * that all state mutations occur atomically and prevents race conditions from concurrent
     * UI triggers.
     *
     * @param intent The specific action to be processed.
     */
    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.Refresh -> triggerSync()
            is DashboardIntent.UpdateLocation -> updateLocation(intent.coordinates)
            is DashboardIntent.ClearError -> clearError()
            is DashboardIntent.ToggleUnit -> toggleTemperatureUnit(intent.unit)
        }
    }

    /**
     * Initiates a weather synchronization.
     *
     * Attempts to resolve the current device location ONLY if app doesn't have a saved location
     * or explicit user intent to "use current location".
     *
     * For this MVP: Pull-to-refresh syncs the *currently displayed* location.
     */
    private fun triggerSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }

            // Use the current coordinates (restored from DataStore or Default)
            var targetCoords = _uiState.value.coordinates

            // If the app is still at default coordinates (and haven't loaded from DataStore yet),
            // try to get GPS. This handles the "First Run" scenario.
            // Check if DataStore is empty to decide if the app should override with GPS.
            val savedLocation = settingsDataStore.selectedLocation.first()
            if (savedLocation == null && targetCoords == defaultCoordinates) {
                locationTracker.getCurrentLocation()?.let { gpsCoords ->
                    targetCoords = gpsCoords
                    // Save GPS location as the selected location so it persists
                    processIntent(DashboardIntent.UpdateLocation(gpsCoords))
                }
            }

            val result = syncWeatherUseCase(
                latitude = targetCoords.latitude,
                longitude = targetCoords.longitude
            )

            when (result) {
                is Result.Success -> _uiState.update { it.copy(isSyncing = false) }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            error = result.message ?: "Unknown Sync Error."
                        )
                    }
                }

                is Result.Loading -> _uiState.update { it.copy(isSyncing = true) }
            }
        }
    }

    /**
     * Updates the current location coordinates for the weather feed and persists it.
     * This triggers the Pager to fetch new data from the DB/API automatically via [flatMapLatest].
     *
     * @param coordinates The new location selected by the user.
     */
    private fun updateLocation(coordinates: Coordinates) {
        if (_uiState.value.coordinates != coordinates) {
            _uiState.update { it.copy(coordinates = coordinates) }
            // Persist to DataStore
            viewModelScope.launch {
                settingsDataStore.saveLocation(coordinates)
            }
            processIntent(DashboardIntent.Refresh)
        }
    }

    /**
     * Updates the user's preferred temperature unit in the persistent store.
     *
     * @param unit The new [TemperatureUnit] to persist.
     */
    private fun toggleTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsDataStore.setTemperatureUnit(unit)
        }
    }

    /**
     * Resets the transient error message state.
     * Should be called by the UI after the error has been displayed (e.g., in a Snackbar).
     */
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}