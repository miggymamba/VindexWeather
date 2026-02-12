package com.miguelrivera.vindexweather.presentation.dashboard

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI automation tests for [DashboardLayout].
 *
 * Verifies adaptive layout rendering (Compact vs Expanded) and user interaction callbacks
 * using the Slot API pattern, ensuring UI components are completely decoupled from business logic.
 */
class DashboardLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val weatherSlotTag = "weather_slot"
    private val searchButtonText = "Search"

    @Test
    fun compactLayout_rendersContentCorrectly() {
        composeTestRule.setContent {
            DashboardLayout(
                isExpanded = false,
                weatherSlot = {
                    Text(text = "Weather Content", Modifier.testTag(weatherSlotTag))
                },
                searchActionSlot = {
                    Button(onClick = {}) { Text(text = searchButtonText)}
                }
            )
        }

        // Verify both slots are rendered on screen
        composeTestRule.onNodeWithTag(weatherSlotTag).assertIsDisplayed()
        composeTestRule.onNodeWithText(searchButtonText).assertIsDisplayed()
    }

    @Test
    fun expandedLayout_rendersContentCorrectly() {
        composeTestRule.setContent {
            DashboardLayout(
                isExpanded = true,
                weatherSlot = {
                    Text(text = "Weather Content", Modifier.testTag(weatherSlotTag))
                },
                searchActionSlot = {
                    Button(onClick = {}) { Text(text = searchButtonText)}
                }
            )
        }

        // Verify both slots are rendered on screen
        composeTestRule.onNodeWithTag(weatherSlotTag).assertIsDisplayed()
        composeTestRule.onNodeWithText(searchButtonText).assertIsDisplayed()
    }

    @Test
    fun searchActionSlot_click_triggersCallback() {
        var buttonClicked = false

        composeTestRule.setContent {
            DashboardLayout(
                isExpanded = false,
                weatherSlot = {
                    Text(text = "Weather Content", modifier = Modifier.testTag(weatherSlotTag))
                },
                searchActionSlot = {
                    Button(onClick = { buttonClicked = true }) {
                        Text(text = searchButtonText)
                    }
                }
            )
        }

        composeTestRule.onNodeWithText(searchButtonText).performClick()

        assertTrue("Search button click callback was not triggered.", buttonClicked)
    }
    
}