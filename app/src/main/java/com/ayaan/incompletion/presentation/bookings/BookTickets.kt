package com.ayaan.incompletion.presentation.bookings

//import com.ayaan.incompletion.presentation.bookings.components.RouteResultsList
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.runtime.Composable
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.ayaan.incompletion.presentation.bookings.viewmodel.RouteFinderViewModel
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BookTickets(
//    navController: NavController,
//    sourceName: String = "",
//    sourceId: String = "",
//    destinationName: String = "",
//    destinationId: String = "",
//    viewModel: RouteFinderViewModel = hiltViewModel()
//) {
//
//}
//) {
//    Log.d("BookTickets", "Source: $sourceName ($sourceId), Destination: $destinationName ($destinationId)")
//
//    var sourceStopId by remember { mutableStateOf(sourceId) }
//    var destinationStopId by remember { mutableStateOf(destinationId) }
//    var sourceName by remember { mutableStateOf(sourceName) }
//    var destinationName by remember { mutableStateOf(destinationName) }
//    val uiState by viewModel.uiState.collectAsState()
//    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
//
//    // Auto-search if both IDs are provided
//    LaunchedEffect(sourceId, destinationId) {
//        if (sourceId.isNotEmpty() && destinationId.isNotEmpty()) {
//            viewModel.findRoutes(sourceId, destinationId)
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .background(PrimaryBlue)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(
//                        onClick = { navController.navigateUp() },
//                        modifier = Modifier.size(36.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Route Finder",
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White,
//                        fontSize = 19.sp
//                    )
//                }
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Color(0xFFF5F5F5))
//        ) {
//            // Search Section
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Find Bus Routes",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF333333)
//                    )
//
//                    if (sourceName.isNotEmpty() && destinationName.isNotEmpty()) {
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "From: $sourceName",
//                            fontSize = 14.sp,
//                            color = Color(0xFF666666)
//                        )
//                        Text(
//                            text = "To: $destinationName",
//                            fontSize = 14.sp,
//                            color = Color(0xFF666666)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    ThemedTextField(
//                        value = sourceName,
//                        onValueChange = { }, // Empty lambda since it's read-only
//                        label = "From",
//                        readOnly = true // Prevents keyboard and cursor
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    ThemedTextField(
//                        value = destinationName,
//                        onValueChange = { }, // Empty lambda since it's read-only
//                        label = "To",
//                        readOnly = true // Prevents keyboard and cursor
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    GradientButton(
//                        text = "Find Routes",
//                        icon = Icons.Default.Search,
//                        isLoading = uiState.isLoading,
//                        enabled = sourceStopId.isNotBlank() && destinationStopId.isNotBlank() && !uiState.isLoading,
//                        onClick = {
//                            viewModel.clearError()
//                            viewModel.findRoutes(sourceStopId.trim(), destinationStopId.trim())
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(48.dp)
//                    )
//                }
//            }
//
//            // Results Section
//            SwipeRefresh(
//                state = swipeRefreshState,
//                onRefresh = {
//                    if (sourceStopId.isNotBlank() && destinationStopId.isNotBlank()) {
//                        viewModel.refreshRoutes(sourceStopId.trim(), destinationStopId.trim())
//                    }
//                },
//                modifier = Modifier.fillMaxSize()
//            ) {
//                when {
//                    uiState.isLoading && uiState.routeResponse == null -> {
//                        LoadingDisplay()
//                    }
//
//                    uiState.errorMessage != null -> {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .verticalScroll(rememberScrollState())
//                        ) {
//                            ErrorDisplay(
//                                errorMessage = uiState.errorMessage!!,
//                                onRetry = {
//                                    if (sourceStopId.isNotBlank() && destinationStopId.isNotBlank()) {
//                                        viewModel.clearError()
//                                        viewModel.findRoutes(sourceStopId.trim(), destinationStopId.trim())
//                                    }
//                                }
//                            )
//                        }
//                    }
//
//                    uiState.routeResponse != null -> {
//                        RouteResultsList(
//                            routeResponse = uiState.routeResponse!!,
//                            onRefresh = {
//                                if (sourceStopId.isNotBlank() && destinationStopId.isNotBlank()) {
//                                    viewModel.refreshRoutes(sourceStopId.trim(), destinationStopId.trim())
//                                }
//                            },
//                            isRefreshing = uiState.isRefreshing
//                        )
//                    }
//
//                    else -> {
//                        // Initial state - show helpful message
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(32.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = "Enter Stop IDs to Find Routes",
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    color = Color(0xFF666666)
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    text = "Find direct bus routes between two stops",
//                                    fontSize = 14.sp,
//                                    color = Color(0xFF999999)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}