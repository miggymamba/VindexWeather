package com.miguelrivera.vindexweather.presentation.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrivera.vindexweather.presentation.LocalNavActions
import com.miguelrivera.vindexweather.presentation.LocalWindowSize

/**
 * Stateful wrapper for the Dashboard screen.
 *
 * Resolves global dependencies and state, passing them to the stateless layout.
 */
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val navActions = LocalNavActions.current
    val windowSize = LocalWindowSize.current

    val isExpanded = windowSize.widthSizeClass != WindowWidthSizeClass.Compact

    DashboardLayout(
        modifier = modifier,
        isExpanded = isExpanded,
        weatherSlot = {
            Text(
                text = "Current Weather Data",
                style = MaterialTheme.typography.titleLarge
            )
        },
        searchActionSlot = {
            Button(onClick = { navActions.navigateToSearch() }) {
                Text(text = "Go to Search")
            }
        }
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
 */
@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    weatherSlot: @Composable () -> Unit,
    searchActionSlot: @Composable () -> Unit
) {
    if (isExpanded) {
        Row(
            modifier = Modifier.fillMaxSize(),
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
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            weatherSlot()
            searchActionSlot()
        }
    }
}

@Preview(showBackground = true, name = "Compact Layout (Phone)")
@Composable
private fun DashboardLayoutCompactPreview() {
    MaterialTheme {
        DashboardLayout(
            isExpanded = false,
            weatherSlot = { Text("Weather Content Placeholder") },
            searchActionSlot = { Button(onClick = {}) { Text("Search Action Placeholder") } }
        )
    }
}

@Preview(showBackground = true, name = "Expanded Layout (Tablet/Foldable)", widthDp = 800, heightDp = 600)
@Composable
private fun DashboardLayoutExpandedPreview() {
    MaterialTheme {
        DashboardLayout(
            isExpanded = true,
            weatherSlot = { Text("Weather Content Placeholder") },
            searchActionSlot = { Button(onClick = {}) { Text("Search Action Placeholder") } }
        )
    }
}