import React from 'react';
import { SavedLocation } from '../types/weather';

interface Props {
  locations: SavedLocation[];
  isOpen: boolean;
  onClose: () => void;
  onSelect: (location: SavedLocation) => void;
  onDelete: (id: number) => void;
}

const SavedLocations: React.FC<Props> = ({
  locations, isOpen, onClose, onSelect, onDelete,
}) => {
  return (
    <>
      {isOpen && <div className="overlay" onClick={onClose} />}
      <div className={`saved-panel ${isOpen ? 'open' : ''}`}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <h2>Saved Locations</h2>
          <button className="icon-btn" onClick={onClose}>✕</button>
        </div>

        {locations.length === 0 ? (
          <div style={{ textAlign: 'center', opacity: 0.5, marginTop: 48 }}>
            <p style={{ fontSize: 40, marginBottom: 12 }}>📍</p>
            <p>No saved locations yet</p>
            <p style={{ fontSize: 13, marginTop: 4 }}>
              Search for a city and save it
            </p>
          </div>
        ) : (
          locations.map((loc) => (
            <div
              key={loc.id}
              className="saved-location-item"
              onClick={() => { onSelect(loc); onClose(); }}
            >
              <div>
                <div className="name">
                  {loc.is_default ? '⭐ ' : '📍 '}
                  {loc.name}
                </div>
                <div className="meta">
                  {[loc.state, loc.country].filter(Boolean).join(', ')}
                </div>
              </div>
              <button
                className="delete-btn"
                onClick={(e) => {
                  e.stopPropagation();
                  if (loc.id) onDelete(loc.id);
                }}
              >
                🗑
              </button>
            </div>
          ))
        )}
      </div>
    </>
  );
};

export default SavedLocations;
