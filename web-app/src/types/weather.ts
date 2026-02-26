/**
 * Weather API TypeScript Type Definitions
 */

export interface Coord {
  lon: number;
  lat: number;
}

export interface WeatherCondition {
  id: number;
  main: string;
  description: string;
  icon: string;
}

export interface MainWeatherData {
  temp: number;
  feels_like: number;
  temp_min: number;
  temp_max: number;
  pressure: number;
  humidity: number;
  sea_level?: number;
  grnd_level?: number;
}

export interface Wind {
  speed: number;
  deg: number;
  gust?: number;
}

export interface Sys {
  sunrise?: number;
  sunset?: number;
  country?: string;
}

export interface CurrentWeather {
  coord: Coord;
  weather: WeatherCondition[];
  main: MainWeatherData;
  visibility?: number;
  wind: Wind;
  clouds?: { all: number };
  dt: number;
  sys?: Sys;
  timezone?: number;
  name: string;
  cod?: number;
}

export interface ForecastItem {
  dt: number;
  main: MainWeatherData;
  weather: WeatherCondition[];
  wind: Wind;
  dt_txt: string;
  pop?: number;
}

export interface ForecastResponse {
  list: ForecastItem[];
  city?: {
    name?: string;
    coord?: Coord;
    country?: string;
    timezone?: number;
  };
}

export interface GeoLocation {
  name: string;
  lat: number;
  lon: number;
  country?: string;
  state?: string;
}

export interface SavedLocation {
  id?: number;
  name: string;
  latitude: number;
  longitude: number;
  country?: string;
  state?: string;
  is_default?: boolean;
  created_at?: string;
}

export type UnitSystem = 'metric' | 'imperial';

export interface WeatherState {
  currentWeather: CurrentWeather | null;
  forecast: ForecastResponse | null;
  locationName: string;
  isLoading: boolean;
  error: string | null;
  savedLocations: SavedLocation[];
  searchResults: GeoLocation[];
  isSearching: boolean;
  unitSystem: UnitSystem;
}
