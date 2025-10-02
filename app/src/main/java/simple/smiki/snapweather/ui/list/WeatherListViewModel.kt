package simple.smiki.snapweather.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.data.repository.WeatherRepository

/**
 * ViewModel for the weather list screen
 */

class WeatherListViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherListUiState>(WeatherListUiState.Loading)
    val uiState: StateFlow<WeatherListUiState> = _uiState.asStateFlow()

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
                    WeatherListUiState.Success(weatherList)
                }
            } else {
                WeatherListUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )
            }
        }
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