package com.miguelrivera.vindexweather.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.domain.usecase.GetPagedWeatherUseCase
import com.miguelrivera.vindexweather.domain.usecase.SyncWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 *
 * Responsibilities:
 * 1. Exposes the PagingData stream for infinite scrolling.
 * 2. Manages transient UI states (Syncing/Error) via [DashboardState].
 * 3. Triggers manual data synchronization.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPagedWeatherUseCase: GetPagedWeatherUseCase,
    private val syncWeatherUseCase: SyncWeatherUseCase
) : ViewModel() {

    private val defaultLat = 35.6809843
    private val defaultLon = 139.7621861

    /**
     * Cold stream of weather data.
     * [cachedIn] ensures the paging state survives configuration changes.
     */
    val weatherPagingDataFlow: Flow<PagingData<Weather>> = getPagedWeatherUseCase(latitude = defaultLat, longitude = defaultLon)
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        triggerSync()
    }

    /**
     * Triggers a remote synchronization of weather data.
     * Used for initial load and pull-to-refresh interactions.
     */
    fun triggerSync() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Syncing

            val result = syncWeatherUseCase(latitude = defaultLat, longitude = defaultLon)

            _uiState.value = when (result) {
                is Result.Success -> DashboardState.Idle
                is Result.Error -> DashboardState.Error(result.message ?: "Unknown Sync Error.")
                is Result.Loading -> DashboardState.Syncing
            }
        }
    }
}