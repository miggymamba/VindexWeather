package com.miguelrivera.vindexweather.presentation.dashboard.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.domain.model.Weather
import com.miguelrivera.vindexweather.presentation.common.DateFormatter
import com.miguelrivera.vindexweather.presentation.common.TemperatureFormatter
import com.miguelrivera.vindexweather.presentation.theme.Dimens

/**
 * A card component displaying weather details for a specific time slot.
 *
 * @param weather The domain model containing weather data.
 * @param temperatureUnit The user's preferred unit (Metric/Imperial).
 */
@Composable
fun WeatherCard(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingSmall)
            .defaultMinSize(minHeight = Dimens.WeatherCardMinHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Date and Condition
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DateFormatter.formatDayTime(weather.timestamp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = weather.conditionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right: Temperature and Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Weather Icon from Coil
                // Wrapped in a Surface/Box with a background color to ensure visibility.
                // Using SoftBlue (a light brand color) provides a consistent, high-contrast
                // background for all icon types (dark or light) in both Light and Dark modes.
                Box(
                    modifier = Modifier
                        .size(Dimens.IconSizeMedium)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.84f))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(weather.iconUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.weather_icon_desc),
                        modifier = Modifier.size(Dimens.IconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))

                Text(
                    text = TemperatureFormatter.format(
                        context = context,
                        celsiusTemp = weather.tempCurrent,
                        unit = temperatureUnit
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeatherCardPreview() {
    MaterialTheme {
        WeatherCard(
            weather = Weather(
                id = 1,
                cityName = "Tokyo",
                tempCurrent = 25.0,
                conditionText = stringResource(R.string.preview_condition),
                iconUrl = "",
                timestamp = System.currentTimeMillis() / 1000
            ),
            temperatureUnit = TemperatureUnit.METRIC,
            modifier = Modifier.padding(Dimens.PaddingMedium)
        )
    }
}