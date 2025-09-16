package com.ayaan.incompletion.presentation.routedetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ayaan.incompletion.data.model.getRoute.Routes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@Composable
fun RouteMapView(
    routes: List<Routes>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Get the first route (assuming we're showing one route at a time)
    val route = routes.firstOrNull()

    if (route == null) {
        Box(
            modifier = modifier,
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "No route data available",
                color = Color(0xFF666666)
            )
        }
        return
    }

    // Convert stops to LatLng points
    val routePoints = remember(route) {
        route.stops.map { stop ->
            LatLng(
                stop.location.coordinates[1], // latitude
                stop.location.coordinates[0]  // longitude
            )
        }
    }

    // Create markers for bus stops
    val markers = remember(route) {
        route.stops.mapIndexed { index, stop ->
            MarkerData(
                position = LatLng(
                    stop.location.coordinates[1], // latitude
                    stop.location.coordinates[0]  // longitude
                ),
                title = stop.name,
                snippet = "Stop ${stop.stopId} (Index: ${stop.index})",
                isStartOrEnd = index == 0 || index == route.stops.size - 1
            )
        }
    }

    // Calculate bounds for the camera
    val bounds = remember(routePoints) {
        if (routePoints.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            routePoints.forEach { builder.include(it) }
            builder.build()
        } else null
    }

    var cameraPositionState = rememberCameraPositionState()

    // Set camera to show all route points
    LaunchedEffect(bounds) {
        bounds?.let {
            delay(100) // Small delay to ensure map is ready
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(it, 100),
                    1000
                )
            } catch (e: Exception) {
                // Fallback to center of route
                if (routePoints.isNotEmpty()) {
                    val center = routePoints[routePoints.size / 2]
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(center, 12f)
                }
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.NORMAL,
            isTrafficEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            mapToolbarEnabled = true
        )
    ) {
        // Add polyline for the route
        if (routePoints.size > 1) {
            Polyline(
                points = routePoints,
                color = Color(0xFF2196F3), // Blue color for route
                width = 8f,
                pattern = null,
                geodesic = true
            )
        }

        // Add markers for bus stops
        markers.forEach { markerData ->
            Marker(
                state = MarkerState(position = markerData.position),
                title = markerData.title,
                snippet = markerData.snippet,
                icon = if (markerData.isStartOrEnd) {
                    BitmapDescriptorFactory.defaultMarker(
                        if (markers.indexOf(markerData) == 0)
                            BitmapDescriptorFactory.HUE_GREEN
                        else
                            BitmapDescriptorFactory.HUE_RED
                    )
                } else {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                }
            )
        }
    }

    // Route info overlay
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Route ${route.routeNumber}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Type: ${route.routeType}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
            Text(
                text = "${route.stops.size} stops",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "From: ${route.stops.first().name}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50)
            )
            Text(
                text = "To: ${route.stops.last().name}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFF44336)
            )
        }
    }
}

data class MarkerData(
    val position: LatLng,
    val title: String,
    val snippet: String,
    val isStartOrEnd: Boolean = false
)
