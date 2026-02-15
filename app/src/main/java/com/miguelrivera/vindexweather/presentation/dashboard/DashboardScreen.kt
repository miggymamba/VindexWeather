package com.miguelrivera.vindexweather.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.presentation.LocalNavActions
import com.miguelrivera.vindexweather.presentation.LocalWindowSize
import com.miguelrivera.vindexweather.presentation.common.LocationRationaleDialog
import com.miguelrivera.vindexweather.presentation.common.RequestLocationPermission
import kotlinx.coroutines.launch

/**
 * Stateful wrapper for the Dashboard screen.
 *
 * Resolves global dependencies and state, passing them to the stateless layout.
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

    // Only extract these because they are used inside a CoroutineScope.
    val deniedMessage = stringResource(R.string.location_denied_fallback)
    val requiredMessage = stringResource(R.string.location_required_generic)

    // State for managing the Rationale Dialog
    var showRationaleDialog by remember { mutableStateOf(false) }
    var permissionContinuation by remember { mutableStateOf<() -> Unit>({ }) }

    val isExpanded = windowSize.widthSizeClass != WindowWidthSizeClass.Compact

    // Headless effect: Request permission on start, handling all edge cases
    RequestLocationPermission(
        onPermissionGranted = {
            viewModel.triggerSync()
        },
        onPermissionDenied = {
            // Rejection Point Handling: Inform the user via UI why they see Tokyo.
            viewModel.triggerSync() // Trigger fallback to default coords
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

    DashboardLayout(
        modifier = modifier,
        isExpanded = isExpanded,
        weatherSlot = {
            Text(
                text = stringResource(R.string.dashboard_weather_section_title),
                style = MaterialTheme.typography.titleLarge
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

/**
 * Stateless layout component utilizing the Slot API pattern.
 *
 * Separates the structural layout from the content, enabling adaptive UI and isolated previews.
 *
 * @param isExpanded Determines if the layout should use the expanded (tablet/foldable) or compact (phone) arrangement.
 * @param weatherSlot Composable slot for displaying weather information.
 * @param searchActionSlot Composable slot for search triggers or inputs.
 * @param snackbarHost Slot for the SnackbarHost to ensure it floats above content.
 */
@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
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
                    weatherSlot()
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                weatherSlot()
                searchActionSlot()
            }
        }
    }
}

@Preview(showBackground = true, name = "Compact Layout (Phone)")
@Composable
private fun DashboardLayoutCompactPreview() {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        DashboardLayout(
            isExpanded = false,
            weatherSlot = { Text(stringResource(R.string.preview_weather_placeholder)) },
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
            weatherSlot = { Text(stringResource(R.string.preview_weather_placeholder)) },
            searchActionSlot = { Button(onClick = {}) { Text(stringResource(R.string.preview_weather_placeholder)) } },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )
    }
}