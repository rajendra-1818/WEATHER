/**
 * useWeather - Custom hook for weather state management.
 */
import { useState, useCallback, useRef, useEffect } from 'react';
import weatherApi from '../services/weatherApi';
import {
  CurrentWeather,
  ForecastResponse,
  GeoLocation,
  SavedLocation,
  UnitSystem,
} from '../types/weather';

export function useWeather() {
  const [currentWeather, setCurrentWeather] = useState<CurrentWeather | null>(null);
  const [forecast, setForecast] = useState<ForecastResponse | null>(null);
  const [locationName, setLocationName] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [savedLocations, setSavedLocations] = useState<SavedLocation[]>([]);
  const [searchResults, setSearchResults] = useState<GeoLocation[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [unitSystem, setUnitSystem] = useState<UnitSystem>('metric');

  const searchTimeout = useRef<NodeJS.Timeout>();

  // Load weather for coordinates
  const loadWeather = useCallback(
    async (lat: number, lon: number, name: string = '') => {
      setIsLoading(true);
      setError(null);

      try {
        const [weather, forecastData] = await Promise.all([
          weatherApi.getCurrentWeather(lat, lon, unitSystem),
          weatherApi.getForecast(lat, lon, unitSystem),
        ]);
        setCurrentWeather(weather);
        setForecast(forecastData);
        setLocationName(name || weather.name);
      } catch (err: any) {
        setError(err.message || 'Failed to load weather data');
      } finally {
        setIsLoading(false);
      }
    },
    [unitSystem]
  );

  // Search cities with debounce
  const searchCity = useCallback((query: string) => {
    if (searchTimeout.current) clearTimeout(searchTimeout.current);

    if (query.length < 2) {
      setSearchResults([]);
      setIsSearching(false);
      return;
    }

    setIsSearching(true);
    searchTimeout.current = setTimeout(async () => {
      try {
        const results = await weatherApi.geocode(query);
        setSearchResults(results);
      } catch {
        setSearchResults([]);
      } finally {
        setIsSearching(false);
      }
    }, 300);
  }, []);

  const selectSearchResult = useCallback(
    (location: GeoLocation) => {
      setSearchResults([]);
      const name = [location.name, location.state, location.country]
        .filter(Boolean)
        .join(', ');
      loadWeather(location.lat, location.lon, name);
    },
    [loadWeather]
  );

  // Saved locations
  const loadSavedLocations = useCallback(async () => {
    try {
      const locations = await weatherApi.getSavedLocations();
      setSavedLocations(locations);
    } catch {
      /* ignore */
    }
  }, []);

  const saveCurrentLocation = useCallback(async () => {
    if (!currentWeather) return;
    try {
      await weatherApi.addLocation({
        name: locationName || currentWeather.name,
        latitude: currentWeather.coord.lat,
        longitude: currentWeather.coord.lon,
        country: currentWeather.sys?.country || undefined,
      });
      loadSavedLocations();
    } catch {
      /* ignore */
    }
  }, [currentWeather, locationName, loadSavedLocations]);

  const deleteLocation = useCallback(
    async (id: number) => {
      try {
        await weatherApi.deleteLocation(id);
        loadSavedLocations();
      } catch {
        /* ignore */
      }
    },
    [loadSavedLocations]
  );

  const toggleUnits = useCallback(() => {
    setUnitSystem((prev) => (prev === 'metric' ? 'imperial' : 'metric'));
  }, []);

  // Initial load
  useEffect(() => {
    loadWeather(40.7128, -74.006, 'New York');
    loadSavedLocations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Reload when units change
  useEffect(() => {
    if (currentWeather) {
      loadWeather(currentWeather.coord.lat, currentWeather.coord.lon, locationName);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [unitSystem]);

  return {
    currentWeather,
    forecast,
    locationName,
    isLoading,
    error,
    savedLocations,
    searchResults,
    isSearching,
    unitSystem,
    loadWeather,
    searchCity,
    selectSearchResult,
    saveCurrentLocation,
    deleteLocation,
    toggleUnits,
    refreshWeather: () =>
      currentWeather &&
      loadWeather(currentWeather.coord.lat, currentWeather.coord.lon, locationName),
  };
}
