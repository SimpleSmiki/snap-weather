package simple.smiki.snapweather

import android.app.Application
import simple.smiki.snapweather.di.AppDependencies

class WeatherApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AppDependencies.init(this)
    }
}