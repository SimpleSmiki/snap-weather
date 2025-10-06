package simple.smiki.snapweather.data.model

import com.google.gson.annotations.SerializedName

/**
 * Main response from OpenWeatherMap API
 */
data class WeatherResponse(
    @SerializedName("name")
    val cityName: String,

    @SerializedName("main")
    val main: MainWeather,

    @SerializedName("weather")
    val weather: List<WeatherCondition>,

    @SerializedName("clouds")
    val clouds: Clouds

)

/**
 * Temperature and atmospheric data
 */
data class MainWeather(
    @SerializedName("temp")
    val temperature: Double,

    @SerializedName("temp_min")
    val tempMin: Double,

    @SerializedName("temp_max")
    val tempMax: Double,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("pressure")
    val pressure: Int
)

/**
 * Weather condition with icon code
 */
data class WeatherCondition(
    @SerializedName("id")
    val id: Int,

    @SerializedName("main")
    val main: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String
)

/**
 * Cloud coverage data
 */
data class Clouds(
    @SerializedName("all")
    val all: Int,
)


