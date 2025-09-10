package com.ayaan.incompletion.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawer(drawerState: DrawerState, scope: CoroutineScope, navController: NavController) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Navigation Menu", modifier = Modifier.padding(bottom = 16.dp)
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                })

            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.DirectionsBus, contentDescription = "Book Ticket") },
                label = { Text("Book Ticket") },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
//                    navController.navigate("profile")
                })

            Spacer(modifier = Modifier.height(8.dp))

//            NavigationDrawerItem(
//                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
//                label = { Text("Settings") },
//                selected = false,
//                onClick = {
//                    scope.launch {
//                        drawerState.close()
//                    }
////                    navController.navigate("settings")
//                }
//            )
        }
    }
}