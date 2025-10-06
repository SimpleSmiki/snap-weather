package simple.smiki.snapweather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.data.preferences.TemperaturePreferences
import simple.smiki.snapweather.data.repository.WeatherRepository
import simple.smiki.snapweather.ui.list.WeatherListUiState
import simple.smiki.snapweather.ui.list.WeatherListViewModel

/**
 * Unit tests for WeatherListViewModel
 *
 */

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherListViewModelTest {

    @Mock
    private lateinit var mockRepository: WeatherRepository

    @Mock
    private lateinit var mockTemperaturePreferences: TemperaturePreferences

    private lateinit var viewModel: WeatherListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(mockTemperaturePreferences.getTemperatureUnit())
            .thenReturn(TemperaturePreferences.TemperatureUnit.FAHRENHEIT)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        viewModel = WeatherListViewModel(mockRepository, mockTemperaturePreferences)
        assertTrue(viewModel.uiState.value is WeatherListUiState.Loading)
    }

    @Test
    fun `loadWeather updates state to Success when data is fetched`() = runTest {
        val mockWeatherList = listOf(createMockCityWeather("San Francisco, CA"),
            createMockCityWeather("Chicago, IL"))
        `when`(mockRepository.getAllCitiesWeather())
            .thenReturn(Result.success(mockWeatherList))

        viewModel = WeatherListViewModel(mockRepository, mockTemperaturePreferences)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherListUiState.Success)
        assertEquals(2, (state as WeatherListUiState.Success).cities.size)
    }

    @Test
    fun `toggleTemperatureUnit changes unit and reloads weather`() = runTest {
        val mockWeatherList = listOf(createMockCityWeather("Test City, CA"))
        `when`(mockRepository.getAllCitiesWeather())
            .thenReturn(Result.success(mockWeatherList))
        `when`(mockTemperaturePreferences.toggleTemperatureUnit())
            .thenReturn(TemperaturePreferences.TemperatureUnit.CELSIUS)

        viewModel = WeatherListViewModel(mockRepository, mockTemperaturePreferences)
        advanceUntilIdle()

        viewModel.toggleTemperatureUnit()
        advanceUntilIdle()

        assertEquals(TemperaturePreferences.TemperatureUnit.CELSIUS, viewModel.currentUnit.value)
        val state = viewModel.uiState.value
        assertTrue(state is WeatherListUiState.Success)
    }

    private fun createMockCityWeather(
        cityName: String,
        temperature: Double = 72.0,
        unit: String = "°F"
    ): CityWeather {
        return CityWeather(
            cityName = cityName,
            temperature = temperature,
            tempHigh = temperature + 5,
            tempLow = temperature - 5,
            weatherDescription = "Clear sky",
            weatherIconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
            humidity = 65,
            chanceOfPrecipitation = "Low",
            temperatureUnit = unit
        )
    }
}