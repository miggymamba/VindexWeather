package com.miguelrivera.vindexweather.domain.usecase

import androidx.paging.PagingData
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class GetPagedWeatherUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private val useCase = GetPagedWeatherUseCase(repository)

    @Test
    fun `invoke delegates to repository getPagedWeather`() {
        // Arrange
        val lat = 35.0
        val lon = 139.0
        val expectedFlow = flowOf(PagingData.from(emptyList<Weather>()))

        every { repository.getPagedWeather(lat, lon) } returns expectedFlow

        // Act
        val result = useCase(lat, lon)

        // Assert
        verify(exactly = 1) { repository.getPagedWeather(lat, lon) }
        assert(result == expectedFlow)
    }
}