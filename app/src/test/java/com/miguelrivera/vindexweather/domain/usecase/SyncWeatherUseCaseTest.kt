package com.miguelrivera.vindexweather.domain.usecase

import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class SyncWeatherUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private val useCase = SyncWeatherUseCase(repository)

    @Test
    fun `invoke calls repository syncWeather and returns success`() = runTest {
        // Arrange
        val lat = 35.0
        val lon = 139.0
        coEvery { repository.syncWeather(lat, lon) } returns Result.Success(Unit)

        // Act
        val result = useCase(lat, lon)

        // Assert
        assert(result is Result.Success)
        coVerify(exactly = 1) { repository.syncWeather(lat, lon) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Arrange
        val lat = 35.0
        val lon = 139.0
        val expectedError = IOException("Network error")
        coEvery { repository.syncWeather(lat, lon) } returns Result.Error(expectedError)

        // Act
        val result = useCase(lat, lon)

        // Assert
        assert(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).exception)
        coVerify(exactly = 1) { repository.syncWeather(lat, lon) }
    }
}