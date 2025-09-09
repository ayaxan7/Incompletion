package com.ayaan.incompletion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ayaan.incompletion.presentation.navigation.Navigator
import com.ayaan.incompletion.ui.theme.IncompletionTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IncompletionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(innerPadding)
                }
            }
        }
    }
}
@Composable
fun HomeScreen(navController: NavController){
    val bangalore = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bangalore, 12f)
    }
    GoogleMap(cameraPositionState = cameraPositionState) {
        Marker(
            state = MarkerState(position = bangalore),
            title = "Bangalore",
            snippet = "Test Marker"
        )
    }
}