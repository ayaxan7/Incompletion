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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ayaan.incompletion.presentation.navigation.Navigator
import com.ayaan.incompletion.ui.theme.IncompletionTheme
import com.ayaan.incompletion.data.location.LocationService
import com.ayaan.incompletion.presentation.settings.SettingsViewModel
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var locationService: LocationService

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
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val selectedLanguageCode by settingsViewModel.selectedLanguageCode.collectAsState()
//            LaunchedEffect(selectedLanguageCode) {
//                while (true) {
//                    Log.d("MainActivity", "Selected language code: $selectedLanguageCode")
//                    delay(5000)
//                }
//            }
            val locale = Locale("hi") // Hindi
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            IncompletionTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(innerPadding, locationService,selectedLanguageCode)
                }
            }
        }
    }
}