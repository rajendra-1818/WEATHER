package com.weather.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.weather.app.data.model.*
import com.weather.app.ui.theme.*
import com.weather.app.utils.WeatherUtils

/**
 * Main Weather Home Screen with current conditions, forecast, and details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeWeatherScreen(
    viewModel: WeatherViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToLocations: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val gradientColors = getWeatherGradient(uiState.currentWeather)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 48.dp)
        ) {
            // Top Bar
            TopActionBar(
                onSearchClick = onNavigateToSearch,
                onLocationsClick = onNavigateToLocations,
                onSaveClick = { viewModel.saveCurrentLocation() },
                onUnitToggle = { viewModel.toggleUnits() },
                unitSystem = uiState.unitSystem
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (uiState.error != null) {
                ErrorDisplay(
                    error = uiState.error!!,
                    onRetry = { viewModel.refreshWeather() }
                )
            } else {
                uiState.currentWeather?.let { weather ->
                    // Location name
                    Text(
                        text = uiState.locationName,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    // Current temperature - hero display
                    CurrentTemperatureDisplay(weather, uiState.unitSystem)

                    // Weather condition
                    weather.weather.firstOrNull()?.let { condition ->
                        Text(
                            text = condition.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // High / Low
                    Text(
                        text = "H: ${weather.main.tempMax.toInt()}° L: ${weather.main.tempMin.toInt()}°",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Hourly Forecast
                    uiState.forecast?.let { forecast ->
                        HourlyForecastCard(forecast, uiState.unitSystem)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weather Details Grid
                    WeatherDetailsGrid(weather, uiState.unitSystem)

                    // 5-Day Forecast
                    uiState.forecast?.let { forecast ->
                        DailyForecastCard(forecast, uiState.unitSystem)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TopActionBar(
    onSearchClick: () -> Unit,
    onLocationsClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUnitToggle: () -> Unit,
    unitSystem: UnitSystem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLocationsClick) {
            Icon(Icons.Filled.List, "Saved Locations", tint = Color.White)
        }

        Row {
            TextButton(onClick = onUnitToggle) {
                Text(
                    text = if (unitSystem == UnitSystem.METRIC) "°C" else "°F",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Outlined.BookmarkAdd, "Save Location", tint = Color.White)
            }
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, "Search", tint = Color.White)
            }
        }
    }
}

@Composable
private fun CurrentTemperatureDisplay(
    weather: CurrentWeatherResponse,
    unitSystem: UnitSystem
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Weather icon
        weather.weather.firstOrNull()?.let { condition ->
            AsyncImage(
                model = WeatherUtils.getWeatherIconUrl(condition.icon),
                contentDescription = condition.description,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Temperature
        Text(
            text = "${weather.main.temp.toInt()}°",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 96.sp,
                fontWeight = FontWeight.Thin
            ),
            color = Color.White,
        )

        // Feels like
        Text(
            text = "Feels like ${weather.main.feelsLike.toInt()}${unitSystem.tempSymbol}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun HourlyForecastCard(forecast: ForecastResponse, unitSystem: UnitSystem) {
    val hourlyItems = forecast.list.take(8) // Next 24 hours

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "HOURLY FORECAST",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
        )
        Divider(color = Color.White.copy(alpha = 0.15f))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(hourlyItems) { item ->
                HourlyForecastItem(item, unitSystem)
            }
        }
    }
}

@Composable
private fun HourlyForecastItem(item: ForecastItem, unitSystem: UnitSystem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = WeatherUtils.formatForecastHour(item.dtTxt),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        item.weather.firstOrNull()?.let { condition ->
            AsyncImage(
                model = WeatherUtils.getWeatherIconUrl(condition.icon, 2),
                contentDescription = condition.description,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = "${item.main.temp.toInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun WeatherDetailsGrid(weather: CurrentWeatherResponse, unitSystem: UnitSystem) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Outlined.WaterDrop,
                label = "HUMIDITY",
                value = "${weather.main.humidity}%",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Outlined.Air,
                label = "WIND",
                value = "${weather.wind.speed} ${unitSystem.speedUnit}",
                subtitle = WeatherUtils.getWindDirection(weather.wind.deg),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Outlined.Compress,
                label = "PRESSURE",
                value = "${weather.main.pressure} hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Outlined.Visibility,
                label = "VISIBILITY",
                value = "${(weather.visibility ?: 0) / 1000} km",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Outlined.WbSunny,
                label = "SUNRISE",
                value = weather.sys?.sunrise?.let { WeatherUtils.formatTime(it) } ?: "--",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Outlined.WbTwilight,
                label = "SUNSET",
                value = weather.sys?.sunset?.let { WeatherUtils.formatTime(it) } ?: "--",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon, label,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun DailyForecastCard(forecast: ForecastResponse, unitSystem: UnitSystem) {
    // Group by day and get daily summary
    val dailyForecasts = forecast.list
        .groupBy { it.dtTxt.substring(0, 10) }
        .entries
        .take(5)
        .map { (_, items) ->
            val maxTemp = items.maxOf { it.main.tempMax }
            val minTemp = items.minOf { it.main.tempMin }
            val mainCondition = items[items.size / 2] // Mid-day condition
            Triple(mainCondition, minTemp, maxTemp)
        }

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text(
            text = "5-DAY FORECAST",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
        )
        Divider(color = Color.White.copy(alpha = 0.15f))

        dailyForecasts.forEach { (item, minTemp, maxTemp) ->
            DailyForecastRow(item, minTemp, maxTemp)
            Divider(
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun DailyForecastRow(item: ForecastItem, minTemp: Double, maxTemp: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = WeatherUtils.formatShortDay(item.dt),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.width(48.dp)
        )

        item.weather.firstOrNull()?.let { condition ->
            AsyncImage(
                model = WeatherUtils.getWeatherIconUrl(condition.icon, 2),
                contentDescription = condition.description,
                modifier = Modifier.size(28.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${minTemp.toInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "${maxTemp.toInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorDisplay(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Outlined.CloudOff,
            "Error",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

// ─── Glassmorphism Card Component ────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        )
    ) {
        content()
    }
}

// ─── Helper ──────────────────────────────────────────────────────

@Composable
private fun getWeatherGradient(weather: CurrentWeatherResponse?): List<Color> {
    val condition = weather?.weather?.firstOrNull()?.main
    return when (WeatherUtils.getWeatherConditionType(condition)) {
        com.weather.app.utils.WeatherType.CLEAR -> listOf(ClearDayStart, ClearDayEnd)
        com.weather.app.utils.WeatherType.CLOUDY -> listOf(CloudyStart, CloudyEnd)
        com.weather.app.utils.WeatherType.RAINY -> listOf(RainyStart, RainyEnd)
        com.weather.app.utils.WeatherType.THUNDERSTORM -> listOf(Color(0xFF1A237E), Color(0xFF0D47A1))
        com.weather.app.utils.WeatherType.SNOWY -> listOf(Color(0xFFCFD8DC), Color(0xFF78909C))
        com.weather.app.utils.WeatherType.FOGGY -> listOf(Color(0xFF78909C), Color(0xFF455A64))
    }
}
