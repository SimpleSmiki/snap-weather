package simple.smiki.snapweather.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            AppDependencies.weatherRepository,
            AppDependencies.temperaturePreferences
        )
    )
) {

    val uiState by viewModel.uiState.collectAsState()
    val currentUnit by viewModel.currentUnit.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var showAddCityDialog by remember { mutableStateOf(false) }
    var cityToDelete by remember { mutableStateOf<CityWeather?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleTemperatureUnit() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Toggle unit",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUnit.symbol,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
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
                is WeatherListUiState.Empty -> {
                    NoContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is WeatherListUiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.loadWeather() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WeatherList(
                            cities = state.cities,
                            onCityClick = onCityClick,
                            onCityDelete = { cityToDelete = it }
                        )
                    }
                }

                is WeatherListUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadWeather() },
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

    cityToDelete?.let { city ->
        DeleteCityDialog(
            cityName = city.cityName,
            onDismiss = { cityToDelete = null },
            onConfirm = {
                viewModel.removeCity(city)
                cityToDelete = null
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
    onCityDelete: (CityWeather) -> Unit,
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

            val dismissState = rememberSwipeToDismissBoxState(
                initialValue = SwipeToDismissBoxValue.Settled,
                positionalThreshold = { distance -> distance * 0.5f }
            )

            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    onCityDelete(cityWeather)
                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true
            ) {
                WeatherListItem(
                    cityWeather = cityWeather,
                    onClick = { onCityClick(cityWeather) }
                )
            }
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
                text = "${cityWeather.temperature.toInt()}${cityWeather.temperatureUnit}",
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
    onRetry: () -> Unit,
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
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

/**
 * State UI without cities added
 */
@Composable
private fun NoContent(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = "Please add cities to see weather data.",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun WeatherListItemPreview() {
    WeatherListItem(
        cityWeather = CityWeather(
            cityName = "San Francisco",
            temperature = 68.0,
            tempHigh = 70.5,
            tempLow = 55.3,
            weatherDescription = "Sunny",
            weatherIconUrl = "",
            humidity = 20
        ),
        onClick = {}
    )
}

@Preview(showBackground = true, widthDp =  360)
@Composable
fun ErrorPreview() {
    SnapWeatherTheme {
        ErrorContent(
            message = "Failed to load data. Please try again.",
            onRetry = {}
        )
    }
}
