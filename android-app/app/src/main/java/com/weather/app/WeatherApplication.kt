package com.weather.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Weather App.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any app-level configurations here
    }
}
