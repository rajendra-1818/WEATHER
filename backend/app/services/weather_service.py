"""
Weather Service - Handles OpenWeatherMap API integration and caching.
"""
import os
import requests
from datetime import datetime, timedelta, timezone
from typing import Optional
from app import db
from app.models import WeatherCache

OPENWEATHERMAP_BASE_URL = "https://api.openweathermap.org/data/2.5"
GEOCODING_BASE_URL = "https://api.openweathermap.org/geo/1.0"
CACHE_DURATION_MINUTES = 30


class WeatherService:
    """Service for fetching and caching weather data."""

    def __init__(self):
        self.api_key = os.environ.get('OPENWEATHERMAP_API_KEY', '')

    def get_current_weather(self, lat: float, lon: float, units: str = 'metric') -> dict:
        """Get current weather for coordinates, with caching."""
        # Check cache first
        cached = self._get_cached_weather(lat, lon)
        if cached and cached.weather_data:
            return cached.weather_data

        # Fetch from API
        if not self.api_key:
            return self._get_mock_weather(lat, lon)

        try:
            response = requests.get(
                f"{OPENWEATHERMAP_BASE_URL}/weather",
                params={
                    'lat': lat,
                    'lon': lon,
                    'appid': self.api_key,
                    'units': units,
                },
                timeout=10,
            )
            response.raise_for_status()
            data = response.json()

            # Cache the result
            self._cache_weather(lat, lon, weather_data=data)
            return data

        except requests.RequestException as e:
            return {'error': str(e), 'fallback': self._get_mock_weather(lat, lon)}

    def get_forecast(self, lat: float, lon: float, units: str = 'metric') -> dict:
        """Get 5-day/3-hour forecast for coordinates."""
        cached = self._get_cached_weather(lat, lon)
        if cached and cached.forecast_data:
            return cached.forecast_data

        if not self.api_key:
            return self._get_mock_forecast(lat, lon)

        try:
            response = requests.get(
                f"{OPENWEATHERMAP_BASE_URL}/forecast",
                params={
                    'lat': lat,
                    'lon': lon,
                    'appid': self.api_key,
                    'units': units,
                },
                timeout=10,
            )
            response.raise_for_status()
            data = response.json()

            self._cache_weather(lat, lon, forecast_data=data)
            return data

        except requests.RequestException as e:
            return {'error': str(e), 'fallback': self._get_mock_forecast(lat, lon)}

    def geocode(self, query: str, limit: int = 5) -> list:
        """Geocode a city name to coordinates."""
        if not self.api_key:
            return self._get_mock_geocode(query)

        try:
            response = requests.get(
                f"{GEOCODING_BASE_URL}/direct",
                params={
                    'q': query,
                    'limit': limit,
                    'appid': self.api_key,
                },
                timeout=10,
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException:
            return self._get_mock_geocode(query)

    def reverse_geocode(self, lat: float, lon: float) -> list:
        """Reverse geocode coordinates to a location name."""
        if not self.api_key:
            return [{'name': 'Unknown', 'lat': lat, 'lon': lon}]

        try:
            response = requests.get(
                f"{GEOCODING_BASE_URL}/reverse",
                params={
                    'lat': lat,
                    'lon': lon,
                    'limit': 1,
                    'appid': self.api_key,
                },
                timeout=10,
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException:
            return [{'name': 'Unknown', 'lat': lat, 'lon': lon}]

    def _get_cached_weather(self, lat: float, lon: float) -> Optional[WeatherCache]:
        """Retrieve cached weather if not expired."""
        tolerance = 0.01  # ~1km tolerance
        cached = WeatherCache.query.filter(
            WeatherCache.latitude.between(lat - tolerance, lat + tolerance),
            WeatherCache.longitude.between(lon - tolerance, lon + tolerance),
            WeatherCache.expires_at > datetime.now(timezone.utc)
        ).first()
        return cached

    def _cache_weather(self, lat: float, lon: float,
                       weather_data: dict = None, forecast_data: dict = None):
        """Store weather data in cache."""
        try:
            tolerance = 0.01
            existing = WeatherCache.query.filter(
                WeatherCache.latitude.between(lat - tolerance, lat + tolerance),
                WeatherCache.longitude.between(lon - tolerance, lon + tolerance),
            ).first()

            if existing:
                if weather_data:
                    existing.weather_data = weather_data
                if forecast_data:
                    existing.forecast_data = forecast_data
                existing.fetched_at = datetime.now(timezone.utc)
                existing.expires_at = datetime.now(timezone.utc) + timedelta(minutes=CACHE_DURATION_MINUTES)
            else:
                cache_entry = WeatherCache(
                    latitude=lat,
                    longitude=lon,
                    weather_data=weather_data or {},
                    forecast_data=forecast_data,
                    expires_at=datetime.now(timezone.utc) + timedelta(minutes=CACHE_DURATION_MINUTES),
                )
                db.session.add(cache_entry)

            db.session.commit()
        except Exception:
            db.session.rollback()

    @staticmethod
    def _get_mock_weather(lat: float, lon: float) -> dict:
        """Return mock weather data for development/demo."""
        return {
            "coord": {"lon": lon, "lat": lat},
            "weather": [
                {"id": 800, "main": "Clear", "description": "clear sky", "icon": "01d"}
            ],
            "main": {
                "temp": 22.5, "feels_like": 21.8, "temp_min": 19.0,
                "temp_max": 25.0, "pressure": 1013, "humidity": 65
            },
            "visibility": 10000,
            "wind": {"speed": 3.5, "deg": 180, "gust": 5.2},
            "clouds": {"all": 10},
            "dt": int(datetime.now(timezone.utc).timestamp()),
            "sys": {"sunrise": 1640000000, "sunset": 1640040000, "country": "US"},
            "timezone": -18000,
            "name": "Demo City",
            "cod": 200,
            "_mock": True,
        }

    @staticmethod
    def _get_mock_forecast(lat: float, lon: float) -> dict:
        """Return mock 5-day forecast for development/demo."""
        now = datetime.now(timezone.utc)
        forecasts = []
        conditions = [
            ("Clear", "clear sky", "01d"),
            ("Clouds", "few clouds", "02d"),
            ("Clouds", "scattered clouds", "03d"),
            ("Rain", "light rain", "10d"),
            ("Clear", "clear sky", "01d"),
        ]
        for i in range(40):  # 5 days * 8 intervals
            dt = now + timedelta(hours=3 * i)
            cond = conditions[i % len(conditions)]
            forecasts.append({
                "dt": int(dt.timestamp()),
                "main": {
                    "temp": 20 + (i % 8) * 1.5 - 3,
                    "feels_like": 19 + (i % 8) * 1.5 - 3,
                    "temp_min": 17 + (i % 5),
                    "temp_max": 24 + (i % 5),
                    "pressure": 1013,
                    "humidity": 60 + (i % 20),
                },
                "weather": [{"main": cond[0], "description": cond[1], "icon": cond[2]}],
                "wind": {"speed": 2 + i % 5, "deg": 180},
                "dt_txt": dt.strftime('%Y-%m-%d %H:%M:%S'),
            })
        return {"list": forecasts, "city": {"name": "Demo City"}, "_mock": True}

    @staticmethod
    def _get_mock_geocode(query: str) -> list:
        """Return mock geocoding results."""
        cities = {
            'new york': [{'name': 'New York', 'lat': 40.7128, 'lon': -74.0060, 'country': 'US', 'state': 'New York'}],
            'london': [{'name': 'London', 'lat': 51.5074, 'lon': -0.1278, 'country': 'GB'}],
            'tokyo': [{'name': 'Tokyo', 'lat': 35.6762, 'lon': 139.6503, 'country': 'JP'}],
            'paris': [{'name': 'Paris', 'lat': 48.8566, 'lon': 2.3522, 'country': 'FR'}],
            'sydney': [{'name': 'Sydney', 'lat': -33.8688, 'lon': 151.2093, 'country': 'AU'}],
        }
        key = query.lower().strip()
        for city_key, data in cities.items():
            if city_key in key:
                return data
        return [{'name': query.title(), 'lat': 40.0, 'lon': -74.0, 'country': 'US'}]
