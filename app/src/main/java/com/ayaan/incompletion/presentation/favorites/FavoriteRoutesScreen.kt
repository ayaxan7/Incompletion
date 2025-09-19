package com.ayaan.incompletion.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ayaan.incompletion.R
import com.ayaan.incompletion.presentation.common.components.GradientExtendedFloatingActionButton
import com.ayaan.incompletion.presentation.favorites.components.AddFavoriteDialog
import com.ayaan.incompletion.presentation.favorites.components.FavoriteRoutesList
import com.ayaan.incompletion.presentation.home.viewmodel.RouteSelectionViewModel
import com.ayaan.incompletion.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRoutesScreen(
    navController: NavController,
    favoriteRouteViewModel: FavoriteRouteViewModel = hiltViewModel(),
    routeSelectionViewModel: RouteSelectionViewModel = hiltViewModel(),
    language:String="en"
) {
    val favoriteRoutes by favoriteRouteViewModel.favoriteRoutes.collectAsState()
    var showAddFavoriteDialog by remember { mutableStateOf(false) }

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
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.favorite_routes),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 19.sp
                    )
                }
            }
        },
        floatingActionButton = {
            GradientExtendedFloatingActionButton(
                text = stringResource(R.string.add_favourites),
                icon = Icons.Default.Add,
                onClick = { showAddFavoriteDialog = true },
                enabled = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (favoriteRoutes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_favorite_routes),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.add_frequently_used_routes),
                            fontSize = 14.sp,
                            color = Color(0xFF999999),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                FavoriteRoutesList(
                    favoriteRoutes = favoriteRoutes,
                    onDeleteFavorite = { favoriteRoute ->
                        favoriteRouteViewModel.removeFavoriteRoute(favoriteRoute)
                    },
                    navController = navController,
                    routeSelectionViewModel = routeSelectionViewModel
                )
            }
            // Add favorite dialog
            AddFavoriteDialog(
                isVisible = showAddFavoriteDialog,
                onDismiss = { showAddFavoriteDialog = false },
                onAddFavorite = { sourceName, sourceId, destinationName, destinationId ->
                    favoriteRouteViewModel.addFavoriteRoute(
                        sourceName = sourceName,
                        sourceId = sourceId,
                        destinationName = destinationName,
                        destinationId = destinationId
                    )
                }
            )
        }
    }
}
