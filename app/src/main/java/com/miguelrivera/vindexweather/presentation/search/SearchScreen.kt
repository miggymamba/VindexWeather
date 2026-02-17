package com.miguelrivera.vindexweather.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.domain.model.Coordinates
import com.miguelrivera.vindexweather.presentation.LocalNavActions
import com.miguelrivera.vindexweather.presentation.search.components.CityResultItem
import com.miguelrivera.vindexweather.presentation.search.components.SearchShimmerItem
import com.miguelrivera.vindexweather.presentation.search.components.VindexSearchBar
import com.miguelrivera.vindexweather.presentation.theme.Dimens
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme

/**
 * Stateful entry point for the City Search feature.
 *
 * Responsibilities:
 * 1. Collects UI state from [SearchViewModel].
 * 2. Manages the search query input interaction.
 * 3. Navigates back upon successful selection or cancellation.
 * 4. Delegates rendering to the stateless [SearchLayout] using the Slot API.
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val navActions = LocalNavActions.current
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchLayout(
        searchBarSlot = {
            VindexSearchBar(
                query = query,
                onQueryChange = viewModel::onQueryChanged,
                onClearClick = { viewModel.onQueryChanged("") }
            )
        },
        resultsSlot = {
            when (val state = uiState) {
                is SearchState.Idle -> {
                    CenteredMessage(stringResource(R.string.search_start_prompt))
                }
                is SearchState.Loading -> {
                    LazyColumn {
                        items(5) { SearchShimmerItem() }
                    }
                }
                is SearchState.Empty -> {
                    CenteredMessage(stringResource(R.string.search_no_results))
                }
                is SearchState.Error -> {
                    CenteredMessage(state.message)
                }
                is SearchState.Success -> {
                    LazyColumn{
                        items(state.results) { city ->
                            CityResultItem(
                                city = city,
                                onClick = {
                                    // Pass selected city coordinates back to the caller (Dashboard)
                                    navActions.navigateBackBackWithResult(
                                        Coordinates(city.latitude,city.longitude)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

/**
 * Stateless layout component for the Search Screen.
 *
 * Decouples the fixed search bar area from the dynamic results area.
 *
 * @param searchBarSlot The slot for the search input field.
 * @param resultsSlot The slot for the search results list, loading state, or messages.
 */
@Composable
fun SearchLayout(
    searchBarSlot: @Composable () -> Unit,
    resultsSlot: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(Dimens.PaddingMedium)
        ) {
            searchBarSlot()
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Box(modifier = Modifier.fillMaxSize()) {
                resultsSlot()
            }
        }
    }
}

/**
 * Utility component for displaying centered status messages (Idle, Empty, Error).
 */
@Composable
private fun CenteredMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- Previews ---

@Preview(name = "Idle State", showBackground = true)
@Composable
private fun SearchLayoutIdlePreview() {
    VindexWeatherTheme {
        SearchLayout(
            searchBarSlot = { VindexSearchBar("", {}, {}) },
            resultsSlot = { CenteredMessage(stringResource(R.string.search_start_prompt)) }
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun SearchLayoutLoadingPreview() {
    VindexWeatherTheme {
        SearchLayout(
            searchBarSlot = { VindexSearchBar("Lon", {}, {}) },
            resultsSlot = {
                LazyColumn {
                    items(5) { SearchShimmerItem() }
                }
            }
        )
    }
}

@Preview(name = "Success State (Dark)", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchLayoutSuccessPreview() {
    val sampleCities = listOf(
        City("London", 51.5, -0.1, "GB", "England"),
        City("London", 42.9, -81.2, "CA", "Ontario")
    )
    VindexWeatherTheme {
        SearchLayout(
            searchBarSlot = { VindexSearchBar("London", {}, {}) },
            resultsSlot = {
                LazyColumn {
                    items(sampleCities) { city ->
                        CityResultItem(city, {})
                    }
                }
            }
        )
    }
}