package com.ayaan.incompletion.presentation.bookings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayaan.incompletion.data.model.RouteInfo
import com.ayaan.incompletion.ui.theme.PrimaryBlue

@Composable
fun RouteCard(
    route: RouteInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Route Number Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Bus Route",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Route ${route.routeNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.weight(1f))
                // Direction indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = if (route.direction == "forward")
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (route.direction == "forward")
                            Icons.AutoMirrored.Filled.TrendingUp
                        else
                            Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = "Direction",
                        tint = if (route.direction == "forward")
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = route.direction.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (route.direction == "forward")
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stop indices information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Start Stop",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Index: ${route.startStopIndex}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "End Stop",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Index: ${route.destinationStopIndex}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Number of stops
            val numberOfStops = kotlin.math.abs(route.destinationStopIndex - route.startStopIndex)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total stops: $numberOfStops",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            // Optional: Show estimated time and distance if available
            route.estimatedTime?.let { time ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Est. time: $time",
                    fontSize = 12.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            route.distance?.let { distance ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Distance: $distance",
                    fontSize = 12.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
