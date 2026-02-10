package com.miguelrivera.vindexweather.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.domain.usecase.SearchCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the City Search screen.
 *
 * Handles query debouncing and executes search requests via the Domain layer.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCityUseCase: SearchCityUseCase
): ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchState>(SearchState.Idle)
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    init {
        observeQueryChanges()
    }

    /**
     * Updates the current query state.
     * Called by the UI when the user types in the search bar.
     */
    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchState.Idle
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChanges() {
        viewModelScope.launch {
            _query
                .debounce(300) // Prevents API spamming while typing
                .distinctUntilChanged() // Ignores duplicate consecutive queries
                .filter { it.isNotBlank() } // Prevents searching for empty strings
                .collectLatest { validQuery ->
                    // collectLatest cancels the previous block if a new query emits
                    performSearch(validQuery)
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = SearchState.Loading

        val result = searchCityUseCase(query)

        _uiState.value = when (result) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Success(result.data)
                }
            }
            is Result.Error -> {
                SearchState.Error(result.message ?: "Search failed.")
            }
            is Result.Loading -> {
                SearchState.Loading
            }
        }
    }
}