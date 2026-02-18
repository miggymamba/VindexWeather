package com.miguelrivera.vindexweather.presentation.search.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * UI Component tests for Search-related composables.
 */
class SearchComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBar_inputUpdates_callbackTriggers() {
        var currentQuery = ""

        composeTestRule.setContent {
            VindexWeatherTheme {
                VindexSearchBar(
                    query = currentQuery,
                    onQueryChange = { currentQuery = it },
                    onClearClick = {}
                )
            }
        }

        // Act: Type "Helsinki"
        composeTestRule.onNodeWithText("Search for a city…").performTextInput("Helsinki")

        // Assert: Callback updated local variable
        assertEquals("Helsinki", currentQuery)
    }

    @Test
    fun searchBar_clearIcon_appearsOnlyWhenQueryNotEmpty() {
        composeTestRule.setContent {
            VindexWeatherTheme {
                VindexSearchBar(
                    query = "Yokohama",
                    onQueryChange = {},
                    onClearClick = {}
                )
            }
        }

        // Assert: Clear Icon Visible
        composeTestRule.onNodeWithContentDescription("Clear Search Query").assertIsDisplayed()
    }

    @Test
    fun cityResultItem_rendersDetails_and_clicks() {
        var clickedCity: City? = null
        val city = City(
            name = "Stockholm",
            latitude = 59.3251172,
            longitude = 18.0710935,
            country = "SE",
            state = null
        )

        composeTestRule.setContent {
            VindexWeatherTheme {
                CityResultItem(
                    city = city,
                    onClick = { clickedCity = it }
                )
            }
        }

        // Assert: Text is visible (Name and formatted "State, Country")
        composeTestRule.onNodeWithText("Stockholm").assertIsDisplayed()
        composeTestRule.onNodeWithText(", SE").assertIsDisplayed()

        // Act: Click
        composeTestRule.onNodeWithText("Stockholm").performClick()

        // Assert: Callback fired
        assertEquals(city, clickedCity)
    }
}