import React from 'react';
import { CurrentWeather, UnitSystem } from '../types/weather';
import { formatTime, getWindDirection } from '../services/weatherUtils';

interface Props {
  weather: CurrentWeather;
  unitSystem: UnitSystem;
}

const WeatherDetails: React.FC<Props> = ({ weather, unitSystem }) => {
  const speedUnit = unitSystem === 'metric' ? 'm/s' : 'mph';

  return (
    <div className="details-grid">
      <div className="detail-card">
        <div className="label">💧 Humidity</div>
        <div className="value">{weather.main.humidity}%</div>
      </div>

      <div className="detail-card">
        <div className="label">💨 Wind</div>
        <div className="value">{weather.wind.speed} {speedUnit}</div>
        <div className="subtitle">{getWindDirection(weather.wind.deg)}</div>
      </div>

      <div className="detail-card">
        <div className="label">🔽 Pressure</div>
        <div className="value">{weather.main.pressure} hPa</div>
      </div>

      <div className="detail-card">
        <div className="label">👁 Visibility</div>
        <div className="value">{Math.round((weather.visibility ?? 0) / 1000)} km</div>
      </div>

      <div className="detail-card">
        <div className="label">🌅 Sunrise</div>
        <div className="value">
          {weather.sys?.sunrise ? formatTime(weather.sys.sunrise) : '--'}
        </div>
      </div>

      <div className="detail-card">
        <div className="label">🌇 Sunset</div>
        <div className="value">
          {weather.sys?.sunset ? formatTime(weather.sys.sunset) : '--'}
        </div>
      </div>
    </div>
  );
};

export default WeatherDetails;
