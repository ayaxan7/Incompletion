package com.ayaan.incompletion.presentation.nearestbusstop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ayaan.incompletion.presentation.home.viewmodel.NearestBusStopViewModel
import com.ayaan.incompletion.ui.theme.PrimaryBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlin.math.*

// Utility function to calculate distance between two points using Haversine formula
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371 // Radius of the earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c // Distance in km
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearestBusStopScreen(
    navController: NavController,
    viewModel: NearestBusStopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userLocation = LatLng(12.9098849, 77.5644359) // User's current location (matching repository)

    // State to track if camera has been positioned
    var cameraPositioned by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

    // Automatically fetch nearest bus stops when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.findNearestBusStops()
    }

    // Move camera to nearest bus stop when bus stops are loaded
    LaunchedEffect(uiState.busStopsResponse) {
        uiState.busStopsResponse?.let { response ->
            if (response.stops.isNotEmpty() && !cameraPositioned) {
                // Find the nearest bus stop
                val nearestBusStop = response.stops.minByOrNull { busStop ->
                    calculateDistance(
                        userLocation.latitude,
                        userLocation.longitude,
                        busStop.location.coordinates[1], // latitude
                        busStop.location.coordinates[0]  // longitude
                    )
                }

                nearestBusStop?.let { stop ->
                    val nearestLocation = LatLng(
                        stop.location.coordinates[1], // latitude
                        stop.location.coordinates[0]  // longitude
                    )

                    // Add a small delay to ensure GoogleMap is fully initialized
                    delay(300)

                    // Immediately position camera to nearest bus stop with appropriate zoom
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(nearestLocation, 17f)

                    // Mark camera as positioned to avoid repositioning
                    cameraPositioned = true

                    // Optional: Add a subtle animation after initial positioning
                    delay(100)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(nearestLocation, 17f),
                        durationMs = 800
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(PrimaryBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nearest Bus Stops",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 19.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Add markers for nearest bus stops
                uiState.busStopsResponse?.stops?.forEach { busStop ->
                    val position = LatLng(
                        busStop.location.coordinates[1], // latitude
                        busStop.location.coordinates[0]  // longitude
                    )

                    Marker(
                        state = MarkerState(position = position),
                        title = busStop.name,
                        snippet = "Routes: ${busStop.routes?.joinToString(", ") { it.routeNumber } ?: "No routes available"}"
                    )
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Finding nearest bus stops...",
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = error,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.findNearestBusStops()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            // Bus stops info card
            uiState.busStopsResponse?.let { response ->
                if (response.stops.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Found ${response.stops.size} nearby bus stop(s)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap on markers to see bus routes",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }
        }
    }
}
