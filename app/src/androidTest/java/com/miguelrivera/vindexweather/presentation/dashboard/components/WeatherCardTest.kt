package com.miguelrivera.vindexweather.presentation.dashboard.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI Component test for [WeatherCard].
 * Verifies that the card correctly displays formatted data based on the provided model.
 */
class WeatherCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherCard_rendersCorrectData_metric() {
        // Arrange
        val timestamp = 1771329600L // Approx Tue, 12:00 PM
        val temp = 25.4
        val condition = "Light Rain"

        val weather = Weather(
            id = 1,
            cityName = "Tokyo",
            tempCurrent = temp,
            conditionText = condition,
            iconUrl = "http://example.com/icon.png",
            timestamp = timestamp
        )

        // Act
        composeTestRule.setContent {
            VindexWeatherTheme {
                WeatherCard (
                    weather = weather,
                    temperatureUnit = TemperatureUnit.METRIC
                )
            }
        }

        // Assert
        // 1. Verify Condition Text
        composeTestRule.onNodeWithText(condition).assertIsDisplayed()

        // 2. Verify Temperature Formatted (25.4 -> 25°C)
        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()

        // 3. Verify Icon exists (via content description)
        composeTestRule.onNodeWithContentDescription("Weather Condition Icon").assertIsDisplayed()
    }

    @Test
    fun weatherCard_rendersCorrectDate_imperial() {
        // Arrange
        val tempCelsius = 20.0 // 20°C = 68°F

        val weather = Weather(
            id = 1,
            cityName = "London",
            tempCurrent = tempCelsius,
            conditionText = "Cloudy",
            iconUrl = "",
            timestamp = 1771329600L
        )

        // Act
        composeTestRule.setContent {
            VindexWeatherTheme {
                WeatherCard(
                    weather = weather,
                    temperatureUnit = TemperatureUnit.IMPERIAL
                )
            }
        }

        // Assert
        // Verify conversion: (20 * 9/5) + 32 = 68
        composeTestRule.onNodeWithText("68°F").assertIsDisplayed()
    }
}