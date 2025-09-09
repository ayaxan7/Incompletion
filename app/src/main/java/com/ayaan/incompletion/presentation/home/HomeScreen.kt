package com.ayaan.incompletion.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.google.android.libraries.places.api.model.RectangularBounds
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ayaan.incompletion.R
import com.ayaan.incompletion.presentation.auth.components.ThemedTextField
import com.ayaan.incompletion.presentation.navigation.NavDrawer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Data class for place suggestions
data class PlaceSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawer(
                drawerState = drawerState,
                scope = scope,
                navController = navController
            )
        }
    ) {
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
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection() {
    var sourceText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var sourceSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var destinationSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var activeField by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Vehicle Type Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Bus",
                tint = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Public Bus Routes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2)
            )
        }

        ThemedTextField(
            value = sourceText,
            onValueChange = { newText ->
                sourceText = newText
                activeField = "source"

                if (newText.isNotBlank() && newText.length > 2) {
                    scope.launch {
                        delay(300) // debounce
                        sourceSuggestions = getPlaceSuggestions(newText, placesClient)
                    }
                } else {
                    sourceSuggestions = emptyList()
                }
            },
            label = "From (Source)",
            icon = Icons.Default.LocationOn
        )

        if (activeField == "source" && sourceSuggestions.isNotEmpty()) {
            SuggestionsDropdown(
                suggestions = sourceSuggestions,
                onItemClick = {
                    sourceText = it.primaryText
                    sourceSuggestions = emptyList()
                    activeField = null
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Swap Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    val temp = sourceText
                    sourceText = destinationText
                    destinationText = temp
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap",
                    tint = Color.Gray
                )
            }
        }

        ThemedTextField(
            value = destinationText,
            onValueChange = { newText ->
                destinationText = newText
                activeField = "destination"

                if (newText.isNotBlank() && newText.length > 2) {
                    scope.launch {
                        delay(300)
                        destinationSuggestions = getPlaceSuggestions(newText, placesClient)
                    }
                } else {
                    destinationSuggestions = emptyList()
                }
            },
            label = "To (Destination)",
            icon = Icons.Default.LocationOn
        )

        if (activeField == "destination" && destinationSuggestions.isNotEmpty()) {
            SuggestionsDropdown(
                suggestions = destinationSuggestions,
                onItemClick = {
                    destinationText = it.primaryText
                    destinationSuggestions = emptyList()
                    activeField = null
                }
            )
        }
    }
}

@Composable
fun SuggestionsDropdown(
    suggestions: List<PlaceSuggestion>,
    onItemClick: (PlaceSuggestion) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp) // ✅ restrict max height but allow dynamic expansion
        ) {
            items(suggestions) { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onClick = { onItemClick(suggestion) }
                )
            }
        }
    }
}

@Composable
fun SuggestionItem(
    suggestion: PlaceSuggestion,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = suggestion.primaryText,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Text(
            text = suggestion.secondaryText,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
suspend fun getPlaceSuggestions(query: String, placesClient: PlacesClient): List<PlaceSuggestion> {
    return try {
        // Assume you have user's location (lat/lng)
        val userLat = 12.9716  // get from FusedLocationProviderClient in real code
        val userLng = 77.5946

        // Define bounding box around user's city (approx 15 km radius)
        val bounds = RectangularBounds.newInstance(
            LatLng(userLat - 0.15, userLng - 0.15), // southwest corner
            LatLng(userLat + 0.15, userLng + 0.15)  // northeast corner
        )

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setLocationBias(bounds)
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
