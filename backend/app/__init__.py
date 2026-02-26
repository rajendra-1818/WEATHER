"""
Weather API Backend - Flask Application Factory
"""
import os
from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

db = SQLAlchemy()
migrate = Migrate()


def create_app(config_name=None):
    """Application factory pattern."""
    app = Flask(__name__)

    # Configuration
    app.config['SECRET_KEY'] = os.environ.get('SECRET_KEY', 'dev-secret-key-change-in-production')
    app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get(
        'DATABASE_URL', 'sqlite:///weather.db'
    )
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['OPENWEATHERMAP_API_KEY'] = os.environ.get('OPENWEATHERMAP_API_KEY', '')

    # Initialize extensions
    CORS(app, resources={r"/api/*": {"origins": "*"}})
    db.init_app(app)
    migrate.init_app(app, db)

    # Register blueprints
    from app.routes.weather import weather_bp
    from app.routes.locations import locations_bp
    from app.routes.health import health_bp

    app.register_blueprint(weather_bp, url_prefix='/api/weather')
    app.register_blueprint(locations_bp, url_prefix='/api/locations')
    app.register_blueprint(health_bp, url_prefix='/api')

    # Create tables
    with app.app_context():
        db.create_all()

    return app
