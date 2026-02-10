package com.miguelrivera.vindexweather.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.miguelrivera.vindexweather.presentation.navigation.NavActions
import com.miguelrivera.vindexweather.presentation.navigation.VindexNavHost
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VindexWeatherTheme {
                // Initialize the single source of truth for navigation
                val navController = rememberNavController()
                // Create the actions wrapper
                val navActions = remember(navController) { NavActions(navController) }
                CompositionLocalProvider(LocalNavActions provides navActions) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        VindexNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}