package com.miguelrivera.vindexweather.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.miguelrivera.vindexweather.presentation.LocalNavActions

@Composable
fun SearchScreen() {
    val navActions = LocalNavActions.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navActions.navigateBack() }) {
            Text(text = "Go Back")
        }
    }
}