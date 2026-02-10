package com.miguelrivera.vindexweather.presentation.dashboard


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.miguelrivera.vindexweather.presentation.LocalNavActions

@Composable
fun DashboardScreen() {
    val navActions = LocalNavActions.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navActions.navigateToSearch() }) {
            Text(text = "Go to Search")
        }
    }
}