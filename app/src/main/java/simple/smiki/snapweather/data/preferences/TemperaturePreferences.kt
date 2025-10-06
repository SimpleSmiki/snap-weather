package simple.smiki.snapweather.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manages user preferences for temperature units
 */
class TemperaturePreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Temperature unit enum
     */
    enum class TemperatureUnit(val apiValue: String, val symbol: String) {
        FAHRENHEIT("imperial", "°F"),
        CELSIUS("metric", "°C")
    }

    /**
     * Get the currently selected temperature unit
     * Defaults to Fahrenheit
     */
    fun getTemperatureUnit(): TemperatureUnit {
        val unitName = prefs.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.FAHRENHEIT.name)
        return try {
            TemperatureUnit.valueOf(unitName ?: TemperatureUnit.FAHRENHEIT.name)
        } catch (_: IllegalArgumentException) {
            TemperatureUnit.FAHRENHEIT
        }
    }

    /**
     * Save the user's temperature unit preference
     */
    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit { putString(KEY_TEMPERATURE_UNIT, unit.name) }
    }

    /**
     * Toggle between Fahrenheit and Celsius
     * Returns the new unit after toggling
     */
    fun toggleTemperatureUnit(): TemperatureUnit {
        val current = getTemperatureUnit()
        val new = if (current == TemperatureUnit.FAHRENHEIT) {
            TemperatureUnit.CELSIUS
        } else {
            TemperatureUnit.FAHRENHEIT
        }
        setTemperatureUnit(new)
        return new
    }

    companion object {
        private const val PREFS_NAME = "weather_preferences"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
    }
}