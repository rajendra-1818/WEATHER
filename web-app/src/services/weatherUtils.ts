/**
 * Weather utility functions for formatting and display.
 */

export function getWeatherIconUrl(iconCode: string, size: number = 4): string {
  return `https://openweathermap.org/img/wn/${iconCode}@${size}x.png`;
}

export function formatTemperature(temp: number, unit: 'metric' | 'imperial'): string {
  return `${Math.round(temp)}°${unit === 'metric' ? 'C' : 'F'}`;
}

export function formatTime(timestamp: number): string {
  return new Date(timestamp * 1000).toLocaleTimeString([], {
    hour: 'numeric',
    minute: '2-digit',
  });
}

export function formatHour(dtTxt: string): string {
  const date = new Date(dtTxt);
  return date.toLocaleTimeString([], { hour: 'numeric' }).toLowerCase();
}

export function formatShortDay(timestamp: number): string {
  return new Date(timestamp * 1000).toLocaleDateString([], { weekday: 'short' });
}

export function getWindDirection(degrees: number | undefined): string {
  if (degrees === undefined) return '';
  const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
  const index = Math.round(((degrees % 360) + 360) % 360 / 45) % 8;
  return directions[index];
}

export type WeatherType = 'clear' | 'cloudy' | 'rainy' | 'thunderstorm' | 'snowy' | 'foggy';

export function getWeatherType(main?: string): WeatherType {
  switch (main?.toLowerCase()) {
    case 'clear': return 'clear';
    case 'clouds': return 'cloudy';
    case 'rain':
    case 'drizzle': return 'rainy';
    case 'thunderstorm': return 'thunderstorm';
    case 'snow': return 'snowy';
    case 'mist':
    case 'fog':
    case 'haze':
    case 'smoke': return 'foggy';
    default: return 'clear';
  }
}

export function getWeatherGradient(main?: string): string {
  switch (getWeatherType(main)) {
    case 'clear': return 'linear-gradient(180deg, #4FC3F7, #0288D1)';
    case 'cloudy': return 'linear-gradient(180deg, #90A4AE, #546E7A)';
    case 'rainy': return 'linear-gradient(180deg, #455A64, #263238)';
    case 'thunderstorm': return 'linear-gradient(180deg, #1A237E, #0D47A1)';
    case 'snowy': return 'linear-gradient(180deg, #CFD8DC, #78909C)';
    case 'foggy': return 'linear-gradient(180deg, #78909C, #455A64)';
  }
}
