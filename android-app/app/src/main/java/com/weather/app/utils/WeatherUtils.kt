package com.weather.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Weather utility functions for formatting and conversions.
 */
object WeatherUtils {

    /**
     * Get weather icon URL from OpenWeatherMap icon code.
     */
    fun getWeatherIconUrl(iconCode: String, size: Int = 4): String {
        return "https://openweathermap.org/img/wn/${iconCode}@${size}x.png"
    }

    /**
     * Format temperature with unit symbol.
     */
    fun formatTemperature(temp: Double, symbol: String = "°C"): String {
        return "${temp.toInt()}$symbol"
    }

    /**
     * Format Unix timestamp to readable time.
     */
    fun formatTime(timestamp: Long, pattern: String = "h:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    /**
     * Format Unix timestamp to day of week.
     */
    fun formatDayOfWeek(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    /**
     * Format Unix timestamp to short day name.
     */
    fun formatShortDay(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    /**
     * Format date from dt_txt string.
     */
    fun formatForecastDate(dtTxt: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val date = inputFormat.parse(dtTxt)
            date?.let { outputFormat.format(it) } ?: dtTxt
        } catch (_: Exception) {
            dtTxt
        }
    }

    /**
     * Get hour from dt_txt string.
     */
    fun formatForecastHour(dtTxt: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("ha", Locale.getDefault())
            val date = inputFormat.parse(dtTxt)
            date?.let { outputFormat.format(it).lowercase() } ?: ""
        } catch (_: Exception) { "" }
    }

    /**
     * Get wind direction from degrees.
     */
    fun getWindDirection(degrees: Int?): String {
        if (degrees == null) return ""
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((degrees + 22.5) / 45).toInt() % 8
        return directions[index]
    }

    /**
     * Map weather condition to a descriptive background gradient.
     */
    fun getWeatherConditionType(weatherMain: String?): WeatherType {
        return when (weatherMain?.lowercase()) {
            "clear" -> WeatherType.CLEAR
            "clouds" -> WeatherType.CLOUDY
            "rain", "drizzle" -> WeatherType.RAINY
            "thunderstorm" -> WeatherType.THUNDERSTORM
            "snow" -> WeatherType.SNOWY
            "mist", "fog", "haze", "smoke" -> WeatherType.FOGGY
            else -> WeatherType.CLEAR
        }
    }

    /**
     * Check if it's currently daytime based on sunrise/sunset.
     */
    fun isDaytime(currentDt: Long, sunrise: Long?, sunset: Long?): Boolean {
        if (sunrise == null || sunset == null) return true
        return currentDt in sunrise..sunset
    }
}

enum class WeatherType {
    CLEAR, CLOUDY, RAINY, THUNDERSTORM, SNOWY, FOGGY
}
