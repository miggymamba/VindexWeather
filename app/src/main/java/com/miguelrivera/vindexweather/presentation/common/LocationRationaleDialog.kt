package com.miguelrivera.vindexweather.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrivera.vindexweather.R
import com.miguelrivera.vindexweather.presentation.theme.VindexWeatherTheme

/**
 * A reusable dialog component that explains why the app needs location permissions.
 *
 * Extracted from [DashboardScreen] to improve readability and testability.
 *
 * @param onConfirm Callback when the user agrees to grant permission.
 * @param onDismiss Callback when the user declines or dismisses the dialog.
 */
@Composable
fun LocationRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.location_rationale_title)) },
        text = {
            Text(stringResource(R.string.location_rationale_body))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_grant_access))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_no_thanks))
            }
        }
    )
}

@Preview
@Composable
private fun LocationRationaleDialogPreview() {
    VindexWeatherTheme{
        LocationRationaleDialog(onConfirm = {}, onDismiss = {})
    }
}