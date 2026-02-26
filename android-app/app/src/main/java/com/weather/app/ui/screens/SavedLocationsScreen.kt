package com.weather.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weather.app.data.model.SavedLocation

/**
 * Saved Locations Screen - Manage favorite cities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsScreen(
    viewModel: WeatherViewModel,
    onBack: () -> Unit,
    onLocationSelected: (SavedLocation) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<SavedLocation?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Locations") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (uiState.savedLocations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.BookmarkBorder,
                        contentDescription = "No saved locations",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No saved locations yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Search for a city and tap the save icon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.savedLocations) { location ->
                    SavedLocationItem(
                        location = location,
                        onClick = { onLocationSelected(location) },
                        onDelete = { showDeleteDialog = location }
                    )
                }
            }
        }

        // Delete confirmation dialog
        showDeleteDialog?.let { location ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Remove Location") },
                text = { Text("Remove ${location.name} from saved locations?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.removeSavedLocation(location)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SavedLocationItem(
    location: SavedLocation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (location.isDefault) FontWeight.Bold else FontWeight.Normal
                )
                if (location.isDefault) {
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "Default",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        },
        supportingContent = {
            val detail = buildString {
                location.state?.let { append("$it, ") }
                location.country?.let { append(it) }
                if (isEmpty()) {
                    append("${String.format("%.2f", location.latitude)}, ${String.format("%.2f", location.longitude)}")
                }
            }
            Text(detail, style = MaterialTheme.typography.bodySmall)
        },
        leadingContent = {
            Icon(
                if (location.isDefault) Icons.Filled.Star else Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = if (location.isDefault) Color(0xFFFFB300)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
