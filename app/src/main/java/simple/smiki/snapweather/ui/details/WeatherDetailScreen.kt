package simple.smiki.snapweather.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.ui.theme.SnapWeatherTheme

/**
 * Detail screen showing comprehensive weather information for a selected city
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    cityWeather: CityWeather,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityWeather.cityName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WeatherMainInfo(cityWeather)

            HorizontalDivider()

            WeatherDetailsGrid(cityWeather)
        }
    }
}

/**
 * Main weather information section with large icon and temperature
 */
@Composable
private fun WeatherMainInfo(cityWeather: CityWeather) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = cityWeather.weatherIconUrl,
            contentDescription = cityWeather.weatherDescription,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = cityWeather.weatherDescription,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "${cityWeather.temperature}${cityWeather.temperatureUnit}",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TemperatureLabel(
                label = "High",
                temperature = cityWeather.tempHigh,
                temperatureUnit = cityWeather.temperatureUnit
            )
            TemperatureLabel(
                label = "Low",
                temperature = cityWeather.tempLow,
                temperatureUnit = cityWeather.temperatureUnit
            )
        }
    }
}

/**
 * Small temperature display with label (used for High/Low)
 */
@Composable
private fun TemperatureLabel(label: String, temperature: Int, temperatureUnit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${temperature}${temperatureUnit}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Grid of additional weather details
 */
@Composable
private fun WeatherDetailsGrid(cityWeather: CityWeather) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        DetailItem(
            label = "Chance of Precipitation",
            value = "${cityWeather.chanceOfPrecipitation}%"
        )

        DetailItem(
            label = "Humidity",
            value = "${cityWeather.humidity}%"
        )

    }
}

/**
 * Individual detail item showing a label and value
 */
@Composable
private fun DetailItem(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun WeatherMainPreview() {
    SnapWeatherTheme {
        WeatherMainInfo(
            cityWeather = CityWeather(
                cityName = "San Francisco",
                temperature = 68,
                tempHigh = 72,
                tempLow = 55,
                weatherDescription = "Partly Cloudy",
                weatherIconUrl = "",
                humidity = 60,
                chanceOfPrecipitation = 20
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TemperatureLabelPreview() {
    SnapWeatherTheme {
        TemperatureLabel(
            label = "High",
            temperature = 75,
            temperatureUnit = "°F"
        )
    }
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun WeatherDetailsGridPreview() {
    SnapWeatherTheme {
        WeatherDetailsGrid(
            cityWeather = CityWeather(
                cityName = "San Francisco",
                temperature = 68,
                tempHigh = 72,
                tempLow = 55,
                weatherDescription = "Partly Cloudy",
                weatherIconUrl = "",
                humidity = 60,
                chanceOfPrecipitation = 20
            )
        )
    }
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun DetailItemPreview() {
    SnapWeatherTheme {
        DetailItem(
            label = "Humidity",
            value = "50%"
        )
    }
}