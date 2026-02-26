"""
Weather API Routes
"""
from flask import Blueprint, request, jsonify
from app.services import WeatherService

weather_bp = Blueprint('weather', __name__)
weather_service = WeatherService()


@weather_bp.route('/current', methods=['GET'])
def get_current_weather():
    """
    Get current weather data for a location.
    ---
    parameters:
      - name: lat
        in: query
        type: number
        required: true
        description: Latitude
      - name: lon
        in: query
        type: number
        required: true
        description: Longitude
      - name: units
        in: query
        type: string
        default: metric
        description: Units (metric, imperial, standard)
    responses:
      200:
        description: Current weather data
      400:
        description: Missing required parameters
    """
    lat = request.args.get('lat', type=float)
    lon = request.args.get('lon', type=float)
    units = request.args.get('units', 'metric')

    if lat is None or lon is None:
        return jsonify({'error': 'lat and lon parameters are required'}), 400

    data = weather_service.get_current_weather(lat, lon, units)
    return jsonify(data)


@weather_bp.route('/forecast', methods=['GET'])
def get_forecast():
    """
    Get 5-day weather forecast.
    ---
    parameters:
      - name: lat
        in: query
        type: number
        required: true
      - name: lon
        in: query
        type: number
        required: true
      - name: units
        in: query
        type: string
        default: metric
    responses:
      200:
        description: 5-day forecast data
    """
    lat = request.args.get('lat', type=float)
    lon = request.args.get('lon', type=float)
    units = request.args.get('units', 'metric')

    if lat is None or lon is None:
        return jsonify({'error': 'lat and lon parameters are required'}), 400

    data = weather_service.get_forecast(lat, lon, units)
    return jsonify(data)


@weather_bp.route('/geocode', methods=['GET'])
def geocode():
    """
    Geocode a city name to coordinates.
    ---
    parameters:
      - name: q
        in: query
        type: string
        required: true
        description: City name
      - name: limit
        in: query
        type: integer
        default: 5
    responses:
      200:
        description: List of matching locations
    """
    query = request.args.get('q', '')
    limit = request.args.get('limit', 5, type=int)

    if not query:
        return jsonify({'error': 'q parameter is required'}), 400

    data = weather_service.geocode(query, limit)
    return jsonify(data)


@weather_bp.route('/reverse-geocode', methods=['GET'])
def reverse_geocode():
    """
    Reverse geocode coordinates to location name.
    """
    lat = request.args.get('lat', type=float)
    lon = request.args.get('lon', type=float)

    if lat is None or lon is None:
        return jsonify({'error': 'lat and lon parameters are required'}), 400

    data = weather_service.reverse_geocode(lat, lon)
    return jsonify(data)
