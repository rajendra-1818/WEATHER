package com.weather.app.data.api

import com.weather.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for the Weather backend.
 */
interface WeatherApiService {

    // ─── Weather Endpoints ───────────────────────────────────────

    @GET("weather/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): Response<CurrentWeatherResponse>

    @GET("weather/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>

    @GET("weather/geocode")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): Response<List<GeoLocation>>

    @GET("weather/reverse-geocode")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<List<GeoLocation>>

    // ─── Location Endpoints ──────────────────────────────────────

    @GET("locations/")
    suspend fun getSavedLocations(): Response<List<SavedLocation>>

    @POST("locations/")
    suspend fun addSavedLocation(
        @Body location: SavedLocation
    ): Response<SavedLocation>

    @PUT("locations/{id}")
    suspend fun updateSavedLocation(
        @Path("id") id: Int,
        @Body location: SavedLocation
    ): Response<SavedLocation>

    @DELETE("locations/{id}")
    suspend fun deleteSavedLocation(
        @Path("id") id: Int
    ): Response<Map<String, String>>

    // ─── Health ──────────────────────────────────────────────────

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, String>>
}
