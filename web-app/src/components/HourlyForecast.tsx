import React from 'react';
import { ForecastResponse, UnitSystem } from '../types/weather';
import { getWeatherIconUrl, formatHour } from '../services/weatherUtils';

interface Props {
  forecast: ForecastResponse;
  unitSystem: UnitSystem;
}

const HourlyForecast: React.FC<Props> = ({ forecast }) => {
  const hourlyItems = forecast.list.slice(0, 8);

  return (
    <div className="glass-card">
      <h3>Hourly Forecast</h3>
      <div className="hourly-scroll">
        {hourlyItems.map((item, i) => (
          <div key={i} className="hourly-item">
            <span className="time">{formatHour(item.dt_txt)}</span>
            {item.weather[0] && (
              <img
                src={getWeatherIconUrl(item.weather[0].icon, 2)}
                alt={item.weather[0].description}
              />
            )}
            <span className="temp">{Math.round(item.main.temp)}°</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default HourlyForecast;
