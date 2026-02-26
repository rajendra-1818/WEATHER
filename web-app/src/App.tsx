import React, { useState } from 'react';
import { useWeather } from './hooks/useWeather';
import { getWeatherGradient } from './services/weatherUtils';
import SearchBar from './components/SearchBar';
import CurrentWeatherDisplay from './components/CurrentWeather';
import HourlyForecast from './components/HourlyForecast';
import DailyForecast from './components/DailyForecast';
import WeatherDetails from './components/WeatherDetails';
import SavedLocations from './components/SavedLocations';
import './styles/App.css';

const App: React.FC = () => {
  const {
    currentWeather, forecast, locationName, isLoading, error,
    savedLocations, searchResults, isSearching, unitSystem,
    searchCity, selectSearchResult, saveCurrentLocation,
    deleteLocation, toggleUnits, refreshWeather,
    loadWeather,
  } = useWeather();

  const [showLocations, setShowLocations] = useState(false);

  const gradient = getWeatherGradient(currentWeather?.weather[0]?.main);

  return (
    <div className="app" style={{ background: gradient }}>
      {/* Top Bar */}
      <div className="top-bar">
        <button className="icon-btn" onClick={() => setShowLocations(true)}>
          ☰
        </button>
        <div className="top-bar-actions">
          <button className="unit-toggle" onClick={toggleUnits}>
            {unitSystem === 'metric' ? '°C' : '°F'}
          </button>
          <button className="icon-btn" onClick={saveCurrentLocation}>
            🔖
          </button>
        </div>
      </div>

      {/* Search */}
      <SearchBar
        onSearch={searchCity}
        onSelect={selectSearchResult}
        results={searchResults}
        isSearching={isSearching}
      />

      {/* Content */}
      {isLoading ? (
        <div className="loading-container">
          <div className="spinner" />
          <p>Loading weather data...</p>
        </div>
      ) : error ? (
        <div className="error-container">
          <p style={{ fontSize: 48 }}>☁️</p>
          <p>{error}</p>
          <button className="retry-btn" onClick={refreshWeather}>
            Retry
          </button>
        </div>
      ) : (
        <>
          {currentWeather && (
            <CurrentWeatherDisplay
              weather={currentWeather}
              locationName={locationName}
              unitSystem={unitSystem}
            />
          )}

          {forecast && (
            <HourlyForecast forecast={forecast} unitSystem={unitSystem} />
          )}

          {currentWeather && (
            <WeatherDetails weather={currentWeather} unitSystem={unitSystem} />
          )}

          {forecast && (
            <DailyForecast forecast={forecast} unitSystem={unitSystem} />
          )}
        </>
      )}

      {/* Saved Locations Panel */}
      <SavedLocations
        locations={savedLocations}
        isOpen={showLocations}
        onClose={() => setShowLocations(false)}
        onSelect={(loc) => loadWeather(loc.latitude, loc.longitude, loc.name)}
        onDelete={deleteLocation}
      />
    </div>
  );
};

export default App;
