package simple.smiki.snapweather.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for adding a new city to the weather list
 */
@Composable
fun AddCityDialog(
    onDismiss: () -> Unit,
    onConfirm: (cityName: String, state: String) -> Unit
) {
    var cityName by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add City")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = cityName,
                    onValueChange = {
                        cityName = it
                        showError = false
                    },
                    label = { Text("City Name") },
                    placeholder = { Text("e.g. Los Angeles") },
                    singleLine = true,
                    isError = showError && cityName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state,
                    onValueChange = {
                        if (it.length <= 2) {
                            state = it.uppercase()
                            showError = false
                        }
                    },
                    label = { Text("State") },
                    placeholder = { Text("e.g. CA") },
                    singleLine = true,
                    isError = showError && state.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = "Please enter both city name and state",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (cityName.isNotBlank() && state.isNotBlank()) {
                        onConfirm(cityName.trim(), state.trim())
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}