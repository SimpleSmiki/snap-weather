package simple.smiki.snapweather.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import simple.smiki.snapweather.data.model.WeatherResponse

/**
 * Retrofit interface for OpenWeatherMap API
 * Base URL: https://api.openweathermap.org/data/2.5/
 */
interface WeatherApiService {

    /**
     * Fetch current weather for a city
     * Example: weather?q=San Francisco,US&appid=YOUR_KEY&units=imperial
     *
     * @param cityQuery City name with optional country code (e.g., "San Francisco,US")
     * @param apiKey OpenWeatherMap API key
     * @param units Temperature units - "imperial" for Fahrenheit, "metric" for Celsius
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityQuery: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"

        /**
         * Constructs the full icon URL from the icon code
         * @param iconCode The icon code from the API (e.g., "10d")
         */
        fun getIconUrl(iconCode: String): String {
            return "$ICON_BASE_URL$iconCode@2x.png"
        }
    }
}