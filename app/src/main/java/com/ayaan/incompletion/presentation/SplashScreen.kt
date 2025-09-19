package com.ayaan.incompletion.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ayaan.incompletion.presentation.navigation.Destinations
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController= rememberNavController()) {
    val user= FirebaseAuth.getInstance().currentUser
    LaunchedEffect(Unit) {
        delay(1000)
        if (user==null){
            navController.navigate(Destinations.Login.route){
                popUpTo(Destinations.Splash.route){inclusive=true}
            }
        }else {
            navController.navigate(Destinations.Home.route) {
                popUpTo(Destinations.Splash.route) { inclusive = true }
            }
        }
    }
}