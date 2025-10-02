package simple.smiki.snapweather.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.di.AppDependencies
import simple.smiki.snapweather.ui.theme.SnapWeatherTheme

/**
 * Main screen that displays a list of cities with their weather
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    onCityClick: (CityWeather) -> Unit,
    viewModel: WeatherListViewModel = viewModel(
        factory = WeatherListViewModelFactory(
            AppDependencies.weatherRepository
        )
    )
) {

    val uiState by viewModel.uiState.collectAsState()

    var showAddCityDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCityDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add city"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WeatherListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is WeatherListUiState.Success -> {
                    WeatherList(
                        cities = state.cities,
                        onCityClick = onCityClick
                    )
                }

                is WeatherListUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showAddCityDialog) {
        AddCityDialog(
            onDismiss = { showAddCityDialog = false },
            onConfirm = { cityName, state ->
                viewModel.addCity(cityName, state)
                showAddCityDialog = false
            }
        )
    }
}

/**
 * Displays the list of cities using LazyColumn
 */
@Composable
private fun WeatherList(
    cities: List<CityWeather>,
    onCityClick: (CityWeather) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = cities,
            key = { it.cityName }
        ) { cityWeather ->

            WeatherListItem(
                cityWeather = cityWeather,
                onClick = { onCityClick(cityWeather) }
            )
        }
    }
}

/**
 * Individual weather item card
 */
@Composable
private fun WeatherListItem(
    cityWeather: CityWeather,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = cityWeather.weatherIconUrl,
                    contentDescription = cityWeather.weatherDescription,
                    modifier = Modifier.size(64.dp)
                )

                Column {
                    Text(
                        text = cityWeather.cityName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cityWeather.weatherDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${cityWeather.temperature}${cityWeather.temperatureUnit}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Error state UI with a retry button
 */
@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun WeatherListItemPreview() {
    WeatherListItem(
        cityWeather = CityWeather(
            cityName = "San Francisco",
            temperature = 68,
            tempHigh = 70,
            tempLow = 55,
            weatherDescription = "Sunny",
            weatherIconUrl = "",
            humidity = 20
    ), onClick = {}
    )
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun ErrorPreview() {
    SnapWeatherTheme {
        ErrorContent(
            message = "Failed to load data. Please try again."
        )
    }
}
