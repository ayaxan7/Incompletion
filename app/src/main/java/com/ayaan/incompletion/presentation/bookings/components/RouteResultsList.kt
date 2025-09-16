package com.ayaan.incompletion.presentation.bookings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayaan.incompletion.data.model.RouteResponse
import com.ayaan.incompletion.ui.theme.PrimaryBlue

//@Composable
//fun RouteResultsList(
//    routeResponse: RouteResponse,
//    onRefresh: () -> Unit,
//    isRefreshing: Boolean,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier.fillMaxSize()
//    ) {
//        // Header with route summary
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = PrimaryBlue.copy(alpha = 0.1f)
//            ),
//            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Route,
//                        contentDescription = "Routes Found",
//                        tint = PrimaryBlue,
//                        modifier = Modifier.size(24.dp)
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Text(
//                        text = "Routes Found",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF333333)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "From: ${routeResponse.startStop.name}",
//                    fontSize = 14.sp,
//                    color = Color(0xFF666666)
//                )
//                Text(
//                    text = "To: ${routeResponse.destinationStop.name}",
//                    fontSize = 14.sp,
//                    color = Color(0xFF666666)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "${routeResponse.totalRoutes} route(s) available",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = PrimaryBlue
//                )
//            }
//        }
//
//        // Routes list
//        if (routeResponse.commonRoutes.isNotEmpty()) {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(routeResponse.commonRoutes) { route ->
//                    RouteCard(route = route)
//                }
//            }
//        } else {
//            // No routes found
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(32.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "No direct routes found",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Color(0xFF666666)
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Try different stops or check for connecting routes",
//                        fontSize = 14.sp,
//                        color = Color(0xFF999999)
//                    )
//                }
//            }
//        }
//    }
//}
