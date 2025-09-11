package com.ayaan.incompletion.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ayaan.incompletion.BuildConfig
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.presentation.common.components.ThemedTextField
import com.ayaan.incompletion.presentation.home.getLatLngFromPlaceId
import com.ayaan.incompletion.presentation.home.getPlaceSuggestions
//import com.ayaan.incompletion.presentation.home.getRoutePolyline
import com.ayaan.incompletion.presentation.navigation.Destinations
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ayaan.incompletion.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    drawerState: DrawerState,
    onRouteFetched: (List<LatLng>) -> Unit,
    onValidityChanged: (Boolean) -> Unit,
    onLocationsSet: (LatLng?, LatLng?) -> Unit,
    navController: NavController
) {
    var sourceText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var sourcePlaceId by remember { mutableStateOf<String?>(null) }
    var destinationPlaceId by remember { mutableStateOf<String?>(null) }
    var suggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var activeField by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val scope = rememberCoroutineScope()

    fun updateButtonState() {
        onValidityChanged(!sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty())
    }

    fun fetchSuggestions(query: String, field: String) {
        activeField = field
        if (query.length > 2) {
            scope.launch {
                delay(300)
                suggestions = getPlaceSuggestions(query, placesClient)
            }
        } else {
            suggestions = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(end = 16.dp, start = 16.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Bus Route",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(22.dp))
            Text(
                text = "Plan Your Journey",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ThemedTextField(
            value = sourceText,
            onValueChange = {
                sourceText = it
                sourcePlaceId = null
                fetchSuggestions(it, "source")
                updateButtonState()
                onLocationsSet(null, null)
            },
            label = "From (Source)",
            icon = Icons.Default.LocationOn,
            focusedBorderColor = Color(0xFF4CAF50), // Green color
            unfocusedBorderColor = Color(0xFF4CAF50)
        )

        // Show suggestions for source field
        if (suggestions.isNotEmpty() && activeField == "source") {
            SuggestionsDropdown(
                suggestions = suggestions,
                onItemClick = { suggestion: PlaceSuggestion ->
                    sourceText = suggestion.primaryText
                    sourcePlaceId = suggestion.placeId
                    suggestions = emptyList()
                    activeField = null
//                    fetchRouteIfReady()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    val temp = sourceText
                    sourceText = destinationText
                    destinationText = temp

                    val tempId = sourcePlaceId
                    sourcePlaceId = destinationPlaceId
                    destinationPlaceId = tempId

//                    fetchRouteIfReady()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap Source and Destination",
                    tint = Color(0xFF2196F3)
                )
            }
        }

        ThemedTextField(
            value = destinationText,
            onValueChange = {
                destinationText = it
                destinationPlaceId = null
                fetchSuggestions(it, "destination")
                updateButtonState()
                onLocationsSet(null, null)
            },
            label = "To (Destination)",
            painter = painterResource(R.drawable.destination),
            focusedBorderColor = Color(0xFFF44336), // Red color
            unfocusedBorderColor = Color(0xFFF44336)
        )

        // Show suggestions for destination field
        if (suggestions.isNotEmpty() && activeField == "destination") {
            SuggestionsDropdown(
                suggestions = suggestions,
                onItemClick = { suggestion: PlaceSuggestion ->
                    destinationText = suggestion.primaryText
                    destinationPlaceId = suggestion.placeId
                    suggestions = emptyList()
                    activeField = null
//                    fetchRouteIfReady()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(
            text = "Find Route",
            isLoading = false,
            enabled = !sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty(),
            onClick = {
                scope.launch {
                    drawerState.close()
//                    fetchRouteIfReady()
                }

                // Navigate with source and destination data
                val route = "${Destinations.BookTicket.route}?sourceName=${sourceText}&sourceId=${sourcePlaceId ?: ""}&destName=${destinationText}&destId=${destinationPlaceId ?: ""}"
                navController.navigate(route) {
                    popUpTo(Destinations.Home.route)
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(48.dp)
        )
    }
}