"""
Tests for Weather API Backend
"""
import pytest
import json
from app import create_app, db


@pytest.fixture
def app():
    """Create test application."""
    app = create_app()
    app.config['TESTING'] = True
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'

    with app.app_context():
        db.create_all()
        yield app
        db.session.remove()
        db.drop_all()


@pytest.fixture
def client(app):
    """Create test client."""
    return app.test_client()


class TestHealthCheck:
    def test_health_endpoint(self, client):
        response = client.get('/api/health')
        assert response.status_code == 200
        data = json.loads(response.data)
        assert data['status'] == 'healthy'
        assert data['service'] == 'Weather API'


class TestWeatherRoutes:
    def test_current_weather_requires_params(self, client):
        response = client.get('/api/weather/current')
        assert response.status_code == 400

    def test_current_weather_with_coords(self, client):
        response = client.get('/api/weather/current?lat=40.7128&lon=-74.006')
        assert response.status_code == 200
        data = json.loads(response.data)
        assert 'main' in data
        assert 'temp' in data['main']

    def test_forecast_requires_params(self, client):
        response = client.get('/api/weather/forecast')
        assert response.status_code == 400

    def test_forecast_with_coords(self, client):
        response = client.get('/api/weather/forecast?lat=40.7128&lon=-74.006')
        assert response.status_code == 200
        data = json.loads(response.data)
        assert 'list' in data

    def test_geocode_requires_query(self, client):
        response = client.get('/api/weather/geocode')
        assert response.status_code == 400

    def test_geocode_with_city(self, client):
        response = client.get('/api/weather/geocode?q=New York')
        assert response.status_code == 200
        data = json.loads(response.data)
        assert len(data) > 0
        assert data[0]['name'] == 'New York'


class TestLocationRoutes:
    def test_get_empty_locations(self, client):
        response = client.get('/api/locations/')
        assert response.status_code == 200
        assert json.loads(response.data) == []

    def test_add_location(self, client):
        response = client.post('/api/locations/', json={
            'name': 'New York',
            'latitude': 40.7128,
            'longitude': -74.006,
            'country': 'US',
        })
        assert response.status_code == 201
        data = json.loads(response.data)
        assert data['name'] == 'New York'
        assert data['id'] is not None

    def test_add_location_missing_fields(self, client):
        response = client.post('/api/locations/', json={'name': 'Test'})
        assert response.status_code == 400

    def test_update_location(self, client):
        # Create
        resp = client.post('/api/locations/', json={
            'name': 'Test', 'latitude': 0, 'longitude': 0,
        })
        loc_id = json.loads(resp.data)['id']

        # Update
        response = client.put(f'/api/locations/{loc_id}', json={'name': 'Updated'})
        assert response.status_code == 200
        assert json.loads(response.data)['name'] == 'Updated'

    def test_delete_location(self, client):
        resp = client.post('/api/locations/', json={
            'name': 'Test', 'latitude': 0, 'longitude': 0,
        })
        loc_id = json.loads(resp.data)['id']

        response = client.delete(f'/api/locations/{loc_id}')
        assert response.status_code == 200

        # Verify deleted
        response = client.get('/api/locations/')
        assert json.loads(response.data) == []

    def test_search_history(self, client):
        client.post('/api/locations/search-history', json={
            'query': 'New York',
            'latitude': 40.7128,
            'longitude': -74.006,
        })
        response = client.get('/api/locations/search-history')
        assert response.status_code == 200
        data = json.loads(response.data)
        assert len(data) == 1
        assert data[0]['query'] == 'New York'

    def test_set_default_location(self, client):
        # Add two locations
        client.post('/api/locations/', json={
            'name': 'City 1', 'latitude': 0, 'longitude': 0, 'is_default': True,
        })
        client.post('/api/locations/', json={
            'name': 'City 2', 'latitude': 1, 'longitude': 1, 'is_default': True,
        })

        response = client.get('/api/locations/')
        data = json.loads(response.data)
        defaults = [loc for loc in data if loc['is_default']]
        assert len(defaults) == 1
        assert defaults[0]['name'] == 'City 2'
