package com.miguelrivera.vindexweather.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VindexWeatherTheme {
                // Calculate the dynamic window size (handles rotation and foldables)
                val windowSizeClass = calculateWindowSizeClass(this)

                // Initialize the single source of truth for navigation
                val navController = rememberNavController()
                val navActions = remember(navController) { NavActions(navController) }

                // Provide both Navigation and WindowSize globally down the Compose tree
                CompositionLocalProvider(
                    LocalNavActions provides navActions,
                    LocalWindowSize provides windowSizeClass
                ) {
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