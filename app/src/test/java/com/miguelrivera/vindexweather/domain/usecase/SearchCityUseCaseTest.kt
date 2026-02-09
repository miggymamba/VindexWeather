package com.miguelrivera.vindexweather.domain.usecase

import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchCityUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private val useCase = SearchCityUseCase(repository)

    @Test
    fun `invoke returns success with empty list when query is blank`() = runTest {
        // Act
        val resultBlank = useCase("    ")
        val resultEmpty = useCase("")

        // Assert
        assertTrue(resultBlank is Result.Success && resultBlank.data.isEmpty())
        assertTrue(resultEmpty is Result.Success && resultEmpty.data.isEmpty())

        // Verify repository was NEVER called
        coVerify(exactly = 0) { repository.searchCity(any()) }
    }

    @Test
    fun `invoke calls repository SearchCity when query is valid`() = runTest {
        // Arrange
        val query = "Tokyo"
        val expectedCity = City(
            "Tokyo",
            35.68158085202552,
            139.7670328103394,
            "Japan",
            null
        )
        coEvery { repository.searchCity(query) } returns Result.Success(listOf(expectedCity))

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(expectedCity, (result as Result.Success).data.first())

        // Verify
        coVerify(exactly = 1) { repository.searchCity(query) }
    }
}