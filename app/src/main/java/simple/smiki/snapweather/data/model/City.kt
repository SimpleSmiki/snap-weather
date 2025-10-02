package simple.smiki.snapweather.data.model

/**
 * Simple city identifier
 */
data class City(
    val name: String,
    val state: String
) {
    // Format for API: "San Francisco,US" (OpenWeatherMap uses country codes)
    fun toApiFormat(): String = "$name,US"

    // Format for display: "San Francisco, CA"
    fun toDisplayFormat(): String = "$name, $state"
}