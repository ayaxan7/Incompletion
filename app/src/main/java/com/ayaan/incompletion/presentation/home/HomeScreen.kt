package com.ayaan.incompletion.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayaan.incompletion.R
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.presentation.home.components.SearchSection
import com.ayaan.incompletion.presentation.navigation.NavDrawer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Data class for place suggestions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            NavDrawer(
                drawerState = drawerState, scope = scope, navController = navController
            )
        }) {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.Menu, contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }) { paddingValues ->
            val bangalore = LatLng(12.9716, 77.5946)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(bangalore, 12f)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                ) {
                    Marker(
                        state = MarkerState(position = bangalore),
                        title = "Bangalore",
                        snippet = "Test Marker"
                    )
                }

                // Search Section
                SearchSection()
                Spacer(modifier = Modifier.height(16.dp))
                GradientButton(
                    text = "Book Ticket", isLoading = false, enabled = true, onClick = {
                        // Handle search action
                    })
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
