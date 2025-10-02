package simple.smiki.snapweather.data.model

/**
 * Domain model for our UI
 */
data class CityWeather(
    val cityName: String,
    val temperature: Int,
    val tempHigh: Int,
    val tempLow: Int,
    val weatherDescription: String,
    val weatherIconUrl: String,
    val humidity: Int,
    val chanceOfPrecipitation: Int = 0,
    val temperatureUnit: String = "°F"
)