package simple.smiki.snapweather.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import simple.smiki.snapweather.data.model.CityWeather
import simple.smiki.snapweather.ui.details.WeatherDetailScreen
import simple.smiki.snapweather.ui.list.WeatherListScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val WEATHER_LIST = "weather_list"
    const val WEATHER_DETAIL = "weather_detail"
}

/**
 * Main navigation graph for the app
 *
 * @param navController Controls navigation between screens (back, forward, etc.)
 */
@Composable
fun WeatherAppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER_LIST
    ) {
        composable(route = Routes.WEATHER_LIST) {
            WeatherListScreen(
                onCityClick = { cityWeather ->
                    navigateToDetail(navController, cityWeather)
                }
            )
        }

        composable(
            route = "${Routes.WEATHER_DETAIL}/{cityWeatherJson}",
            arguments = listOf(
                navArgument("cityWeatherJson") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("cityWeatherJson")

            val cityWeather = encodedJson?.let { encoded ->
                val json = Uri.decode(encoded)
                Gson().fromJson(json, CityWeather::class.java)
            }

            if (cityWeather != null) {
                WeatherDetailScreen(
                    cityWeather = cityWeather,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}

/**
 * Helper function to navigate to the detail screen with city weather data
 */
private fun navigateToDetail(
    navController: NavHostController,
    cityWeather: CityWeather
) {
    val cityWeatherJson = Gson().toJson(cityWeather)

    val encodedJson = Uri.encode(cityWeatherJson)

    navController.navigate("${Routes.WEATHER_DETAIL}/$encodedJson")
}