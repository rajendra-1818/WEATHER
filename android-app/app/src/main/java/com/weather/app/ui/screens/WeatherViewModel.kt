package com.weather.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.data.db.FavoriteLocationEntity
import com.weather.app.data.model.*
import com.weather.app.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main weather screen.
 * Manages UI state and coordinates data fetching.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Load default location or New York as fallback
        loadDefaultLocation()
        loadSavedLocations()
    }

    // ─── Weather Data ────────────────────────────────────────────

    fun loadWeather(lat: Double, lon: Double, locationName: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val units = _uiState.value.unitSystem.value

            // Fetch current weather
            val weatherResult = repository.getCurrentWeather(lat, lon, units)
            weatherResult.onSuccess { weather ->
                _uiState.update {
                    it.copy(
                        currentWeather = weather,
                        locationName = locationName.ifEmpty { weather.name },
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Failed to load weather")
                }
            }

            // Fetch forecast
            val forecastResult = repository.getForecast(lat, lon, units)
            forecastResult.onSuccess { forecast ->
                _uiState.update { it.copy(forecast = forecast) }
            }
        }
    }

    fun refreshWeather() {
        val weather = _uiState.value.currentWeather ?: return
        loadWeather(
            weather.coord.lat,
            weather.coord.lon,
            _uiState.value.locationName
        )
    }

    // ─── Search ──────────────────────────────────────────────────

    fun searchCity(query: String) {
        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(300) // Debounce

            val result = repository.searchCity(query)
            result.onSuccess { locations ->
                _uiState.update { it.copy(searchResults = locations, isSearching = false) }
            }.onFailure {
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            }
        }
    }

    fun selectSearchResult(location: GeoLocation) {
        _uiState.update { it.copy(searchResults = emptyList()) }
        val name = buildString {
            append(location.name)
            location.state?.let { append(", $it") }
            location.country?.let { append(", $it") }
        }
        loadWeather(location.lat, location.lon, name)
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
    }

    // ─── Saved Locations ─────────────────────────────────────────

    private fun loadSavedLocations() {
        viewModelScope.launch {
            repository.getFavoriteLocations().collect { favorites ->
                val savedLocations = favorites.map {
                    SavedLocation(
                        id = it.id,
                        name = it.name,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        country = it.country,
                        state = it.state,
                        isDefault = it.isDefault
                    )
                }
                _uiState.update { it.copy(savedLocations = savedLocations) }
            }
        }
    }

    fun saveCurrentLocation() {
        val weather = _uiState.value.currentWeather ?: return
        viewModelScope.launch {
            repository.addFavorite(
                FavoriteLocationEntity(
                    name = _uiState.value.locationName.ifEmpty { weather.name },
                    latitude = weather.coord.lat,
                    longitude = weather.coord.lon,
                    country = weather.sys?.country,
                )
            )
        }
    }

    fun removeSavedLocation(location: SavedLocation) {
        viewModelScope.launch {
            repository.removeFavorite(
                FavoriteLocationEntity(
                    id = location.id ?: 0,
                    name = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    country = location.country,
                    state = location.state,
                    isDefault = location.isDefault
                )
            )
        }
    }

    fun selectSavedLocation(location: SavedLocation) {
        loadWeather(location.latitude, location.longitude, location.name)
    }

    // ─── Settings ────────────────────────────────────────────────

    fun toggleUnits() {
        val newUnit = if (_uiState.value.unitSystem == UnitSystem.METRIC)
            UnitSystem.IMPERIAL else UnitSystem.METRIC
        _uiState.update { it.copy(unitSystem = newUnit) }
        refreshWeather()
    }

    // ─── Private Helpers ─────────────────────────────────────────

    private fun loadDefaultLocation() {
        viewModelScope.launch {
            val default = repository.getDefaultLocation()
            if (default != null) {
                loadWeather(default.latitude, default.longitude, default.name)
            } else {
                // Default to New York
                loadWeather(40.7128, -74.0060, "New York")
            }
        }
    }
}
