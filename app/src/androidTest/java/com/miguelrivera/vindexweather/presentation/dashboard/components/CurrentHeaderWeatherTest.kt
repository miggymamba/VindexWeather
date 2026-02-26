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
 * UI Component test for [CurrentWeatherHeader].
 * * Verifies that the header correctly surfaces the most critical weather data
 * (City Name, Current Temperature, and Condition) with appropriate unit conversions.
 */
class CurrentWeatherHeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun currentWeatherHeader_rendersCorrectData_metric() {
        // Arrange
        val temp = 22.0
        val condition = "Clear Sky"
        val city = "Tokyo"

        val weather = Weather(
            id = 1,
            cityName = city,
            tempCurrent = temp,
            conditionText = condition,
            iconUrl = "http://example.com/icon.png",
            timestamp = 1771329600L
        )

        // Act
        composeTestRule.setContent {
            VindexWeatherTheme {
                CurrentWeatherHeader(
                    weather = weather,
                    temperatureUnit = TemperatureUnit.METRIC
                )
            }
        }

        // Assert
        // 1. Verify City Name is displayed prominently
        composeTestRule.onNodeWithText(city).assertIsDisplayed()

        // 2. Verify Condition Text
        composeTestRule.onNodeWithText(condition).assertIsDisplayed()

        // 3. Verify Temperature Formatted (22.0 -> 22°C)
        composeTestRule.onNodeWithText("22°C").assertIsDisplayed()

        // 4. Verify Icon Container exists
        composeTestRule.onNodeWithContentDescription("Weather Condition Icon").assertIsDisplayed()
    }

    @Test
    fun currentWeatherHeader_rendersCorrectData_imperial() {
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
                CurrentWeatherHeader(
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