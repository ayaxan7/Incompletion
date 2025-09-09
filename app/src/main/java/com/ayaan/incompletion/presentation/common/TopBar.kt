package com.ayaan.incompletion.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// TopBar component for Scaffold
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopBar(navController: NavController): @Composable () -> Unit {
//    return {
//        TopAppBar(
//            title = { Text("Your App Name") },
//            navigationIcon = {
//                IconButton(
//                    onClick = {
//                        // You can handle menu click here
//                        // For now, we'll just navigate or show a snackbar
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Menu,
//                        contentDescription = "Menu"
//                    )
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = Color.Transparent
//            )
//        )
//    }
//}
//
//// Wrapper component that handles the drawer functionality
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreenWithDrawer(
//    navController: NavController,
//    content: @Composable () -> Unit
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            ModalDrawerSheet {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = "Navigation Menu",
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//                        label = { Text("Home") },
//                        selected = false,
//                        onClick = {
//                            scope.launch {
//                                drawerState.close()
//                            }
//                            navController.navigate("home")
//                        }
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
//                        label = { Text("Profile") },
//                        selected = false,
//                        onClick = {
//                            scope.launch {
//                                drawerState.close()
//                            }
//                            navController.navigate("profile")
//                        }
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    NavigationDrawerItem(
//                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
//                        label = { Text("Settings") },
//                        selected = false,
//                        onClick = {
//                            scope.launch {
//                                drawerState.close()
//                            }
//                            navController.navigate("settings")
//                        }
//                    )
//                }
//            }
//        }
//    ) {
//        content()
//    }
//}