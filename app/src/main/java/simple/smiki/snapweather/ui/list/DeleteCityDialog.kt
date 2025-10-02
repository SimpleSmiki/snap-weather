package simple.smiki.snapweather.ui.list

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

/**
 * Confirmation dialog before deleting a city
 */
@Composable
fun DeleteCityDialog(
    cityName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Remove City?")
        },
        text = {
            Text("Are you sure you want to remove $cityName from your weather list?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Remove",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}