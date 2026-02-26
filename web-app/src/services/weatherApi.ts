/**
 * Weather API Service - Handles all backend communication.
 */
import axios, { AxiosInstance } from 'axios';
import {
  CurrentWeather,
  ForecastResponse,
  GeoLocation,
  SavedLocation,
} from '../types/weather';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:5000/api';

class WeatherApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 15000,
      headers: { 'Content-Type': 'application/json' },
    });
  }

  // ─── Weather ────────────────────────────────────────────────

  async getCurrentWeather(
    lat: number,
    lon: number,
    units: string = 'metric'
  ): Promise<CurrentWeather> {
    const { data } = await this.client.get('/weather/current', {
      params: { lat, lon, units },
    });
    return data;
  }

  async getForecast(
    lat: number,
    lon: number,
    units: string = 'metric'
  ): Promise<ForecastResponse> {
    const { data } = await this.client.get('/weather/forecast', {
      params: { lat, lon, units },
    });
    return data;
  }

  async geocode(query: string, limit: number = 5): Promise<GeoLocation[]> {
    const { data } = await this.client.get('/weather/geocode', {
      params: { q: query, limit },
    });
    return data;
  }

  async reverseGeocode(lat: number, lon: number): Promise<GeoLocation[]> {
    const { data } = await this.client.get('/weather/reverse-geocode', {
      params: { lat, lon },
    });
    return data;
  }

  // ─── Saved Locations ────────────────────────────────────────

  async getSavedLocations(): Promise<SavedLocation[]> {
    const { data } = await this.client.get('/locations/');
    return data;
  }

  async addLocation(location: Omit<SavedLocation, 'id'>): Promise<SavedLocation> {
    const { data } = await this.client.post('/locations/', location);
    return data;
  }

  async deleteLocation(id: number): Promise<void> {
    await this.client.delete(`/locations/${id}`);
  }

  // ─── Health ─────────────────────────────────────────────────

  async healthCheck(): Promise<{ status: string; service: string }> {
    const { data } = await this.client.get('/health');
    return data;
  }
}

export const weatherApi = new WeatherApiService();
export default weatherApi;
