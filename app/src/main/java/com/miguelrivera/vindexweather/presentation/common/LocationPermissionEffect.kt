package com.miguelrivera.vindexweather.presentation.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Headless Composable that encapsulates the Location Permission request logic.
 *
 * This component follows the "Effect" pattern, ensuring that permission requests
 * are triggered as side effects of the composition lifecycle.
 *
 * @param onPermissionGranted Callback invoked when the user grants either FINE or COARSE location.
 * @param onPermissionDenied Callback invoked when the user denies the permission.
 * @param onShowRationale Callback invoked when the system recommends showing a rationale (user denied once).
 * Provides a [requestPermission] lambda that must be called to trigger the actual system dialog.
 */
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onShowRationale: (requestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Register the permission launcher using the Activity Result API
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocation || coarseLocation) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    // Check status efficiently on every composition or recomposition
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            val permissionsToRequest = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            // Check if app should show rationale (User denied previously but didn't select "Don't ask again")
            // Check both Fine and Coarse to be thorough.
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } ?: false

            if (shouldShowRationale) {
                // Delegate UI display to the caller, passing the trigger to continue
                onShowRationale {
                    launcher.launch(permissionsToRequest)
                }
            } else {
                // First time asking or "Don't ask again" checked -> Request directly
                launcher.launch(permissionsToRequest)
            }
        } else {
            // If already granted, immediately notify the caller to proceed.
            onPermissionGranted()
        }


    }
}