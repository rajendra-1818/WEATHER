package com.weather.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Current Weather Response from OpenWeatherMap API.
 */
data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<WeatherCondition>,
    val main: MainWeatherData,
    val visibility: Int?,
    val wind: Wind,
    val clouds: Clouds?,
    val dt: Long,
    val sys: Sys?,
    val timezone: Int?,
    val name: String,
    val cod: Int?
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainWeatherData(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level") val seaLevel: Int? = null,
    @SerializedName("grnd_level") val groundLevel: Int? = null
)

data class Wind(
    val speed: Double,
    val deg: Int?,
    val gust: Double? = null
)

data class Clouds(
    val all: Int
)

data class Sys(
    val sunrise: Long?,
    val sunset: Long?,
    val country: String?
)

/**
 * Forecast Response - 5 day / 3 hour intervals.
 */
data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: ForecastCity?
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeatherData,
    val weather: List<WeatherCondition>,
    val wind: Wind,
    @SerializedName("dt_txt") val dtTxt: String,
    val pop: Double? = null // Probability of precipitation
)

data class ForecastCity(
    val name: String?,
    val coord: Coord?,
    val country: String?,
    val timezone: Int?
)

/**
 * Geocoding response.
 */
data class GeoLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String?,
    val state: String? = null
)

/**
 * Saved location from backend.
 */
data class SavedLocation(
    val id: Int?,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val state: String?,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

/**
 * UI State wrapper for weather data.
 */
data class WeatherUiState(
    val isLoading: Boolean = true,
    val currentWeather: CurrentWeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val locationName: String = "",
    val error: String? = null,
    val savedLocations: List<SavedLocation> = emptyList(),
    val searchResults: List<GeoLocation> = emptyList(),
    val isSearching: Boolean = false,
    val unitSystem: UnitSystem = UnitSystem.METRIC
)

enum class UnitSystem(val value: String, val tempSymbol: String, val speedUnit: String) {
    METRIC("metric", "°C", "m/s"),
    IMPERIAL("imperial", "°F", "mph")
}
