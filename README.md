# 🌤 WEATHER — Android Weather Application

A full-stack weather application featuring a native **Android app** (Kotlin/Jetpack Compose), a **React/TypeScript web app**, and a **Python Flask REST API backend** with SQLite database and OpenAPI specification.

![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Web-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple)
![React](https://img.shields.io/badge/React-18-61DAFB)
![Flask](https://img.shields.io/badge/Flask-3.x-green)

---
## Screenshots
<img width="1183" height="836" alt="Screenshot 2026-02-25 at 9 59 54 PM" src="https://github.com/user-attachments/assets/5656c9aa-ee5e-4136-8014-6fd5cb4b72e4" />

<img width="352" height="723" alt="Screenshot 2026-02-25 at 9 57 58 PM" src="https://github.com/user-attachments/assets/df7bb720-359c-422e-8c5a-8a8e4bc9d77e" />
<img width="351" height="709" alt="Screenshot 2026-02-25 at 9 57 52 PM" src="https://github.com/user-attachments/assets/15b83db1-850b-4c93-806a-2d3c3bd7b49b" />
---
## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Screenshots](#-screenshots)

---

## ✨ Features

| Feature | Android | Web |
|---------|---------|-----|
| Current weather display | ✅ | ✅ |
| 5-day / 3-hour forecast | ✅ | ✅ |
| City search with geocoding | ✅ | ✅ |
| Save favorite locations | ✅ | ✅ |
| Unit toggle (°C / °F) | ✅ | ✅ |
| Dynamic weather gradients | ✅ | ✅ |
| Offline caching (Room DB) | ✅ | — |
| Pull-to-refresh | ✅ | ✅ |
| Glassmorphism UI | ✅ | ✅ |
| Hourly forecast carousel | ✅ | ✅ |
| Weather details grid | ✅ | ✅ |

---

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐
│  Android App    │    │    Web App       │
│  Kotlin/Compose │    │  React/TypeScript│
│                 │    │                  │
│  MVVM + Hilt    │    │  Hooks + Axios   │
│  Room DB Cache  │    │                  │
└────────┬────────┘    └────────┬─────────┘
         │                      │
         └──────────┬───────────┘
                    │ REST API
         ┌──────────▼───────────┐
         │   Flask Backend      │
         │   Python + SQLAlchemy│
         │                      │
         │  OpenAPI 3.0 Spec    │
         └──────────┬───────────┘
                    │
         ┌──────────▼───────────┐
         │  OpenWeatherMap API  │
         │  + SQLite Database   │
         └──────────────────────┘
```

### Design Patterns

- **Android**: MVVM (Model-View-ViewModel) with Repository pattern, Dependency Injection via Hilt
- **Web**: Component-based architecture with custom hooks for state management
- **Backend**: Application Factory pattern, Blueprint-based routing, Service layer with caching

---

## 🛠 Tech Stack

### Android App
| Technology | Purpose |
|------------|---------|
| **Kotlin 1.9** | Primary language |
| **Jetpack Compose** | Declarative UI framework |
| **Material3 (Material You)** | Design system |
| **Hilt (Dagger)** | Dependency injection |
| **Retrofit 2 + OkHttp** | HTTP networking |
| **Room Database** | Local caching / offline support |
| **Coroutines + Flow** | Async operations & reactive streams |
| **Navigation Compose** | Screen navigation |
| **Coil** | Image loading (weather icons) |
| **Gson** | JSON serialization |
| **Android SDK 34** | Target API level |

### Web App
| Technology | Purpose |
|------------|---------|
| **React 18** | UI library |
| **TypeScript 5.3** | Type-safe JavaScript |
| **Axios** | HTTP client |
| **CSS3** | Styling (glassmorphism, gradients) |
| **Custom Hooks** | State management |

### Backend API
| Technology | Purpose |
|------------|---------|
| **Python 3.11+** | Server-side language |
| **Flask 3.x** | Web framework |
| **Flask-SQLAlchemy** | ORM for database |
| **Flask-Migrate** | Database migrations |
| **Flask-CORS** | Cross-origin support |
| **SQLite** | Database (dev), PostgreSQL ready |
| **Gunicorn** | Production WSGI server |
| **pytest** | Testing framework |

### API Specification
| Technology | Purpose |
|------------|---------|
| **OpenAPI 3.0.3** | REST API specification |
| **OpenWeatherMap API** | Weather data provider |

---

## 📁 Project Structure

```
WEATHER/
├── android-app/                    # 🤖 Native Android Application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/weather/app/
│   │   │   │   ├── MainActivity.kt          # Entry point
│   │   │   │   ├── WeatherApplication.kt    # Hilt Application
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeWeatherScreen.kt      # Main weather display
│   │   │   │   │   │   ├── SearchScreen.kt           # City search
│   │   │   │   │   │   ├── SavedLocationsScreen.kt   # Saved locations
│   │   │   │   │   │   ├── WeatherViewModel.kt       # MVVM ViewModel
│   │   │   │   │   │   └── WeatherNavHost.kt         # Navigation graph
│   │   │   │   │   └── theme/
│   │   │   │   │       └── Theme.kt          # Material3 theme
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   └── WeatherApiService.kt  # Retrofit API interface
│   │   │   │   │   ├── db/
│   │   │   │   │   │   └── WeatherDatabase.kt    # Room DB + DAOs
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── WeatherModels.kt      # Data classes
│   │   │   │   │   └── repository/
│   │   │   │   │       └── WeatherRepository.kt   # Repository pattern
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt          # Hilt DI module
│   │   │   │   └── utils/
│   │   │   │       └── WeatherUtils.kt       # Utility functions
│   │   │   ├── res/
│   │   │   │   └── values/                   # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── build.gradle.kts                  # App-level build config
│   │   └── proguard-rules.pro
│   ├── build.gradle.kts                      # Project-level build config
│   ├── settings.gradle.kts
│   └── gradle.properties
│
├── web-app/                        # ⚛️  React/TypeScript Web App
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/
│   │   │   ├── SearchBar.tsx         # City search with autocomplete
│   │   │   ├── CurrentWeather.tsx    # Current conditions hero
│   │   │   ├── HourlyForecast.tsx    # Hourly scroll forecast
│   │   │   ├── DailyForecast.tsx     # 5-day forecast
│   │   │   ├── WeatherDetails.tsx    # Details grid
│   │   │   └── SavedLocations.tsx    # Side panel
│   │   ├── hooks/
│   │   │   └── useWeather.ts         # Custom weather hook
│   │   ├── services/
│   │   │   ├── weatherApi.ts         # API client (Axios)
│   │   │   └── weatherUtils.ts       # Formatting utilities
│   │   ├── types/
│   │   │   └── weather.ts            # TypeScript interfaces
│   │   ├── styles/
│   │   │   └── App.css               # Global styles
│   │   ├── App.tsx                   # Root component
│   │   └── index.tsx                 # Entry point
│   ├── package.json
│   ├── tsconfig.json
│   └── .env.example
│
├── backend/                        # 🐍 Python Flask API
│   ├── app/
│   │   ├── __init__.py               # Flask app factory
│   │   ├── routes/
│   │   │   ├── weather.py            # /api/weather/* endpoints
│   │   │   ├── locations.py          # /api/locations/* CRUD
│   │   │   └── health.py             # /api/health check
│   │   ├── models/
│   │   │   └── models.py             # SQLAlchemy models
│   │   ├── services/
│   │   │   └── weather_service.py    # OpenWeatherMap integration
│   │   └── utils/
│   ├── tests/
│   │   └── test_api.py               # pytest test suite
│   ├── openapi.yaml                  # OpenAPI 3.0.3 specification
│   ├── requirements.txt
│   ├── run.py                        # Server entry point
│   └── .env.example
│
├── tests/
│   └── validate_project.py          # Project validation script
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Python 3.11+
- Node.js 18+ & npm
- Android Studio Hedgehog+ (for Android app)
- OpenWeatherMap API key (free at [openweathermap.org](https://openweathermap.org/api))

### 1. Backend Setup

```bash
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate    # Linux/macOS
venv\Scripts\activate       # Windows

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env and add your OPENWEATHERMAP_API_KEY

# Run the server
python run.py
# Server starts at http://localhost:5000
```

### 2. Web App Setup

```bash
cd web-app

# Install dependencies
npm install

# Configure environment
cp .env.example .env.local

# Start development server
npm run dev
# App opens at http://localhost:3000
```

### 3. Android App Setup

1. Open `android-app/` in **Android Studio**
2. Add to `local.properties`:
   ```properties
   WEATHER_API_BASE_URL=http://10.0.2.2:5000/api
   OPENWEATHERMAP_API_KEY=your_key_here
   ```
3. Sync Gradle and run on emulator or device

---

## 📡 API Documentation

Full OpenAPI 3.0.3 spec available in [`backend/openapi.yaml`](backend/openapi.yaml).

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/health` | Health check |
| `GET` | `/api/weather/current?lat=&lon=` | Current weather |
| `GET` | `/api/weather/forecast?lat=&lon=` | 5-day forecast |
| `GET` | `/api/weather/geocode?q=` | City → coordinates |
| `GET` | `/api/weather/reverse-geocode?lat=&lon=` | Coordinates → city |
| `GET` | `/api/locations/` | List saved locations |
| `POST` | `/api/locations/` | Add location |
| `PUT` | `/api/locations/{id}` | Update location |
| `DELETE` | `/api/locations/{id}` | Delete location |
| `GET` | `/api/locations/search-history` | Search history |
| `POST` | `/api/locations/search-history` | Record search |

### Example

```bash
# Get current weather for New York
curl "http://localhost:5000/api/weather/current?lat=40.7128&lon=-74.006&units=metric"

# Search for a city
curl "http://localhost:5000/api/weather/geocode?q=London"

# Save a location
curl -X POST "http://localhost:5000/api/locations/" \
  -H "Content-Type: application/json" \
  -d '{"name":"New York","latitude":40.7128,"longitude":-74.006,"country":"US"}'
```

---

## 🧪 Testing

### Backend Tests (pytest)
```bash
cd backend
pytest tests/ -v
```

### Project Validation
```bash
python tests/validate_project.py
```

### Android Tests
```bash
cd android-app
./gradlew test              # Unit tests
./gradlew connectedCheck    # Instrumented tests
```

### Web App Tests
```bash
cd web-app
npm test
```

---

## 🎨 UI Design

The app uses a **glassmorphism** design language with dynamic weather-themed gradients:

- ☀️ **Clear**: Sky blue gradient (`#4FC3F7` → `#0288D1`)
- ☁️ **Cloudy**: Grey-blue gradient (`#90A4AE` → `#546E7A`)
- 🌧 **Rain**: Dark grey gradient (`#455A64` → `#263238`)
- ⛈ **Thunderstorm**: Deep indigo gradient (`#1A237E` → `#0D47A1`)
- ❄️ **Snow**: Soft grey gradient (`#CFD8DC` → `#78909C`)
- 🌫 **Fog**: Muted grey gradient (`#78909C` → `#455A64`)

### Key Design Elements

- **Glass Cards**: Semi-transparent cards with blur effects
- **Material 3**: Dynamic color theming on Android
- **Responsive**: Mobile-first design for web app
- **Weather Icons**: OpenWeatherMap icon set

---

## 📦 Dependencies

### Android (Gradle)
- `androidx.compose:compose-bom:2024.01.00`
- `com.google.dagger:hilt-android:2.48.1`
- `com.squareup.retrofit2:retrofit:2.9.0`
- `androidx.room:room-runtime:2.6.1`
- `io.coil-kt:coil-compose:2.5.0`
- `com.google.android.gms:play-services-location:21.0.1`

### Web (npm)
- `react@^18.2.0`
- `typescript@^5.3.3`
- `axios@^1.6.2`

### Backend (pip)
- `Flask==3.0.0`
- `Flask-SQLAlchemy==3.1.1`
- `Flask-CORS==4.0.0`
- `Flask-Migrate==4.0.5`
- `requests==2.31.0`
- `gunicorn==21.2.0`
- `pytest==7.4.3`

---
<img width="1183" height="836" alt="Screenshot 2026-02-25 at 9 59 54 PM" src="https://github.com/user-attachments/assets/5656c9aa-ee5e-4136-8014-6fd5cb4b72e4" />

<img width="352" height="723" alt="Screenshot 2026-02-25 at 9 57 58 PM" src="https://github.com/user-attachments/assets/df7bb720-359c-422e-8c5a-8a8e4bc9d77e" />
<img width="351" height="709" alt="Screenshot 2026-02-25 at 9 57 52 PM" src="https://github.com/user-attachments/assets/15b83db1-850b-4c93-806a-2d3c3bd7b49b" />

