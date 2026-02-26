package com.weather.app.data.repository

import com.google.gson.Gson
import com.weather.app.data.api.WeatherApiService
import com.weather.app.data.db.*
import com.weather.app.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Pattern - Single source of truth for weather data.
 * Handles caching strategy: local Room DB -> Remote API.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val favoriteLocationDao: FavoriteLocationDao,
    private val gson: Gson
) {

    // ─── Current Weather ─────────────────────────────────────────

    suspend fun getCurrentWeather(
        lat: Double, lon: Double, units: String = "metric"
    ): Result<CurrentWeatherResponse> {
        val cacheKey = "${lat}_${lon}"

        // Try cache first
        val cached = weatherDao.getCachedWeather(cacheKey)
        if (cached != null && !cached.isExpired()) {
            try {
                val weather = gson.fromJson(cached.weatherJson, CurrentWeatherResponse::class.java)
                return Result.success(weather)
            } catch (_: Exception) { /* Cache corrupt, fetch fresh */ }
        }

        // Fetch from API
        return try {
            val response = apiService.getCurrentWeather(lat, lon, units)
            if (response.isSuccessful && response.body() != null) {
                val weather = response.body()!!

                // Cache locally
                weatherDao.insertCachedWeather(
                    CachedWeatherEntity(
                        locationKey = cacheKey,
                        locationName = weather.name,
                        latitude = lat,
                        longitude = lon,
                        weatherJson = gson.toJson(weather),
                        forecastJson = null
                    )
                )

                Result.success(weather)
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback to expired cache if available
            if (cached != null) {
                try {
                    val weather = gson.fromJson(cached.weatherJson, CurrentWeatherResponse::class.java)
                    return Result.success(weather)
                } catch (_: Exception) {}
            }
            Result.failure(e)
        }
    }

    // ─── Forecast ────────────────────────────────────────────────

    suspend fun getForecast(
        lat: Double, lon: Double, units: String = "metric"
    ): Result<ForecastResponse> {
        return try {
            val response = apiService.getForecast(lat, lon, units)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Forecast API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Geocoding ───────────────────────────────────────────────

    suspend fun searchCity(query: String): Result<List<GeoLocation>> {
        return try {
            val response = apiService.geocode(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Geocoding error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Saved Locations (Backend) ───────────────────────────────

    suspend fun getSavedLocations(): Result<List<SavedLocation>> {
        return try {
            val response = apiService.getSavedLocations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch locations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveLocation(location: SavedLocation): Result<SavedLocation> {
        return try {
            val response = apiService.addSavedLocation(location)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to save location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSavedLocation(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteSavedLocation(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to delete location"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Local Favorites (Room) ──────────────────────────────────

    fun getFavoriteLocations(): Flow<List<FavoriteLocationEntity>> =
        favoriteLocationDao.getAllFavorites()

    suspend fun getDefaultLocation(): FavoriteLocationEntity? =
        favoriteLocationDao.getDefaultLocation()

    suspend fun addFavorite(location: FavoriteLocationEntity) {
        if (location.isDefault) {
            favoriteLocationDao.clearDefaults()
        }
        favoriteLocationDao.insert(location)
    }

    suspend fun removeFavorite(location: FavoriteLocationEntity) {
        favoriteLocationDao.delete(location)
    }

    suspend fun setDefaultLocation(id: Int) {
        favoriteLocationDao.clearDefaults()
        favoriteLocationDao.setDefault(id)
    }

    // ─── Cache Management ────────────────────────────────────────

    suspend fun clearExpiredCache() {
        weatherDao.clearExpiredCache()
    }

    suspend fun clearAllCache() {
        weatherDao.clearAllCache()
    }
}
