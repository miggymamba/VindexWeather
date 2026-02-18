package com.miguelrivera.vindexweather.presentation.search

import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.domain.usecase.SearchCityUseCase
import com.miguelrivera.vindexweather.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [SearchViewModel].
 * Verifies debouncing logic, state transitions, and interaction with the [SearchCityUseCase].
 */
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchCityUseCase: SearchCityUseCase = mockk()
    private lateinit var searchViewModel: SearchViewModel

    @Test
    fun `onQueryChanged updates query state immediately`() {
        searchViewModel = SearchViewModel(searchCityUseCase)

        searchViewModel.onQueryChanged("Tok")

        assertEquals("Tok", searchViewModel.query.value)
    }

    @Test
    fun `search is debounced and triggered after delay`() = runTest {
        // Arrange
        val query = "Tokyo"
        val cities = listOf(
            City(
                name = "Tokyo",
                latitude = 35.6809843,
                longitude = 139.7621861,
                country = "JP",
                state = null
            )
        )
        coEvery { searchCityUseCase(query) } returns Result.Success(cities)

        searchViewModel = SearchViewModel(searchCityUseCase)

        // Act
        searchViewModel.onQueryChanged("T")
        searchViewModel.onQueryChanged("To")
        searchViewModel.onQueryChanged("Tok")
        searchViewModel.onQueryChanged("Toky")
        searchViewModel.onQueryChanged(query) // Final query

        // Advance time to allow debounce (300ms) to pass
        advanceTimeBy(301L)

        // Assert
        // Verify use case was called EXACTLY ONCE with final query
        coVerify(exactly = 1) { searchCityUseCase(query) }

        // Verify state is SearchState.Success
        val state = searchViewModel.uiState.value
        assertTrue(state is SearchState.Success)
        assertEquals(cities, (state as SearchState.Success).results)
    }

    @Test
    fun `search error updates state to Error`() = runTest {
        // Arrange
        val query = "Unknown"
        val errorMessage = "Network Error"
        coEvery { searchCityUseCase(query) } returns Result.Error(message = errorMessage)

        searchViewModel = SearchViewModel(searchCityUseCase)

        // Act
        searchViewModel.onQueryChanged(query)
        advanceTimeBy(301)

        // Assert
        val state = searchViewModel.uiState.value
        assertTrue(state is SearchState.Error)
        assertEquals(errorMessage, (state as SearchState.Error).message)
    }
}