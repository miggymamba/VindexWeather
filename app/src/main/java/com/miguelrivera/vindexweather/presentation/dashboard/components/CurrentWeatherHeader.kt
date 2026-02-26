package com.miguelrivera.vindexweather.presentation.dashboard.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.miguelrivera.vindexweather.presentation.common.TemperatureFormatter
import com.miguelrivera.vindexweather.presentation.theme.Dimens
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme

/**
 * Prominent header component displaying the immediate current weather conditions.
 *
 * Utilizes elevated typography and contrasting surface containers to establish
 * visual hierarchy over the subsequent forecast list.
 */
@Composable
fun CurrentWeatherHeader(
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        Box(
            modifier = Modifier
                .size(Dimens.IconSizeLarge)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(weather.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.weather_icon_desc),
                modifier = Modifier.size(Dimens.IconSizeLarge)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        Text(
            text = TemperatureFormatter.format(
                context = context,
                celsiusTemp = weather.tempCurrent,
                unit = temperatureUnit
            ),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = weather.conditionText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CurrentWeatherHeaderPreview() {
    VindexWeatherTheme {
        CurrentWeatherHeader(
            weather = Weather(
                id = 1,
                cityName = "Tokyo",
                tempCurrent = 22.0,
                conditionText = "Clear Sky",
                iconUrl = "",
                timestamp = System.currentTimeMillis() / 1000
            ),
            temperatureUnit = TemperatureUnit.METRIC
        )
    }
}