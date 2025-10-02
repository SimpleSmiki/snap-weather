package simple.smiki.snapweather.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import simple.smiki.snapweather.data.repository.WeatherRepository

/**
 * Factory for creating WeatherListViewModel with constructor parameters
 */
class WeatherListViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherListViewModel::class.java)) {
            return WeatherListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}