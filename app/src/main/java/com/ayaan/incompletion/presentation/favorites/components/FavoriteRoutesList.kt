package com.ayaan.incompletion.presentation.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ayaan.incompletion.data.local.entity.FavoriteRoute
import com.ayaan.incompletion.presentation.home.viewmodel.RouteSelectionViewModel
import com.ayaan.incompletion.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRoutesList(
    favoriteRoutes: List<FavoriteRoute>,
    onDeleteFavorite: (FavoriteRoute) -> Unit,
    navController: NavController,
    routeSelectionViewModel: RouteSelectionViewModel = hiltViewModel()
) {
    val uiState by routeSelectionViewModel.uiState.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedRoute by remember { mutableStateOf<FavoriteRoute?>(null) }

    if (favoriteRoutes.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Favorite Routes",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your Favorite Routes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteRoutes) { favoriteRoute ->
                    FavoriteRouteItem(
                        favoriteRoute = favoriteRoute,
                        onDeleteClick = { onDeleteFavorite(favoriteRoute) },
                        onItemClick = {
                            selectedRoute = favoriteRoute
                            // Set the source and destination in the ViewModel
                            routeSelectionViewModel.updateSourceSelection(favoriteRoute.sourceId)
                            routeSelectionViewModel.updateDestinationSelection(favoriteRoute.destinationId)
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    // BottomSheet for displaying common routes
    if (showBottomSheet && selectedRoute != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                routeSelectionViewModel.clearSelections()
            },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight(),
            containerColor= Color.White
        ) {
            CommonRoutesBottomSheet(
                uiState = uiState,
                selectedRoute = selectedRoute!!,
                onDismiss = {
                    showBottomSheet = false
                    routeSelectionViewModel.clearSelections()
                },
                onRetry = {
                    routeSelectionViewModel.updateSourceSelection(selectedRoute!!.sourceId)
                    routeSelectionViewModel.updateDestinationSelection(selectedRoute!!.destinationId)
                },
                navController = navController
            )
        }
    }
}

@Composable
private fun FavoriteRouteItem(
    favoriteRoute: FavoriteRoute,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Route info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Source
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "From",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = favoriteRoute.sourceName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Destination
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.destination),
                        contentDescription = "To",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = favoriteRoute.destinationName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Favorite",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
