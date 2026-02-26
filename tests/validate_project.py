#!/usr/bin/env python3
"""
Weather App - Project Validation Tests
Validates project structure, code syntax, and completeness.
Runs without external dependencies beyond Python stdlib.
"""
import os
import sys
import json
import ast
import re

WEATHER_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PASS = 0
FAIL = 0


def check(name, condition, detail=""):
    global PASS, FAIL
    if condition:
        PASS += 1
        print(f"  ✅ {name}")
    else:
        FAIL += 1
        print(f"  ❌ {name} — {detail}")


def test_project_structure():
    print("\n🔍 Project Structure")
    dirs = [
        "backend", "backend/app", "backend/app/routes", "backend/app/models",
        "backend/app/services", "backend/tests",
        "android-app", "android-app/app/src/main/java/com/weather/app",
        "android-app/app/src/main/java/com/weather/app/ui/screens",
        "android-app/app/src/main/java/com/weather/app/ui/theme",
        "android-app/app/src/main/java/com/weather/app/data/model",
        "android-app/app/src/main/java/com/weather/app/data/repository",
        "android-app/app/src/main/java/com/weather/app/data/api",
        "android-app/app/src/main/java/com/weather/app/data/db",
        "android-app/app/src/main/java/com/weather/app/di",
        "android-app/app/src/main/java/com/weather/app/utils",
        "android-app/app/src/main/res/values",
        "web-app/src/components", "web-app/src/hooks",
        "web-app/src/services", "web-app/src/types",
    ]
    for d in dirs:
        check(f"Directory: {d}", os.path.isdir(os.path.join(WEATHER_DIR, d)))


def test_backend_files():
    print("\n🐍 Backend (Python/Flask)")
    files = {
        "backend/run.py": ["create_app"],
        "backend/app/__init__.py": ["create_app", "SQLAlchemy", "Flask"],
        "backend/app/routes/weather.py": ["weather_bp", "get_current_weather", "get_forecast", "geocode"],
        "backend/app/routes/locations.py": ["locations_bp", "get_locations", "add_location", "delete_location"],
        "backend/app/routes/health.py": ["health_bp", "health_check"],
        "backend/app/models/models.py": ["SavedLocation", "WeatherCache", "SearchHistory"],
        "backend/app/services/weather_service.py": ["WeatherService", "get_current_weather", "get_forecast", "geocode"],
        "backend/tests/test_api.py": ["TestHealthCheck", "TestWeatherRoutes", "TestLocationRoutes"],
    }
    for filepath, keywords in files.items():
        full = os.path.join(WEATHER_DIR, filepath)
        exists = os.path.isfile(full)
        check(f"File exists: {filepath}", exists)
        if exists:
            content = open(full).read()
            for kw in keywords:
                check(f"  Contains '{kw}'", kw in content, f"Missing in {filepath}")

    # Validate Python syntax
    py_files = []
    for root, _, fnames in os.walk(os.path.join(WEATHER_DIR, "backend")):
        for f in fnames:
            if f.endswith(".py"):
                py_files.append(os.path.join(root, f))

    for pf in py_files:
        try:
            with open(pf) as fh:
                ast.parse(fh.read())
            check(f"Syntax valid: {os.path.relpath(pf, WEATHER_DIR)}", True)
        except SyntaxError as e:
            check(f"Syntax valid: {os.path.relpath(pf, WEATHER_DIR)}", False, str(e))


def test_backend_openapi():
    print("\n📜 OpenAPI Specification")
    path = os.path.join(WEATHER_DIR, "backend/openapi.yaml")
    check("openapi.yaml exists", os.path.isfile(path))
    if os.path.isfile(path):
        content = open(path).read()
        for keyword in ["openapi: 3.0", "/weather/current", "/weather/forecast",
                        "/weather/geocode", "/locations/", "schemas"]:
            check(f"  Contains '{keyword}'", keyword in content)


def test_android_files():
    print("\n🤖 Android App (Kotlin)")
    kt_files = {
        "MainActivity.kt": ["@AndroidEntryPoint", "ComponentActivity", "WeatherNavHost"],
        "WeatherApplication.kt": ["@HiltAndroidApp"],
        "ui/screens/HomeWeatherScreen.kt": ["HomeWeatherScreen", "CurrentTemperatureDisplay", "WeatherDetailsGrid", "GlassCard"],
        "ui/screens/SearchScreen.kt": ["SearchScreen", "SearchResultItem"],
        "ui/screens/SavedLocationsScreen.kt": ["SavedLocationsScreen", "SavedLocationItem"],
        "ui/screens/WeatherViewModel.kt": ["@HiltViewModel", "WeatherViewModel", "loadWeather", "searchCity"],
        "ui/screens/WeatherNavHost.kt": ["WeatherNavHost", "NavHost"],
        "ui/theme/Theme.kt": ["WeatherAppTheme", "WeatherTypography"],
        "data/model/WeatherModels.kt": ["CurrentWeatherResponse", "ForecastResponse", "GeoLocation", "WeatherUiState"],
        "data/repository/WeatherRepository.kt": ["WeatherRepository", "getCurrentWeather", "getForecast"],
        "data/api/WeatherApiService.kt": ["WeatherApiService", "getCurrentWeather", "getForecast", "geocode"],
        "data/db/WeatherDatabase.kt": ["WeatherDatabase", "CachedWeatherEntity", "FavoriteLocationEntity"],
        "di/AppModule.kt": ["@Module", "provideRetrofit", "provideWeatherDatabase"],
        "utils/WeatherUtils.kt": ["WeatherUtils", "getWeatherIconUrl", "formatTime", "getWindDirection"],
    }

    base = os.path.join(WEATHER_DIR, "android-app/app/src/main/java/com/weather/app")
    for filepath, keywords in kt_files.items():
        full = os.path.join(base, filepath)
        exists = os.path.isfile(full)
        check(f"Kotlin file: {filepath}", exists)
        if exists:
            content = open(full).read()
            for kw in keywords:
                check(f"  Contains '{kw}'", kw in content, f"Missing in {filepath}")

    # Check Gradle files
    for gf in ["android-app/build.gradle.kts", "android-app/app/build.gradle.kts",
                "android-app/settings.gradle.kts", "android-app/gradle.properties"]:
        check(f"Gradle: {gf}", os.path.isfile(os.path.join(WEATHER_DIR, gf)))

    # Check manifest
    manifest = os.path.join(WEATHER_DIR, "android-app/app/src/main/AndroidManifest.xml")
    check("AndroidManifest.xml exists", os.path.isfile(manifest))
    if os.path.isfile(manifest):
        content = open(manifest).read()
        for perm in ["INTERNET", "ACCESS_FINE_LOCATION"]:
            check(f"  Permission: {perm}", perm in content)

    # Check resources
    for res in ["values/strings.xml", "values/colors.xml", "values/themes.xml"]:
        check(f"Resource: {res}",
              os.path.isfile(os.path.join(WEATHER_DIR, f"android-app/app/src/main/res/{res}")))


def test_web_app():
    print("\n⚛️  Web App (React/TypeScript)")
    files = {
        "web-app/package.json": ["react", "typescript", "axios"],
        "web-app/tsconfig.json": ["compilerOptions", "jsx"],
        "web-app/src/App.tsx": ["useWeather", "SearchBar", "CurrentWeatherDisplay"],
        "web-app/src/index.tsx": ["ReactDOM", "App"],
        "web-app/src/types/weather.ts": ["CurrentWeather", "ForecastResponse", "GeoLocation", "UnitSystem"],
        "web-app/src/services/weatherApi.ts": ["getCurrentWeather", "getForecast", "geocode"],
        "web-app/src/services/weatherUtils.ts": ["getWeatherIconUrl", "formatTime", "getWeatherGradient"],
        "web-app/src/hooks/useWeather.ts": ["useWeather", "loadWeather", "searchCity"],
        "web-app/src/components/SearchBar.tsx": ["SearchBar", "onSearch", "onSelect"],
        "web-app/src/components/CurrentWeather.tsx": ["CurrentWeatherDisplay"],
        "web-app/src/components/HourlyForecast.tsx": ["HourlyForecast"],
        "web-app/src/components/DailyForecast.tsx": ["DailyForecast"],
        "web-app/src/components/WeatherDetails.tsx": ["WeatherDetails"],
        "web-app/src/components/SavedLocations.tsx": ["SavedLocations"],
    }
    for filepath, keywords in files.items():
        full = os.path.join(WEATHER_DIR, filepath)
        exists = os.path.isfile(full)
        check(f"File: {filepath}", exists)
        if exists:
            content = open(full).read()
            for kw in keywords:
                check(f"  Contains '{kw}'", kw in content, f"Missing in {filepath}")

    # Validate package.json
    pkg = os.path.join(WEATHER_DIR, "web-app/package.json")
    if os.path.isfile(pkg):
        try:
            data = json.load(open(pkg))
            check("package.json valid JSON", True)
            check("Has 'react' dependency", "react" in data.get("dependencies", {}))
            check("Has 'typescript' dependency", "typescript" in data.get("dependencies", {}))
            check("Has 'axios' dependency", "axios" in data.get("dependencies", {}))
        except json.JSONDecodeError:
            check("package.json valid JSON", False, "Invalid JSON")


def test_config_files():
    print("\n⚙️  Configuration Files")
    configs = [
        ".gitignore",
        "backend/requirements.txt",
        "backend/.env.example",
        "web-app/.env.example",
        "android-app/app/proguard-rules.pro",
    ]
    for cfg in configs:
        check(f"Config: {cfg}", os.path.isfile(os.path.join(WEATHER_DIR, cfg)))


def test_readme():
    print("\n📖 README")
    readme = os.path.join(WEATHER_DIR, "README.md")
    check("README.md exists", os.path.isfile(readme))
    if os.path.isfile(readme):
        content = open(readme).read()
        for section in ["Kotlin", "Flask", "React", "TypeScript", "OpenAPI",
                        "Architecture", "API"]:
            check(f"  Mentions '{section}'", section.lower() in content.lower(),
                  f"Missing section about {section}")


if __name__ == "__main__":
    print("=" * 60)
    print("  WEATHER APP — PROJECT VALIDATION SUITE")
    print("=" * 60)

    test_project_structure()
    test_backend_files()
    test_backend_openapi()
    test_android_files()
    test_web_app()
    test_config_files()
    test_readme()

    print("\n" + "=" * 60)
    print(f"  RESULTS: {PASS} passed, {FAIL} failed")
    print("=" * 60)

    sys.exit(0 if FAIL == 0 else 1)
