import React from 'react';
import { CurrentWeather as CurrentWeatherType, UnitSystem } from '../types/weather';
import { getWeatherIconUrl } from '../services/weatherUtils';

interface Props {
  weather: CurrentWeatherType;
  locationName: string;
  unitSystem: UnitSystem;
}

const CurrentWeatherDisplay: React.FC<Props> = ({ weather, locationName, unitSystem }) => {
  const condition = weather.weather[0];
  const tempSymbol = unitSystem === 'metric' ? '°C' : '°F';
  const speedUnit = unitSystem === 'metric' ? 'm/s' : 'mph';

  return (
    <div className="weather-hero">
      <h1 className="location-name">{locationName}</h1>

      {condition && (
        <img
          src={getWeatherIconUrl(condition.icon)}
          alt={condition.description}
          className="weather-icon-large"
        />
      )}

      <div className="current-temp">{Math.round(weather.main.temp)}°</div>

      {condition && (
        <div className="condition-text">{condition.description}</div>
      )}

      <div className="high-low">
        H: {Math.round(weather.main.temp_max)}° L: {Math.round(weather.main.temp_min)}°
      </div>

      <div className="feels-like">
        Feels like {Math.round(weather.main.feels_like)}{tempSymbol}
      </div>
    </div>
  );
};

export default CurrentWeatherDisplay;
