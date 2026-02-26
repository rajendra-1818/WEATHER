package com.weather.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Navigation graph for the Weather App.
 */
@Composable
fun WeatherNavHost() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeWeatherScreen(
                viewModel = viewModel,
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToLocations = { navController.navigate("locations") }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("locations") {
            SavedLocationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLocationSelected = {
                    viewModel.selectSavedLocation(it)
                    navController.popBackStack()
                }
            )
        }
    }
}
