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
            chanceOfPrecipitation = mapPrecipitationChance(precipitationChance),
            temperatureUnit = temperaturePreferences.getTemperatureUnit().symbol
        )
    }

    /**
     * Estimates precipitation chance based on weather condition, humidity and cloud coverage
     */
    private fun calculatePrecipitationChance(condition: String, humidity: Int, clouds: Int): Int {
        var pop = (0.3 * clouds + 0.5 * humidity).toInt()

        if (
            condition.contains("rain", ignoreCase = true) ||
            condition.contains("snow", ignoreCase = true) ||
            condition.contains("thunderstorm", ignoreCase = true)
        ) pop += 20
        if (condition.contains("drizzle", ignoreCase = true)) pop += 10
        if (condition.contains("clear", ignoreCase = true)) pop -= 20

        return pop
    }

    /**
     * Maps precipitation chance to a user-friendly string
     */
    private fun mapPrecipitationChance(precipitationChance: Int): String {
        return when {
            precipitationChance < 20 -> "Very Low"
            precipitationChance < 40 -> "Low"
            precipitationChance < 60 -> "Medium"
            precipitationChance < 80 -> "High"
            else -> "Very High"
        }
    }
}
