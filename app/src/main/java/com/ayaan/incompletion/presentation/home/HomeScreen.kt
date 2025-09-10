package com.ayaan.incompletion.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.presentation.home.components.SearchSection
import com.ayaan.incompletion.presentation.navigation.Destinations
import com.ayaan.incompletion.presentation.navigation.NavDrawer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
// Add these additional imports to your HomeScreen file
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var sourceLocation by remember { mutableStateOf<LatLng?>(null) }
    var destinationLocation by remember { mutableStateOf<LatLng?>(null) }
    var isButtonEnabled by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )
        Box(modifier = Modifier.fillMaxSize()) {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 0.dp,
                sheetContainerColor = Color.White,
                sheetContent = {
                    Column( modifier = Modifier.fillMaxWidth().padding(16.dp) ) {
                        SearchSection( drawerState = drawerState,
                            onRouteFetched = { points -> routePoints = points },
                            onValidityChanged = { isValid -> },
                            onLocationsSet = { source, destination ->
                                sourceLocation = source
                                destinationLocation = destination
                            },
                            navController=navController
                        )
                    }
                },
                containerColor = Color.White
            ) { innerPadding ->
                val bangalore = LatLng(12.9716, 77.5946)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(bangalore, 12f)
                }

                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    sourceLocation?.let { source ->
                        Marker(
                            state = MarkerState(position = source),
                            title = "Source",
                            snippet = "Starting point"
                        )
                    }
                    destinationLocation?.let { destination ->
                        Marker(
                            state = MarkerState(position = destination),
                            title = "Destination",
                            snippet = "End point"
                        )
                    }

                    if (routePoints.isNotEmpty()) {
                        Polyline(
                            points = routePoints,
                            color = Color.Blue,
                            width = 8f
                        )
                    }
                }
                LaunchedEffect(routePoints) {
                    if (routePoints.isNotEmpty()) {
                        // 1. Animate camera to show the route
                        val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.builder()
                        routePoints.forEach { boundsBuilder.include(it) }
                        val bounds = boundsBuilder.build()

                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )

                        // 2. Lower the bottom sheet if it's expanded
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                    }
                }
            }

            // Manually place FAB on top of BottomSheetScaffold
            if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                scaffoldState.bottomSheetState.expand()
                            } else {
                                scaffoldState.bottomSheetState.hide()
                            }
                        }
                    },
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Buses"
                    )
                }
            }
        }
    }
suspend fun getPlaceSuggestions(query: String, placesClient: PlacesClient): List<PlaceSuggestion> {
    return try {
        // Assume you have user's location (lat/lng)
        val userLat = 12.9716  // get from FusedLocationProviderClient in real code
        val userLng = 77.5946

        val bounds = RectangularBounds.newInstance(
            LatLng(userLat - 0.40, userLng - 0.40), LatLng(userLat + 0.40, userLng + 0.40)
        )

        val request =
            FindAutocompletePredictionsRequest.builder().setQuery(query).setLocationBias(bounds)
                .build()

        val response = placesClient.findAutocompletePredictions(request)
            .await<com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse>()

        response.autocompletePredictions.map { prediction ->
            PlaceSuggestion(
                placeId = prediction.placeId,
                primaryText = prediction.getPrimaryText(null).toString(),
                secondaryText = prediction.getSecondaryText(null).toString(),
                description = prediction.getFullText(null).toString()
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

suspend fun getLatLngFromPlaceId(placeId: String, placesClient: PlacesClient): LatLng? {
    return try {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        val response = placesClient.fetchPlace(request).await()
        response.place.latLng
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun getRoutePolyline(
    origin: LatLng, destination: LatLng, apiKey: String
): List<LatLng> {
    return try {
        Log.d(
            "getRoutePolyline",
            "Fetching route from ${origin.latitude},${origin.longitude} to ${destination.latitude},${destination.longitude}"
        )

        val client = OkHttpClient()
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?" + "origin=${origin.latitude},${origin.longitude}" + "&destination=${destination.latitude},${destination.longitude}" + "&mode=transit&transit_mode=bus&key=$apiKey"

        Log.d("getRoutePolyline", "Request URL: $url")
        val request = Request.Builder().url(url).build()

        // Use withContext to run on IO dispatcher
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            Log.d("getRoutePolyline", "Response code: ${response.code}")
            Log.d("getRoutePolyline", "Response: $bodyString")

            if (!response.isSuccessful) {
                Log.e("getRoutePolyline", "HTTP error: ${response.code}")
                return@withContext emptyList<LatLng>()
            }

            val json = JSONObject(bodyString)

            // Check for API errors
            if (json.has("error_message")) {
                Log.e("getRoutePolyline", "API error: ${json.getString("error_message")}")
                return@withContext emptyList<LatLng>()
            }

            if (json.getString("status") != "OK") {
                Log.e("getRoutePolyline", "API status: ${json.getString("status")}")
                return@withContext emptyList<LatLng>()
            }

            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.w("getRoutePolyline", "No routes found")
                return@withContext emptyList<LatLng>()
            }

            val points =
                routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points")

            val decodedPoints = decodePolyline(points)
            Log.d("getRoutePolyline", "Successfully decoded ${decodedPoints.size} route points")
            decodedPoints
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("getRoutePolyline", "Error fetching route: ${e.message}")
        emptyList()
    }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latLng = LatLng(lat / 1E5, lng / 1E5)
        poly.add(latLng)
    }

    return poly
}
