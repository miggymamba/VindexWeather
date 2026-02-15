package com.miguelrivera.vindexweather.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.miguelrivera.vindexweather.core.common.Result
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 *
 * Responsibilities:
 * 1. Exposes the PagingData stream for infinite scrolling.
 * 2. Manages transient UI states (Syncing/Error) via [DashboardState].
 * 3. Triggers manual data synchronization using the device's actual location.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPagedWeatherUseCase: GetPagedWeatherUseCase,
    private val syncWeatherUseCase: SyncWeatherUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    // Default fallback (Tokyo) if permission is denied or location is off
    private val defaultCoordinates = Coordinates(35.6809843, 139.7621861)

    // Helper to track the current target location for the weather stream
    private val _currentCoordinates = MutableStateFlow(defaultCoordinates)

    /**
     * Cold stream of weather data.
     * [cachedIn] ensures the paging state survives configuration changes.
     */
    val weatherPagingDataFlow: Flow<PagingData<Weather>> =
        _currentCoordinates.flatMapLatest { coords ->
            getPagedWeatherUseCase(latitude = coords.latitude, longitude = coords.longitude)
        }.cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    /**
     * Triggers a remote synchronization of weather data.
     *
     * This method attempts to fetch the *real* device location.
     * If successful, it updates the [_currentCoordinates] (which refreshes the UI stream)
     * and calls the Sync UseCase.
     */
    fun triggerSync() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Syncing

            // Try to get actual location (requires permission)
            val location = locationTracker.getCurrentLocation()

            // Determine which coordinates to use (Real vs Default)
            val targetCoords = location ?: defaultCoordinates

            // Update the stream source if we found a new location
            if (targetCoords != _currentCoordinates.value) {
                _currentCoordinates.value = targetCoords
            }

            val result = syncWeatherUseCase(
                latitude = targetCoords.latitude,
                longitude = targetCoords.longitude
            )

            _uiState.value = when (result) {
                is Result.Success -> DashboardState.Idle
                is Result.Error -> DashboardState.Error(result.message ?: "Unknown Sync Error.")
                is Result.Loading -> DashboardState.Syncing
            }
        }
    }
}