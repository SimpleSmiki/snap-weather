package simple.smiki.snapweather.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import simple.smiki.snapweather.data.api.WeatherApiService
import simple.smiki.snapweather.data.model.City
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.data.model.WeatherResponse
import simple.smiki.snapweather.data.preferences.TemperaturePreferences

/**
 * Repository that manages weather data and city list
 */
class WeatherRepository(
    private val apiService: WeatherApiService,
    private val temperaturePreferences: TemperaturePreferences
) {

    private val apiKey = "da65fafb6cb9242168b7724fb5ab75e7"

    private val _cities = mutableListOf(
        City("San Francisco", "CA"),
        City("New York", "NY"),
        City("Salt Lake City", "UT")
    )

    /**
     * Add a city to the list
     */
    fun addCity(city: City) {
        if (!_cities.contains(city)) {
            _cities.add(city)
        }
    }

    /**
     * Remove a city from the list
     */
    fun removeCity(city: City) {
        _cities.remove(city)
    }

    /**
     * Fetch weather for all cities concurrently
     */
    suspend fun getAllCitiesWeather(): Result<List<CityWeather>> = withContext(Dispatchers.IO) {
        try {

            val weatherData = coroutineScope {
                _cities.map { city ->
                    async {
                        try {
                            val response = apiService.getCurrentWeather(
                                cityQuery = city.toApiFormat(),
                                apiKey = apiKey,
                                units = temperaturePreferences.getTemperatureUnit().apiValue
                            )
                            mapResponseToCityWeather(response, city)
                        } catch (e: Exception) {
                            Log.e("WeatherRepository", "Error fetching weather for ${city.name}", e)
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch weather for a single city
     */
    suspend fun getCityWeather(city: City): Result<CityWeather> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentWeather(
                cityQuery = city.toApiFormat(),
                apiKey = apiKey,
                units = temperaturePreferences.getTemperatureUnit().apiValue
            )
            Result.success(mapResponseToCityWeather(response, city))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Maps the API response to our domain model
     */
    private fun mapResponseToCityWeather(
        response: WeatherResponse,
        city: City
    ): CityWeather {
        val condition = response.weather.firstOrNull()
        val precipitationChance = calculatePrecipitationChance(condition?.main ?: "", response.main.humidity, response.clouds.all)

        return CityWeather(
            cityName = city.toDisplayFormat(),
            temperature = response.main.temperature,
            tempHigh = response.main.tempMax,
            tempLow = response.main.tempMin,
            weatherDescription = condition?.description ?: "Unknown",
            weatherIconUrl = WeatherApiService.getIconUrl(condition?.icon ?: "01d"),
            humidity = response.main.humidity,
            chanceOfPrecipitation = calculatePrecipitationChance(condition?.description ?: ""),
            temperatureUnit = temperaturePreferences.getTemperatureUnit().symbol
        )
    }

    /**
     * Estimates precipitation chance based on weather condition
     */
    private fun calculatePrecipitationChance(condition: String): Int {
        return when (condition.lowercase()) {
            "thunderstorm", "rain" -> 90
            "overcast clouds" -> 85
            "snow" -> 75
            "drizzle" -> 60
            "broken clouds" -> 51
            "scattered clouds" -> 25
            "mist", "fog" -> 20
            "few clouds" -> 15
            else -> 0
        }
    }
}
