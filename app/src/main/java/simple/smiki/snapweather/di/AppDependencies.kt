package simple.smiki.snapweather.di

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import simple.smiki.snapweather.data.api.WeatherApiService
import simple.smiki.snapweather.data.preferences.TemperaturePreferences
import simple.smiki.snapweather.data.repository.WeatherRepository
import java.util.concurrent.TimeUnit

/**
 * Simple singleton object that provides dependencies
 */
object AppDependencies {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val weatherApiService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    /**
     * Temperature preferences instance
     */
    val temperaturePreferences: TemperaturePreferences by lazy {
        TemperaturePreferences(applicationContext)
    }

    /**
     * Public singleton instance of the repository
     */
    val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(weatherApiService, temperaturePreferences)
    }

}