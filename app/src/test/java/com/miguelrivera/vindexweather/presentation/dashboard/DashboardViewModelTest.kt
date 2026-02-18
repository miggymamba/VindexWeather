package com.miguelrivera.vindexweather.presentation.dashboard

import com.miguelrivera.vindexweather.data.local.datastore.SettingsDataStore
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.location.LocationTracker
import com.miguelrivera.vindexweather.domain.model.Coordinates
import com.miguelrivera.vindexweather.domain.usecase.GetPagedWeatherUseCase
import com.miguelrivera.vindexweather.domain.usecase.SyncWeatherUseCase
import com.miguelrivera.vindexweather.rules.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [DashboardViewModel].
 * Verifies integration between UI state, DataStore preferences, and Domain use cases.
 */
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPagedWeatherUseCase: GetPagedWeatherUseCase = mockk(relaxed = true)
    private val syncWeatherUseCase: SyncWeatherUseCase = mockk(relaxed = true)
    private val locationTracker: LocationTracker = mockk(relaxed = true)
    private val settingsDataStore: SettingsDataStore = mockk(relaxed = true)

    private lateinit var dashboardViewModel: DashboardViewModel

    @Test
    fun `init loads saved location from DataStore`() = runTest {
        // Arrange
        val savedLocation = Coordinates(10.0, 10.0)
        // Mock DataStore flow to emit a saved location immediately
        every { settingsDataStore.selectedLocation } returns flowOf(savedLocation)
        every { settingsDataStore.temperatureUnit } returns flowOf(TemperatureUnit.METRIC)

        // Act
        dashboardViewModel = DashboardViewModel(getPagedWeatherUseCase, syncWeatherUseCase, locationTracker, settingsDataStore)

        // Ensure the paging flow is collected so flatMapLatest executes
        val job = launch(UnconfinedTestDispatcher()) {
            dashboardViewModel.weatherPagingDataFlow.collect {}
        }

        // Assert
        // Verify that the Paging Flow was requested with the saved coordinates.
        coVerify(timeout = 1000) { getPagedWeatherUseCase(10.0, 10.0) }

        job.cancel()
    }

    @Test
    fun `updateLocation persists to DataStore and triggers sync`() = runTest {
        // Arrange
        val newLocation = Coordinates(20.0, 20.0)
        every { settingsDataStore.selectedLocation } returns flowOf(null) // Start default
        every { settingsDataStore.temperatureUnit } returns flowOf(TemperatureUnit.METRIC)

        dashboardViewModel = DashboardViewModel(getPagedWeatherUseCase, syncWeatherUseCase, locationTracker, settingsDataStore)

        // Act
        dashboardViewModel.updateLocation(newLocation)

        // Assert
        coVerify { settingsDataStore.saveLocation(newLocation) }
        coVerify { syncWeatherUseCase(20.0, 20.0) }
    }

    @Test
    fun `toggleTemperatureUnit updates DataStore`() = runTest {
        // Arrange
        every { settingsDataStore.selectedLocation } returns flowOf(null)
        every { settingsDataStore.temperatureUnit } returns flowOf(TemperatureUnit.METRIC)
        dashboardViewModel = DashboardViewModel(getPagedWeatherUseCase, syncWeatherUseCase, locationTracker, settingsDataStore)

        // Act
        dashboardViewModel.toggleTemperatureUnit(TemperatureUnit.IMPERIAL)

        // Assert
        coVerify { settingsDataStore.setTemperatureUnit(TemperatureUnit.IMPERIAL) }
    }
}