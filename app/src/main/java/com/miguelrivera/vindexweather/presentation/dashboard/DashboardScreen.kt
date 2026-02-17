package com.miguelrivera.vindexweather.presentation.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.presentation.LocalNavActions
import com.miguelrivera.vindexweather.presentation.LocalWindowSize
import com.miguelrivera.vindexweather.presentation.common.LocationRationaleDialog
import com.miguelrivera.vindexweather.presentation.common.RequestLocationPermission
import com.miguelrivera.vindexweather.presentation.dashboard.components.UnitToggleControl
import com.miguelrivera.vindexweather.presentation.dashboard.components.WeatherCard
import com.miguelrivera.vindexweather.presentation.theme.Dimens
import kotlinx.coroutines.launch

/**
 * The stateful entry point for the Dashboard feature.
 *
 * Responsibilities:
 * 1. Orchestrates the interaction between the [DashboardViewModel] (State Holder) and the [DashboardLayout] (UI).
 * 2. Manages side effects such as Location Permissions, Snackbars, and Navigation actions.
 * 3. Observes the [DashboardState] via [collectAsStateWithLifecycle] to ensure UI updates are lifecycle-aware.
 *
 * This component remains decoupled from specific layout implementations by delegating rendering
 * to [DashboardLayout].
 */
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val navActions = LocalNavActions.current
    val windowSize = LocalWindowSize.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val weatherPagingItems = viewModel.weatherPagingDataFlow.collectAsLazyPagingItems()

    // Resources extracted for use within CoroutineScope
    val deniedMessage = stringResource(R.string.location_denied_fallback)
    val requiredMessage = stringResource(R.string.location_required_generic)

    // Permission Flow State
    var showRationaleDialog by remember { mutableStateOf(false) }
    var permissionContinuation by remember { mutableStateOf({ }) }

    val isExpanded = windowSize.widthSizeClass != WindowWidthSizeClass.Compact

    // Event Consumption: Display errors via Snackbar and reset state
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            scope.launch {
                snackbarHostState.showSnackbar(message = errorMessage)
                viewModel.clearError()
            }
        }
    }

    // Headless effect: Request permission on start, handling all edge cases
    RequestLocationPermission(
        onPermissionGranted = {
            viewModel.triggerSync()
        },
        onPermissionDenied = {
            viewModel.triggerSync()
            scope.launch {
                snackbarHostState.showSnackbar(message = deniedMessage)
            }
        },
        onShowRationale = { continueRequest ->
            permissionContinuation = continueRequest
            showRationaleDialog = true
        }
    )

    if (showRationaleDialog) {
        LocationRationaleDialog(
            onConfirm = {
                showRationaleDialog = false
                permissionContinuation() // Proceed with system dialog
            },
            onDismiss = {
                showRationaleDialog = false
                // If dismissed without action, treat as denial and fallback
                viewModel.triggerSync()
                scope.launch {
                    snackbarHostState.showSnackbar(requiredMessage)
                }
            }
        )
    }

    PullToRefreshBox(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.triggerSync() },
        modifier = modifier.fillMaxSize()
    ) {
        DashboardLayout(
            isExpanded = isExpanded,
            currentUnit = uiState.temperatureUnit,
            onUnitToggled = viewModel::toggleTemperatureUnit,
            weatherSlot = {
                WeatherPagedList(
                    weatherItems = weatherPagingItems,
                    temperatureUnit = uiState.temperatureUnit
                )
            },
            searchActionSlot = {
                Button(onClick = { navActions.navigateToSearch() }) {
                    Text(text = stringResource(R.string.dashboard_action_search))
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )
    }
}

/**
 * A stateless, adaptive layout component responsible for rendering the Dashboard UI.
 *
 * Utilizes the Slot API pattern ([weatherSlot], [searchActionSlot]) to decouple the
 * structural layout from specific content implementations. This enables isolated testing,
 * easier previews, and flexibility for future UI changes.
 *
 * Note: The [weatherSlot] is contained within a Box with `weight(1f)` to ensure the LazyColumn
 * inside it consumes available space properly without nested scrolling conflicts.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param isExpanded Dictates the structural layout (Row vs Column) for responsive design (e.g., Tablets).
 * @param currentUnit The current display unit for the toggle control.
 * @param onUnitToggled Event callback for the unit toggle interaction.
 * @param weatherSlot Slot for main weather content injection (typically a List).
 * @param searchActionSlot Slot for search action/input injection.
 * @param snackbarHost Slot for snackbar host placement.
 */
@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    currentUnit: TemperatureUnit,
    onUnitToggled: (TemperatureUnit) -> Unit,
    weatherSlot: @Composable () -> Unit,
    searchActionSlot: @Composable () -> Unit,
    snackbarHost: @Composable () -> Unit = { }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = snackbarHost
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        if (isExpanded) {
            Row(
                modifier = contentModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.PaddingMedium)
                    ) {
                        UnitToggleControl(currentUnit, onUnitToggled)
                        // Weather Slot takes remaining space
                        Box(modifier = Modifier.weight(1f)) {
                            weatherSlot()
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    searchActionSlot()
                }
            }
        } else {
            Column(
                modifier = contentModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.PaddingMedium),
                    horizontalArrangement = Arrangement.End
                ) {
                    UnitToggleControl(currentUnit, onUnitToggled)
                }

                // Weather Slot takes remaining space above the search button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingMedium)
                ) {
                    weatherSlot()
                }

                Box(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                    searchActionSlot()
                }
            }
        }
    }
}

/**
 * Internal component to handle Paging 3 LoadStates and list rendering.
 *
 * @param weatherItems The lazy paging items collected from the PagingData flow.
 * @param temperatureUnit The current unit for temperature display.
 */
@Composable
private fun WeatherPagedList(
    weatherItems: LazyPagingItems<Weather>,
    temperatureUnit: TemperatureUnit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        // 1. Loading State (Initial Refresh)
        if (weatherItems.loadState.refresh is LoadState.Loading && weatherItems.itemCount == 0) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // 2. Error State (Initial Refresh)
        if (weatherItems.loadState.refresh is LoadState.Error && weatherItems.itemCount == 0) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.weather_list_error))
                        Button(onClick = { weatherItems.retry() }) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
            }
        }

        // 3. The Data List
        items(count = weatherItems.itemCount) { index ->
            val weather = weatherItems[index]
            if (weather != null) {
                WeatherCard(
                    weather = weather,
                    temperatureUnit = temperatureUnit
                )
            }
        }

        // 4. Append Loading State (Infinite Scroll Spinner)
        if (weatherItems.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.PaddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(Dimens.IconSizeSmall))
                }
            }
        }

        // 5. Append Error State
        if (weatherItems.loadState.append is LoadState.Error) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.PaddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { weatherItems.retry()} ) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}

// --- Previews ---

@Preview(name = "Compact - Light", showBackground = true)
@Preview(
    name = "Compact - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DashboardLayoutCompactPreview() {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        DashboardLayout(
            isExpanded = false,
            currentUnit = TemperatureUnit.METRIC,
            onUnitToggled = { },
            weatherSlot = {
                Text(
                    text = stringResource(R.string.preview_weather_placeholder),
                    modifier = Modifier.padding(Dimens.PaddingMedium)
                )
            },
            searchActionSlot = { Button(onClick = {}) { Text(stringResource(R.string.preview_search_placeholder)) } },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )
    }
}

@Preview(
    showBackground = true,
    name = "Expanded Layout (Tablet/Foldable)",
    widthDp = 800,
    heightDp = 600
)
@Composable
private fun DashboardLayoutExpandedPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    MaterialTheme {
        DashboardLayout(
            isExpanded = true,
            currentUnit = TemperatureUnit.METRIC,
            onUnitToggled = { },
            weatherSlot = { Text(stringResource(R.string.preview_weather_placeholder)) },
            searchActionSlot = { Button(onClick = {}) { Text(stringResource(R.string.preview_weather_placeholder)) } },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )
    }
}