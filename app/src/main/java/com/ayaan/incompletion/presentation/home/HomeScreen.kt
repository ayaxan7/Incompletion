package com.ayaan.incompletion.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ayaan.incompletion.presentation.home.components.ActionButtonsSection
import com.ayaan.incompletion.presentation.common.components.GradientExtendedFloatingActionButton
import com.ayaan.incompletion.presentation.home.components.AddFavoriteDialog
import com.ayaan.incompletion.presentation.home.components.FavoriteRoutesList
import com.ayaan.incompletion.presentation.navigation.Destinations
import com.ayaan.incompletion.presentation.home.components.RouteSelectionSection
import com.ayaan.incompletion.presentation.favorites.FavoriteRouteViewModel
import com.ayaan.incompletion.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    favoriteRouteViewModel: FavoriteRouteViewModel = hiltViewModel()
) {
    var showAddFavoriteDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val favoriteRoutes by favoriteRouteViewModel.favoriteRoutes.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(PrimaryBlue)
                ) {
                    Text(
                        text = "Hi, Ayaan",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Replace SearchSection with RouteSelectionSection
                RouteSelectionSection(navController = navController)

                // Action buttons section
                ActionButtonsSection(
                    onFavoriteRoutesClick = {
                        navController.navigate(Destinations.FavoriteRoutes.route)
                    },
                    onNearestBusStopClick = {
                        navController.navigate(Destinations.NearestBusStop.route)
                    }
                )
            }
        }
    }
}
