package simple.smiki.snapweather.data.model

/**
 * Domain model for our UI
 */
data class CityWeather(
    val cityName: String,
    val temperature: Double,
    val tempHigh: Double,
    val tempLow: Double,
    val weatherDescription: String,
    val weatherIconUrl: String,
    val humidity: Int,
    val chanceOfPrecipitation: String = "",
    val temperatureUnit: String = "°F"
)