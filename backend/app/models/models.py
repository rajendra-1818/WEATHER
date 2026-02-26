"""
Database Models for Weather API
"""
from datetime import datetime, timezone
from app import db


class SavedLocation(db.Model):
    """User's saved/favorite locations."""
    __tablename__ = 'saved_locations'

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(255), nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    country = db.Column(db.String(100), nullable=True)
    state = db.Column(db.String(100), nullable=True)
    is_default = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc),
                           onupdate=lambda: datetime.now(timezone.utc))

    # Relationship to cached weather data
    weather_cache = db.relationship('WeatherCache', backref='location',
                                    lazy=True, cascade='all, delete-orphan')

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'country': self.country,
            'state': self.state,
            'is_default': self.is_default,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'updated_at': self.updated_at.isoformat() if self.updated_at else None,
        }


class WeatherCache(db.Model):
    """Cached weather data to reduce API calls."""
    __tablename__ = 'weather_cache'

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    location_id = db.Column(db.Integer, db.ForeignKey('saved_locations.id'), nullable=True)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    weather_data = db.Column(db.JSON, nullable=False)
    forecast_data = db.Column(db.JSON, nullable=True)
    fetched_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))
    expires_at = db.Column(db.DateTime, nullable=False)

    def is_expired(self):
        return datetime.now(timezone.utc) > self.expires_at

    def to_dict(self):
        return {
            'id': self.id,
            'location_id': self.location_id,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'weather_data': self.weather_data,
            'forecast_data': self.forecast_data,
            'fetched_at': self.fetched_at.isoformat() if self.fetched_at else None,
            'expires_at': self.expires_at.isoformat() if self.expires_at else None,
        }


class SearchHistory(db.Model):
    """User search history for autocomplete."""
    __tablename__ = 'search_history'

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    query = db.Column(db.String(255), nullable=False)
    latitude = db.Column(db.Float, nullable=True)
    longitude = db.Column(db.Float, nullable=True)
    result_name = db.Column(db.String(255), nullable=True)
    searched_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))

    def to_dict(self):
        return {
            'id': self.id,
            'query': self.query,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'result_name': self.result_name,
            'searched_at': self.searched_at.isoformat() if self.searched_at else None,
        }
