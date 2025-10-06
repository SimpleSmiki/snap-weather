package simple.smiki.snapweather

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import simple.smiki.snapweather.data.api.WeatherApiService
import simple.smiki.snapweather.data.model.City
import simple.smiki.snapweather.data.model.Clouds
import simple.smiki.snapweather.data.model.MainWeather
import simple.smiki.snapweather.data.model.WeatherCondition
import simple.smiki.snapweather.data.model.WeatherResponse
import simple.smiki.snapweather.data.preferences.TemperaturePreferences
import simple.smiki.snapweather.data.repository.WeatherRepository

/**
 * Unit tests for WeatherRepository
 *
 */
class WeatherRepositoryTest {

    @Mock
    private lateinit var mockApiService: WeatherApiService

    @Mock
    private lateinit var mockTemperaturePreferences: TemperaturePreferences

    private lateinit var repository: WeatherRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = WeatherRepository(mockApiService, mockTemperaturePreferences)
    }

    @Test
    fun `getCities returns initial cities`() {
        val cities = repository.getCities()

        assertEquals(3, cities.size)
        assertTrue(cities.any { it.name == "San Francisco" && it.state == "CA" })
        assertTrue(cities.any { it.name == "New York" && it.state == "NY" })
        assertTrue(cities.any { it.name == "Salt Lake City" && it.state == "UT" })
    }

    @Test
    fun `addCity adds new city`() {
        val newCity = City("Los Angeles", "CA")
        repository.addCity(newCity)

        assertTrue(repository.getCities().contains(newCity))
    }

    @Test
    fun `removeCity removes city`() {
        val city = City("San Francisco", "CA")
        repository.removeCity(city)

        assertFalse(repository.getCities().contains(city))
    }

    @Test
    fun `getCityWeather returns success`() = runTest {
        val city = City("Chicago", "IL")
        `when`(mockTemperaturePreferences.getTemperatureUnit())
            .thenReturn(TemperaturePreferences.TemperatureUnit.FAHRENHEIT)
        val mockResponse = WeatherResponse(
            cityName = "Chicago",
            main = MainWeather(72.0, 67.0, 77.0, 65, 1013),
            weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d")),
            clouds = Clouds(50)
        )
        `when`(mockApiService.getCurrentWeather(anyString(), anyString(), anyString()))
            .thenReturn(mockResponse)
        val result = repository.getCityWeather(city)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCityWeather returns failure when API throws exception`() = runTest {
        val city = City("Invalid City", "XX")
        `when`(mockTemperaturePreferences.getTemperatureUnit())
            .thenReturn(TemperaturePreferences.TemperatureUnit.FAHRENHEIT)
        `when`(mockApiService.getCurrentWeather(anyString(), anyString(), anyString()))
            .thenThrow(RuntimeException("City not found"))

        val result = repository.getCityWeather(city)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    private fun calculatePrecipitationChance(condition: String, humidity: Int, clouds: Int): Int {
        val method = WeatherRepository::class.java.getDeclaredMethod(
            "calculatePrecipitationChance", String::class.java, Int::class.java, Int::class.java
        )
        method.isAccessible = true
        return method.invoke(repository, condition, humidity, clouds) as Int
    }

    private fun mapPrecipitationChance(precipitationChance: Int): String {
        val method = WeatherRepository::class.java.getDeclaredMethod(
            "mapPrecipitationChance", Int::class.java
        )
        method.isAccessible = true
        return method.invoke(repository, precipitationChance) as String
    }

    @Test
    fun `precipitation chance is higher for rain`() {
        val chance = calculatePrecipitationChance("Rain", 80, 60)
        assertTrue(chance == 78)
    }

    @Test
    fun `precipitation chance is lower for clear`() {
        val chance = calculatePrecipitationChance("Clear", 30, 10)
        assertTrue(chance == -2)
    }

    @Test
    fun `precipitation chance is medium for clouds`() {
        val chance = calculatePrecipitationChance("Clouds", 50, 50)
        assertTrue(chance == 40)
    }

    @Test
    fun `mapPrecipitationChance returns correct string`() {
        assertEquals("Very Low", mapPrecipitationChance(10))
        assertEquals("Low", mapPrecipitationChance(30))
        assertEquals("Medium", mapPrecipitationChance(50))
        assertEquals("High", mapPrecipitationChance(70))
        assertEquals("Very High", mapPrecipitationChance(90))
    }

}
