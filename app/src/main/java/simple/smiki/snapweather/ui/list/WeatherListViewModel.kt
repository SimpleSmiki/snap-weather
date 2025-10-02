package simple.smiki.snapweather.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import simple.smiki.snapweather.data.model.City
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.data.preferences.TemperaturePreferences
import simple.smiki.snapweather.data.preferences.TemperaturePreferences.TemperatureUnit
import simple.smiki.snapweather.data.repository.WeatherRepository
import kotlin.collections.plus

/**
 * ViewModel for the weather list screen
 */

class WeatherListViewModel(
    private val repository: WeatherRepository,
    private val temperaturePreferences: TemperaturePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherListUiState>(WeatherListUiState.Loading)
    val uiState: StateFlow<WeatherListUiState> = _uiState.asStateFlow()

    private val _currentUnit = MutableStateFlow(temperaturePreferences.getTemperatureUnit())
    val currentUnit: StateFlow<TemperatureUnit> = _currentUnit.asStateFlow()

    private var cachedWeatherData: List<CityWeather> = emptyList()

    init {
        loadWeather()
    }

    /**
     * Fetches weather data for all cities
     */
    fun loadWeather() {
        _uiState.value = WeatherListUiState.Loading

        viewModelScope.launch {
            val result = repository.getAllCitiesWeather()

            _uiState.value = if (result.isSuccess) {
                val weatherList = result.getOrNull() ?: emptyList()
                if (weatherList.isEmpty()) {
                    WeatherListUiState.Error("No weather data available")
                } else {
                    cachedWeatherData = weatherList
                    WeatherListUiState.Success(weatherList)
                }
            } else {
                WeatherListUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun toggleTemperatureUnit() {
        viewModelScope.launch {
            _currentUnit.value = temperaturePreferences.toggleTemperatureUnit()
            cachedWeatherData = cachedWeatherData.map {
                when (_currentUnit.value) {
                    TemperatureUnit.FAHRENHEIT -> it.copy(
                        temperature = celsiusToFahrenheit(it.temperature.toDouble()).toInt(),
                        tempHigh = celsiusToFahrenheit(it.tempHigh.toDouble()).toInt(),
                        tempLow = celsiusToFahrenheit(it.tempLow.toDouble()).toInt(),
                        temperatureUnit = TemperatureUnit.FAHRENHEIT.symbol
                    )
                    TemperatureUnit.CELSIUS -> it.copy(
                        temperature = fahrenheitToCelsius(it.temperature.toDouble()).toInt(),
                        tempHigh = fahrenheitToCelsius(it.tempHigh.toDouble()).toInt(),
                        tempLow = fahrenheitToCelsius(it.tempLow.toDouble()).toInt(),
                        temperatureUnit = TemperatureUnit.CELSIUS.symbol
                    )
                }
            }
            _uiState.value = WeatherListUiState.Success(cachedWeatherData)
        }
    }

    /**
     * Add a new city to the list
     */
    fun addCity(cityName: String, state: String) {
        viewModelScope.launch {
            _uiState.value = WeatherListUiState.Loading

            val city = City(cityName, state)

            val result = repository.getCityWeather(city)

            if (result.isSuccess) {
                val newCityWeather = result.getOrNull()

                if (newCityWeather != null) {
                    repository.addCity(city)
                    cachedWeatherData = cachedWeatherData.plus(newCityWeather)
                }

                _uiState.value = WeatherListUiState.Success(cachedWeatherData)
            } else {
                _uiState.value = WeatherListUiState.Error(
                    "Could not find weather data for $cityName, $state. Please check the city name and try again."
                )
            }
        }
    }

    /**
     * Remove a city from the list
     */
    fun removeCity(cityWeather: CityWeather) {
        viewModelScope.launch {
            val parts = cityWeather.cityName.split(", ")
            if (parts.size == 2) {
                val city = City(
                    name = parts[0],
                    state = parts[1]
                )
                repository.removeCity(city)

                cachedWeatherData = cachedWeatherData.minus(cityWeather)

                if (cachedWeatherData.isEmpty()) {
                    _uiState.value = WeatherListUiState.Empty
                } else {
                    _uiState.value = WeatherListUiState.Success(cachedWeatherData)
                }
            }
        }
    }

    fun fahrenheitToCelsius(fahrenheitTemp: Double): Double {
        return (fahrenheitTemp - 32) * 5 / 9
    }

    fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }

}

/**
 * Sealed class representing all possible states of the weather list screen
 */
sealed class WeatherListUiState {
    /**
     * Initial state when data is being fetched
     */
    object Loading : WeatherListUiState()

    /**
     * State when all cities were removed and the list is empty
     */
    object Empty : WeatherListUiState()

    /**
     * State when data has been successfully loaded
     * @param cities List of weather data for each city
     */
    data class Success(val cities: List<CityWeather>) : WeatherListUiState()

    /**
     * State when an error occurred
     * @param message Error message to display to the user
     */
    data class Error(val message: String) : WeatherListUiState()
}