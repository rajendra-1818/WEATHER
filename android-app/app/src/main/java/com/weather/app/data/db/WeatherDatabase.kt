package com.weather.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Room Entity for cached weather data.
 */
@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey
    val locationKey: String, // "lat_lon" as key
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val weatherJson: String, // Serialized CurrentWeatherResponse
    val forecastJson: String?, // Serialized ForecastResponse
    val cachedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + CACHE_DURATION_MS
) {
    companion object {
        const val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    }

    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt
}

/**
 * Room Entity for favorite locations.
 */
@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val state: String? = null,
    val isDefault: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Data Access Object for weather cache operations.
 */
@Dao
interface WeatherDao {

    @Query("SELECT * FROM cached_weather WHERE locationKey = :key LIMIT 1")
    suspend fun getCachedWeather(key: String): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(weather: CachedWeatherEntity)

    @Query("DELETE FROM cached_weather WHERE expiresAt < :now")
    suspend fun clearExpiredCache(now: Long = System.currentTimeMillis())

    @Query("DELETE FROM cached_weather")
    suspend fun clearAllCache()
}

/**
 * Data Access Object for favorite locations.
 */
@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations ORDER BY isDefault DESC, name ASC")
    fun getAllFavorites(): Flow<List<FavoriteLocationEntity>>

    @Query("SELECT * FROM favorite_locations WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultLocation(): FavoriteLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: FavoriteLocationEntity)

    @Delete
    suspend fun delete(location: FavoriteLocationEntity)

    @Query("UPDATE favorite_locations SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE favorite_locations SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Int)
}

/**
 * Room Database for the Weather App.
 */
@Database(
    entities = [CachedWeatherEntity::class, FavoriteLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteLocationDao(): FavoriteLocationDao
}
