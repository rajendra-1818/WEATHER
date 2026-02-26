"""
Saved Locations API Routes
"""
from flask import Blueprint, request, jsonify
from app import db
from app.models import SavedLocation, SearchHistory

locations_bp = Blueprint('locations', __name__)


@locations_bp.route('/', methods=['GET'])
def get_locations():
    """Get all saved locations."""
    locations = SavedLocation.query.order_by(
        SavedLocation.is_default.desc(),
        SavedLocation.name.asc()
    ).all()
    return jsonify([loc.to_dict() for loc in locations])


@locations_bp.route('/', methods=['POST'])
def add_location():
    """Add a new saved location."""
    data = request.get_json()
    if not data or 'name' not in data or 'latitude' not in data or 'longitude' not in data:
        return jsonify({'error': 'name, latitude, and longitude are required'}), 400

    location = SavedLocation(
        name=data['name'],
        latitude=data['latitude'],
        longitude=data['longitude'],
        country=data.get('country'),
        state=data.get('state'),
        is_default=data.get('is_default', False),
    )

    if location.is_default:
        SavedLocation.query.update({SavedLocation.is_default: False})

    db.session.add(location)
    db.session.commit()
    return jsonify(location.to_dict()), 201


@locations_bp.route('/<int:location_id>', methods=['PUT'])
def update_location(location_id):
    """Update a saved location."""
    location = SavedLocation.query.get_or_404(location_id)
    data = request.get_json()

    if 'name' in data:
        location.name = data['name']
    if 'latitude' in data:
        location.latitude = data['latitude']
    if 'longitude' in data:
        location.longitude = data['longitude']
    if 'country' in data:
        location.country = data['country']
    if 'state' in data:
        location.state = data['state']
    if 'is_default' in data:
        if data['is_default']:
            SavedLocation.query.update({SavedLocation.is_default: False})
        location.is_default = data['is_default']

    db.session.commit()
    return jsonify(location.to_dict())


@locations_bp.route('/<int:location_id>', methods=['DELETE'])
def delete_location(location_id):
    """Delete a saved location."""
    location = SavedLocation.query.get_or_404(location_id)
    db.session.delete(location)
    db.session.commit()
    return jsonify({'message': 'Location deleted'}), 200


@locations_bp.route('/search-history', methods=['GET'])
def get_search_history():
    """Get recent search history."""
    limit = request.args.get('limit', 10, type=int)
    history = SearchHistory.query.order_by(
        SearchHistory.searched_at.desc()
    ).limit(limit).all()
    return jsonify([h.to_dict() for h in history])


@locations_bp.route('/search-history', methods=['POST'])
def add_search_history():
    """Record a search."""
    data = request.get_json()
    if not data or 'query' not in data:
        return jsonify({'error': 'query is required'}), 400

    entry = SearchHistory(
        query=data['query'],
        latitude=data.get('latitude'),
        longitude=data.get('longitude'),
        result_name=data.get('result_name'),
    )
    db.session.add(entry)
    db.session.commit()
    return jsonify(entry.to_dict()), 201
