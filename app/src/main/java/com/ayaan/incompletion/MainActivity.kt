package com.ayaan.incompletion

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ayaan.incompletion.presentation.navigation.Navigator
import com.ayaan.incompletion.ui.theme.IncompletionTheme
import com.google.android.libraries.places.api.Places
import java.util.Locale
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        try {
            Places.initialize(applicationContext, BuildConfig.GMAPS_API_KEY)
            Log.d("MainActivity", "Places initialized: ${Places.isInitialized()}")
        } catch (e: Exception) {
            Log.e("MainActivity", "Places initialization failed", e)
        }
        setContent {
            IncompletionTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(innerPadding)
                }
            }
        }
    }
}