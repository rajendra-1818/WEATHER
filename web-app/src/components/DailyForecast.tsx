import React from 'react';
import { ForecastResponse, UnitSystem } from '../types/weather';
import { getWeatherIconUrl, formatShortDay } from '../services/weatherUtils';

interface Props {
  forecast: ForecastResponse;
  unitSystem: UnitSystem;
}

const DailyForecast: React.FC<Props> = ({ forecast }) => {
  // Group by day
  const dailyMap = new Map<string, typeof forecast.list>();
  forecast.list.forEach((item) => {
    const day = item.dt_txt.substring(0, 10);
    if (!dailyMap.has(day)) dailyMap.set(day, []);
    dailyMap.get(day)!.push(item);
  });

  const dailyForecasts = Array.from(dailyMap.entries())
    .slice(0, 5)
    .map(([, items]) => {
      const maxTemp = Math.max(...items.map((i) => i.main.temp_max));
      const minTemp = Math.min(...items.map((i) => i.main.temp_min));
      const midItem = items[Math.floor(items.length / 2)];
      return { item: midItem, minTemp, maxTemp };
    });

  return (
    <div className="glass-card">
      <h3>5-Day Forecast</h3>
      {dailyForecasts.map(({ item, minTemp, maxTemp }, i) => (
        <div key={i} className="daily-row">
          <span className="day">{formatShortDay(item.dt)}</span>
          {item.weather[0] && (
            <img
              src={getWeatherIconUrl(item.weather[0].icon, 2)}
              alt={item.weather[0].description}
            />
          )}
          <div className="daily-temps">
            <span className="low">{Math.round(minTemp)}°</span>
            <span className="high">{Math.round(maxTemp)}°</span>
          </div>
        </div>
      ))}
    </div>
  );
};

export default DailyForecast;
