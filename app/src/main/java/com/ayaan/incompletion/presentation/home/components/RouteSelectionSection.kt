package com.ayaan.incompletion.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.SwapVert
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
import com.ayaan.incompletion.presentation.home.viewmodel.RouteSelectionViewModel
import com.ayaan.incompletion.presentation.routedetails.viewmodel.RouteDetailsViewModel
import com.ayaan.incompletion.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionSection(
    modifier: Modifier = Modifier,
    routeSelectionViewModel: RouteSelectionViewModel = hiltViewModel(),
    routeDetailsViewModel: RouteDetailsViewModel=hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by routeSelectionViewModel.uiState.collectAsState()
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var destinationDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Bus Route",
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Find Common Routes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Source Selection
        Text(
            text = "From (Source)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = sourceDropdownExpanded,
            onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded }
            ) {
            OutlinedTextField(
                value = uiState.selectedSourceName ?: "Select source",
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = sourceDropdownExpanded,
                onDismissRequest = { sourceDropdownExpanded = false },
                modifier= Modifier.height(350.dp)
                    .background(Color.White)
            ) {
                routeSelectionViewModel.busStopOptions.forEach { busStop ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = busStop.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = busStop.id,
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        },
                        onClick = {
                            routeSelectionViewModel.updateSourceSelection(busStop.id)
                            sourceDropdownExpanded = false
                        }
                    )
                    HorizontalDivider(modifier=Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Swap Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { routeSelectionViewModel.swapSourceAndDestination() },
                enabled = !uiState.selectedSourceId.isNullOrEmpty() || !uiState.selectedDestinationId.isNullOrEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap Source and Destination",
                    tint = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destination Selection
        Text(
            text = "To (Destination)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = destinationDropdownExpanded,
            onExpandedChange = { destinationDropdownExpanded = !destinationDropdownExpanded },
        ) {
            OutlinedTextField(
                value = uiState.selectedDestinationName ?: "Select destination",
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF44336),
                    unfocusedBorderColor = Color(0xFFF44336)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = destinationDropdownExpanded,
                onDismissRequest = { destinationDropdownExpanded = false },
                modifier= Modifier.height(350.dp)
                    .background(Color.White)
            ) {
                routeSelectionViewModel.busStopOptions.forEach { busStop ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = busStop.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = busStop.id,
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        },
                        onClick = {
                            routeSelectionViewModel.updateDestinationSelection(busStop.id)
                            destinationDropdownExpanded = false
                        }
                    )
                    HorizontalDivider(modifier=Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Results Section
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Finding common routes...",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            uiState.errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            text = uiState.errorMessage!!,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { routeSelectionViewModel.clearError() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            uiState.routeResponse != null -> {
                CommonRoutesDisplay(
                    routes = uiState.routeResponse!!.commonRoutes,
                    sourceId = uiState.selectedSourceId ?: "",
                    destinationId = uiState.selectedDestinationId ?: "",
                    sourceName = uiState.selectedSourceName ?: "",
                    destinationName = uiState.selectedDestinationName ?: "",
                    routeDetailsViewModel=routeDetailsViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun CommonRoutesDisplay(
    routes: List<String>,
    sourceId: String,
    destinationId: String,
    sourceName: String,
    destinationName: String,
    routeDetailsViewModel: RouteDetailsViewModel=hiltViewModel(),
    navController: androidx.navigation.NavController? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Common Routes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "From $sourceName to $destinationName",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (routes.isEmpty()) {
                Text(
                    text = "No common routes found for this selection.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(routes) { route ->
                        RouteItem(
                            routeNumber = route,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteItem(
    routeNumber: String,
    navController: androidx.navigation.NavController? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navController?.navigate("route_details/$routeNumber")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Bus Route",
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Route $routeNumber",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }
    }
}