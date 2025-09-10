package com.ayaan.incompletion.presentation.bookings

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BookTickets(
    navController: NavController,
    sourceName: String = "",
    sourceId: String = "",
    destinationName: String = "",
    destinationId: String = ""
) {
    Log.d("BookTickets", "Source: $sourceName ($sourceId), Destination: $destinationName ($destinationId)")

}