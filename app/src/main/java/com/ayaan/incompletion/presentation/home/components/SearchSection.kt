package com.ayaan.incompletion.presentation.home.components

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DirectionsBus
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material.icons.filled.SwapVert
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.ayaan.incompletion.presentation.common.components.GradientButton
//import com.ayaan.incompletion.presentation.common.components.ThemedTextField
//import com.ayaan.incompletion.presentation.home.viewmodel.PlacesViewModel
//import com.ayaan.incompletion.presentation.navigation.Destinations
//import com.google.android.gms.maps.model.LatLng
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import com.ayaan.incompletion.R
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchSection(
//    drawerState: DrawerState,
//    onRouteFetched: (List<LatLng>) -> Unit,
//    onValidityChanged: (Boolean) -> Unit,
//    onLocationsSet: (LatLng?, LatLng?) -> Unit,
//    navController: NavController,
//    placesViewModel: PlacesViewModel = hiltViewModel()
//) {
//    var sourceText by remember { mutableStateOf("") }
//    var destinationText by remember { mutableStateOf("") }
//    var sourcePlaceId by remember { mutableStateOf<String?>(null) }
//    var destinationPlaceId by remember { mutableStateOf<String?>(null) }
//    var activeField by remember { mutableStateOf<String?>(null) }
//
//    val suggestions by placesViewModel.suggestions.collectAsState()
//    val scope = rememberCoroutineScope()
//
//    fun updateButtonState() {
//        onValidityChanged(!sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty())
//    }
//
//    fun fetchSuggestions(query: String, field: String) {
//        activeField = field
//        if (query.length > 2) {
//            scope.launch {
//                delay(300)
//                // Use bus station suggestions for both source and destination
//                placesViewModel.getBusStationSuggestions(query)
//            }
//        } else {
//            placesViewModel.clearSuggestions()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(end = 16.dp, start = 16.dp, bottom = 24.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.DirectionsBus,
//                contentDescription = stringResource(R.string.bus_route),
//                tint = Color(0xFF2196F3),
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(22.dp))
//            Text(
//                text = stringResource(R.string.plan_your_journey),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF333333)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        ThemedTextField(
//            value = sourceText,
//            onValueChange = {
//                sourceText = it
//                sourcePlaceId = null
//                fetchSuggestions(it, "source")
//                updateButtonState()
//                onLocationsSet(null, null)
//            },
//            label = stringResource(R.string.from_source),
//            icon = Icons.Default.LocationOn,
//            focusedBorderColor = Color(0xFF4CAF50), // Green color
//            unfocusedBorderColor = Color(0xFF4CAF50)
//        )
//
//        // Show suggestions for source field
//        if (suggestions.isNotEmpty() && activeField == "source") {
//            SuggestionsDropdown(
//                suggestions = suggestions,
//                onItemClick = { suggestion ->
//                    sourceText = suggestion.primaryText
//                    sourcePlaceId = suggestion.placeId
//                    placesViewModel.clearSuggestions()
//                    activeField = null
//                    updateButtonState()
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Box(
//            modifier = Modifier.fillMaxWidth(),
//            contentAlignment = Alignment.Center
//        ) {
//            IconButton(
//                onClick = {
//                    val temp = sourceText
//                    sourceText = destinationText
//                    destinationText = temp
//
//                    val tempId = sourcePlaceId
//                    sourcePlaceId = destinationPlaceId
//                    destinationPlaceId = tempId
//                    updateButtonState()
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.SwapVert,
//                    contentDescription = stringResource(R.string.swap_source_destination),
//                    tint = Color(0xFF2196F3)
//                )
//            }
//        }
//
//        ThemedTextField(
//            value = destinationText,
//            onValueChange = {
//                destinationText = it
//                destinationPlaceId = null
//                fetchSuggestions(it, "destination")
//                updateButtonState()
//                onLocationsSet(null, null)
//            },
//            label = stringResource(R.string.to_destination),
//            painter = painterResource(R.drawable.destination),
//            focusedBorderColor = Color(0xFFF44336), // Red color
//            unfocusedBorderColor = Color(0xFFF44336)
//        )
//
//        // Show suggestions for destination field
//        if (suggestions.isNotEmpty() && activeField == "destination") {
//            SuggestionsDropdown(
//                suggestions = suggestions,
//                onItemClick = { suggestion ->
//                    destinationText = suggestion.primaryText
//                    destinationPlaceId = suggestion.placeId
//                    placesViewModel.clearSuggestions()
//                    activeField = null
//                    updateButtonState()
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        GradientButton(
//            text = stringResource(R.string.find_route),
//            isLoading = false,
//            enabled = !sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty(),
//            onClick = {
//                scope.launch {
//                    drawerState.close()
//                }
//
//                // Navigate with source and destination data
//                val route = "${Destinations.BookTicket.route}?sourceName=${sourceText}&sourceId=${sourcePlaceId ?: ""}&destName=${destinationText}&destId=${destinationPlaceId ?: ""}"
//                navController.navigate(route) {
//                    popUpTo(Destinations.Home.route)
//                    launchSingleTop = true
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp)
//                .height(48.dp)
//        )
//    }
//}