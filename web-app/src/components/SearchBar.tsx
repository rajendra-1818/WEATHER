import React, { useState } from 'react';
import { GeoLocation } from '../types/weather';

interface SearchBarProps {
  onSearch: (query: string) => void;
  onSelect: (location: GeoLocation) => void;
  results: GeoLocation[];
  isSearching: boolean;
}

const SearchBar: React.FC<SearchBarProps> = ({
  onSearch, onSelect, results, isSearching,
}) => {
  const [query, setQuery] = useState('');
  const [focused, setFocused] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setQuery(value);
    onSearch(value);
  };

  const handleSelect = (location: GeoLocation) => {
    setQuery('');
    setFocused(false);
    onSelect(location);
  };

  const showResults = focused && (results.length > 0 || (query.length >= 2 && !isSearching));

  return (
    <div className="search-container">
      <input
        type="text"
        className="search-input"
        placeholder="Search city..."
        value={query}
        onChange={handleChange}
        onFocus={() => setFocused(true)}
        onBlur={() => setTimeout(() => setFocused(false), 200)}
      />
      {showResults && (
        <div className="search-results">
          {isSearching ? (
            <div className="search-result-item">
              <span>Searching...</span>
            </div>
          ) : results.length > 0 ? (
            results.map((loc, i) => (
              <div
                key={`${loc.lat}-${loc.lon}-${i}`}
                className="search-result-item"
                onMouseDown={() => handleSelect(loc)}
              >
                <h4>{loc.name}</h4>
                <span>
                  {[loc.state, loc.country].filter(Boolean).join(', ')}
                </span>
              </div>
            ))
          ) : (
            <div className="search-result-item">
              <span>No results found</span>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default SearchBar;
