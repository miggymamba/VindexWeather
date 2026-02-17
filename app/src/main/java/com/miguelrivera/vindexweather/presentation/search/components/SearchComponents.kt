package com.miguelrivera.vindexweather.presentation.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.domain.model.City
import com.miguelrivera.vindexweather.presentation.common.shimmerEffect
import com.miguelrivera.vindexweather.presentation.theme.Dimens
import com.miguelrivera.vindexweather.presentation.theme.VindexIcons

/**
 * A standard search input field complying with Material 3 guidelines.
 *
 * @param query The current text in the search field.
 * @param onQueryChange Callback triggered when text changes.
 * @param onClearClick Callback triggered when the clear icon is clicked.
 */
@Composable
fun VindexSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.SearchBarHeight),
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = VindexIcons.Search,
                contentDescription = stringResource(R.string.search_icon_desc),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = VindexIcons.Clear,
                        contentDescription = stringResource(R.string.clear_search_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(Dimens.CornerRadiusRound),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
    )
}

/**
 * A single row item representing a city in the search results.
 *
 * @param city The domain model containing city details.
 * @param onClick Callback triggered when the item is tapped.
 */
@Composable
fun CityResultItem(
    city: City,
    onClick: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(city) }
            .padding(Dimens.PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = city.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Text(
                text = stringResource(
                    R.string.search_result_format,
                    city.state ?: "",
                    city.country
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * A placeholder row displaying a loading shimmer effect.
 * Mimics the layout of [CityResultItem] to prevent layout shifts.
 */
@Composable
fun SearchShimmerItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Box(
                modifier = Modifier
                    .width(Dimens.ShimmerCityNameWidth)
                    .height(Dimens.ShimmerCityNameHeight)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Box(
                modifier = Modifier
                    .width(Dimens.ShimmerSubtitleWidth)
                    .height(Dimens.ShimmerSubtitleHeight)
                    .shimmerEffect()
            )
        }
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    MaterialTheme {
        VindexSearchBar(query = "Tokyo", onQueryChange = {}, onClearClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CityResultItemPreview() {
    MaterialTheme {
        CityResultItem(
            city = City("Tokyo", 35.6, 139.6, "JP", "Tokyo Metropolis"),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchShimmerItemPreview() {
    MaterialTheme {
        SearchShimmerItem()
    }
}