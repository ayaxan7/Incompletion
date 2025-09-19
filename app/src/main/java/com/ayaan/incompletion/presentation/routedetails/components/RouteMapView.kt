package com.ayaan.incompletion.presentation.routedetails.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ayaan.incompletion.data.model.getRoute.Routes
import com.ayaan.incompletion.data.directions.DirectionsService
import com.ayaan.incompletion.ui.theme.GradientLightBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import androidx.core.graphics.scale

@Composable
fun RouteMapView(
    routes: List<Routes>,
    modifier: Modifier = Modifier,
    directionsService: DirectionsService = DirectionsService()
) {
    val route = routes.firstOrNull()

    if (route == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No route data available",
                color = Color(0xFF666666)
            )
        }
        return
    }
    val busStops = remember(route) {
        route.stops.map { stop ->
            LatLng(
                stop.location.coordinates[1], // latitude
                stop.location.coordinates[0]  // longitude
            )
        }
    }

    // State for decoded route points
    var decodedRoutePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isLoadingRoute by remember { mutableStateOf(false) }

    LaunchedEffect(route) {
        if (busStops.size >= 2) {
            isLoadingRoute = true
            try {
                val routePoints = directionsService.getBusRouteDirections(busStops)
                decodedRoutePoints = routePoints
            } catch (e: Exception) {
                e.printStackTrace()
                decodedRoutePoints = busStops
            } finally {
                isLoadingRoute = false
            }
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
    val bounds = remember(busStops) {
        if (busStops.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            busStops.forEach { builder.include(it) }
            builder.build()
        } else null
    }

    val cameraPositionState = rememberCameraPositionState()

    // Set camera to show all route points
    LaunchedEffect(bounds) {
        bounds?.let {
            delay(100)
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(it, 100),
                    1000
                )
            } catch (e: Exception) {
                // Fallback to center of route
                if (busStops.isNotEmpty()) {
                    val center = busStops[busStops.size / 2]
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(center, 12f)
                }
            }
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
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
            if (decodedRoutePoints.size > 1) {
                Polyline(
                    points = decodedRoutePoints,
                    color = Color.Red,
                    width = 14f,
                    pattern = null,
                    geodesic = false // Set to false since we have detailed route points
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

        // Loading indicator for route generation
        if (isLoadingRoute) {
            Card(
                modifier = Modifier
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = GradientLightBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Generating route...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF333333)
                    )
                }
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

//                if (decodedRoutePoints.isNotEmpty()) {
//                    Text(
//                        text = "${decodedRoutePoints.size} route points",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color(0xFF888888)
//                    )
//                }

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
}
fun getScaledBitmapDescriptor(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val scaledBitmap = bitmap.scale(width, height, false)
    return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}
data class MarkerData(
    val position: LatLng,
    val title: String,
    val snippet: String,
    val isStartOrEnd: Boolean = false
)
