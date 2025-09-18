package com.ayaan.incompletion.presentation.nearestbusstop

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ayaan.incompletion.ui.theme.PrimaryBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import androidx.core.net.toUri
import com.ayaan.incompletion.data.model.BusStop
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.ayaan.incompletion.R
import android.content.Context
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import androidx.core.graphics.scale
import com.ayaan.incompletion.data.location.LocationService
import com.ayaan.incompletion.presentation.busstop.viewmodel.BusesForStopViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearestBusStopScreen(
    navController: NavController,
    viewModel: NearestBusStopViewModel = hiltViewModel(),
    busesForStopViewModel: BusesForStopViewModel = hiltViewModel(),
    locationService: LocationService
) {
    val uiState by viewModel.uiState.collectAsState()
    val busesForStopUiState by busesForStopViewModel.uiState.collectAsState()
    var userLocation by remember { mutableStateOf(LatLng(12.9098849, 77.5644359)) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // BottomSheet state
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var cameraPositioned by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

    LaunchedEffect(Unit) {
        // Get current location
        try {
            val currentLocation = locationService.getCurrentLocation()
            currentLocation?.let { location ->
                userLocation = LatLng(location.latitude, location.longitude)
                Log.d("NearestBusStopScreen", "Current location updated: ${location.latitude}, ${location.longitude}")
            } ?: run {
                Log.d("NearestBusStopScreen", "Could not get current location, using default location")
            }
        } catch (e: Exception) {
            Log.e("NearestBusStopScreen", "Failed to get current location: ${e.message}")
        }

        // Find nearest bus stops first
        viewModel.findNearestBusStops()
    }

    // Call getBusesForStop when bus stops are loaded and we have the nearest one
    LaunchedEffect(uiState.busStopsResponse) {
        uiState.busStopsResponse?.let { response ->
            if (response.stops.isNotEmpty()) {
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
                    // Extract the stop ID from the nearest bus stop
                    val nearestStopId = stop.stopId // Fallback to S1 if no ID
                    Log.d("NearestBusStopScreen", "Calling getBusesForStop with nearest stop ID: $nearestStopId")
                    busesForStopViewModel.getBusesForStop(nearestStopId)

                    // Camera positioning logic
                    if (!cameraPositioned) {
                        val nearestLocation = LatLng(
                            stop.location.coordinates[1], // latitude
                            stop.location.coordinates[0]  // longitude
                        )
                        delay(300)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(nearestLocation, 17f)
                        cameraPositioned = true
                        delay(100)
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(nearestLocation, 17f),
                            durationMs = 800
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(busesForStopUiState) {
        Log.d("NearestBusStopScreen", "busesForStopUiState changed: isLoading=${busesForStopUiState.isLoading}, buses.size=${busesForStopUiState.buses.size}, errorMessage=${busesForStopUiState.errorMessage}")
        when {
            busesForStopUiState.isLoading -> {
                Log.d("NearestBusStopScreen", "getBusesForStop: Loading...")
            }
            busesForStopUiState.errorMessage != null -> {
                Log.e("NearestBusStopScreen", "getBusesForStop Error: ${busesForStopUiState.errorMessage}")
            }
            busesForStopUiState.buses.isNotEmpty() -> {
                Log.d("NearestBusStopScreen", "getBusesForStop Success: Found ${busesForStopUiState.buses.size} buses")
                busesForStopUiState.buses.forEachIndexed { index, bus ->
                    Log.d("NearestBusStopScreen", "Bus $index: ID=${bus.busId}, Route=${bus.routeNo}, Distance=${bus.distance}m, Duration=${bus.duration}s, Crowd:${bus.crowdDensity}")
                }
            }
            else -> {
                Log.d("NearestBusStopScreen", "getBusesForStop: Success but no buses found (empty list)")
            }
        }
    }

    fun focusOnBusStop(busStop: BusStop) {
        val position = LatLng(
            busStop.location.coordinates[1],
            busStop.location.coordinates[0]
        )

        scope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(position, 17f),
                durationMs = 1000
            )
        }
        showBottomSheet = false
    }

    // Function to open Google Maps with directions
    fun openGoogleMapsDirections(destinationLat: Double, destinationLng: Double) {
        val uri = "https://www.google.com/maps/dir/?api=1&origin=${userLocation.latitude},${userLocation.longitude}&destination=$destinationLat,$destinationLng&travelmode=transit".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        // Check if Google Maps is installed, otherwise open in browser
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to browser if Google Maps app is not installed
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(browserIntent)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Elevated card showing buses approaching nearest stop
            uiState.busStopsResponse?.let { response ->
                if (response.stops.isNotEmpty()) {
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Bus icon
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Buses",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val busCount = busesForStopUiState.buses.size
                                    val busText = if (busCount == 1) "bus is" else "buses are"

                                    Text(
                                        text = "$busCount $busText approaching",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF333333)
                                    )

                                    Text(
                                        text = stop.name,
                                        fontSize = 14.sp,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Medium
                                    )

                                    if (busesForStopUiState.buses.isNotEmpty()) {
                                        val nearestBus = busesForStopUiState.buses.minByOrNull { it.duration }
                                        nearestBus?.let { bus ->
                                            Text(
                                                text = "Route: ${bus.routeNo}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                            Text(
                                                text = "Approaching in: ${formatTime(bus.duration)}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                            Text(
                                                text = "Distance: ${formatDistance(bus.distance)} away",
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                            Text(
                                                text = "Crowd Density: ${bus.crowdDensity}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Map container
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    val busStopIcon = getScaledBitmapDescriptor(context, R.drawable.bus_marker, 120, 120)
                    // Add markers for nearest bus stops
                    uiState.busStopsResponse?.stops?.forEach { busStop ->
                        val position = LatLng(
                            busStop.location.coordinates[1], // latitude
                            busStop.location.coordinates[0]  // longitude
                        )

                        Marker(
                            state = MarkerState(position = position),
                            icon = busStopIcon,
                            title = busStop.name,
                            snippet = "Routes: ${busStop.routes?.joinToString(", ") { it.routeNumber } ?: "No routes available"}",
                            onClick = {
                                openGoogleMapsDirections(
                                    destinationLat = busStop.location.coordinates[1],
                                    destinationLng = busStop.location.coordinates[0]
                                )
                                true // Return true to indicate we have handled the click
                            },
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

                // FAB to show bus stops list
                uiState.busStopsResponse?.let { response ->
                    if (response.stops.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Show bus stops list"
                            )
                        }
                    }
                }
            }
        }

        // BottomSheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                uiState.busStopsResponse?.let { response ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Found ${response.stops.size} nearby bus stop(s)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(response.stops) { busStop ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            focusOnBusStop(busStop)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F5F5)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = busStop.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )

                                        if (!busStop.routes.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Routes: ${busStop.routes.joinToString(", ") { it.routeNumber }}",
                                                fontSize = 14.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        val distance = calculateDistance(
                                            userLocation.latitude,
                                            userLocation.longitude,
                                            busStop.location.coordinates[1],
                                            busStop.location.coordinates[0]
                                        )
//                                        Text(
//                                            text = "Distance: ${formatDistanceFromKm(distance)}",
//                                            fontSize = 12.sp,
//                                            color = PrimaryBlue,
//                                            fontWeight = FontWeight.Medium
//                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tap on any bus stop to focus the map on it",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getScaledBitmapDescriptor(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val scaledBitmap = bitmap.scale(width, height, false)
    return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}

// Utility function to format distance from meters
fun formatDistance(distanceInMeters: Int): String {
    return if (distanceInMeters >= 1000) {
        val distanceInKm = distanceInMeters / 1000.0
        String.format("%.1fkm", distanceInKm)
    } else {
        "${distanceInMeters}m"
    }
}

// Utility function to format distance from kilometers (for the bottom sheet)
fun formatDistanceFromKm(distanceInKm: Double): String {
    return if (distanceInKm >= 1.0) {
        String.format("%.1fkm", distanceInKm)
    } else {
        val distanceInMeters = (distanceInKm * 1000).toInt()
        "${distanceInMeters}m"
    }
}

// Utility function to format time from seconds
fun formatTime(timeInSeconds: Int): String {
    return if (timeInSeconds >= 60) {
        val minutes = timeInSeconds / 60
        val remainingSeconds = timeInSeconds % 60
        if (remainingSeconds > 0) {
            "${minutes}m ${remainingSeconds}s"
        } else {
            "${minutes}m"
        }
    } else {
        "${timeInSeconds}s"
    }
}

// Utility function to calculate distance between two points using Haversine formula
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c // Distance in km
}