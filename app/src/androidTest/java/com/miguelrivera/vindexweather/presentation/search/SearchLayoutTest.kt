package com.miguelrivera.vindexweather.presentation.search

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI automation tests for [SearchLayout].
 *
 * Verifies that the layout correctly renders the content passed into its slots.
 * This ensures the structural integrity of the screen independent of the specific UI state.
 */
class SearchLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val searchBarTag = "search_bar_slot"
    private val resultsTag = "results_slot"

    @Test
    fun searchLayout_rendersSlotsCorrectly() {
        composeTestRule.setContent {
            VindexWeatherTheme {
                SearchLayout(
                    searchBarSlot = {
                        Text("Search Bar Content", modifier = Modifier.testTag(searchBarTag))
                    },
                    resultsSlot = {
                        Text("Results Content", modifier = Modifier.testTag(resultsTag))
                    }
                )
            }
        }

        // Verify both slots are rendered on screen
        composeTestRule.onNodeWithTag(searchBarTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(resultsTag).assertIsDisplayed()
    }
}