package com.miguelrivera.vindexweather.presentation.dashboard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.data.local.datastore.TemperatureUnit
import com.miguelrivera.vindexweather.presentation.theme.Dimens

/**
 * Stateless UI component providing a binary selection mechanism for temperature units.
 *
 * Designed to be slotted into various parent layouts (Compact/Expanded) without carrying
 * its own state or padding assumptions. Uses [Dimens] for consistent spacing.
 *
 * @param currentUnit The currently active temperature unit.
 * @param onUnitToggled Callback invoked when the user interacts with the switch.
 */
@Composable
fun UnitToggleControl(
    currentUnit: TemperatureUnit,
    onUnitToggled: (TemperatureUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.unit_celsius_label),
            style = MaterialTheme.typography.labelLarge,
            color = if (currentUnit == TemperatureUnit.METRIC) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(
            checked = currentUnit == TemperatureUnit.IMPERIAL,
            onCheckedChange = { isImperial ->
                onUnitToggled(if (isImperial) TemperatureUnit.IMPERIAL else TemperatureUnit.METRIC)
            },
            modifier = Modifier.padding(horizontal = Dimens.ToggleSwitchSpacing)
        )
        Text(
            text = stringResource(R.string.unit_fahrenheit_label),
            style = MaterialTheme.typography.labelLarge,
            color = if (currentUnit == TemperatureUnit.IMPERIAL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Metric (Celsius) Selected", showBackground = true)
@Composable
private fun UnitToggleControlMetricPreview() {
    MaterialTheme {
        UnitToggleControl(
            currentUnit = TemperatureUnit.METRIC,
            onUnitToggled = {}
        )
    }
}

@Preview(name = "Imperial (Fahrenheit) Selected", showBackground = true)
@Composable
private fun UnitToggleControlImperialPreview() {
    MaterialTheme {
        UnitToggleControl(
            currentUnit = TemperatureUnit.IMPERIAL,
            onUnitToggled = {}
        )
    }
}