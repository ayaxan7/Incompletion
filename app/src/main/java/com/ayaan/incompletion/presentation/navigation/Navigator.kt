package com.ayaan.incompletion.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ayaan.incompletion.presentation.auth.signin.SignInScreen
import com.ayaan.incompletion.presentation.auth.signup.SignUpScreen
import com.ayaan.incompletion.presentation.bookings.BookTickets
import com.ayaan.incompletion.presentation.home.HomeScreen
import com.ayaan.incompletion.presentation.favorites.FavoriteRoutesScreen
import com.ayaan.incompletion.presentation.nearestbusstop.NearestBusStopScreen
import com.ayaan.incompletion.presentation.common.components.test.TestResult
import com.ayaan.incompletion.presentation.common.components.test.TestViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigator(innerPadding: PaddingValues) {
    val testViewModel: TestViewModel = hiltViewModel()
    val testResult by testViewModel.testResult.collectAsState()

    LaunchedEffect(Unit) {
        testViewModel.executeTest()
    }

    LaunchedEffect(testResult) {
        testResult?.let { result ->
            when (result) {
                is TestResult.Loading -> {
                    Log.d("Navigator", "Test API call loading...")
                }
                is TestResult.Success -> {
                    Log.d("Navigator", "Test API call successful: ${result.data}")
                }
                is TestResult.Error -> {
                    Log.e("Navigator", "Test API call failed: ${result.message}")
                }
            }
        }
    }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val start = if (user != null) Destinations.Home.route else Destinations.Login.route
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavHost(
        navController = navController,
        startDestination = start,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = Destinations.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Destinations.Login.route) {
            SignInScreen(
                onSignInClick = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Destinations.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(Destinations.ForgotPassword.route) })
        }
        composable(
            route = "${Destinations.BookTicket.route}?sourceName={sourceName}&sourceId={sourceId}&destName={destName}&destId={destId}",
            arguments = listOf(
                navArgument("sourceName") { type = NavType.StringType; defaultValue = "" },
                navArgument("sourceId") { type = NavType.StringType; defaultValue = "" },
                navArgument("destName") { type = NavType.StringType; defaultValue = "" },
                navArgument("destId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val sourceName = backStackEntry.arguments?.getString("sourceName") ?: ""
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
            val destName = backStackEntry.arguments?.getString("destName") ?: ""
            val destId = backStackEntry.arguments?.getString("destId") ?: ""

            BookTickets(
                navController = navController,
                sourceName = sourceName,
                sourceId = sourceId,
                destinationName = destName,
                destinationId = destId
            )
        }
        composable(route = Destinations.SignUp.route) {
            SignUpScreen(onSignUpClick = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.Login.route) { inclusive = true }
                }
            }, onSignInClick = { navController.navigate(Destinations.Login.route) })
        }
        composable(route = Destinations.FavoriteRoutes.route) {
            FavoriteRoutesScreen(navController)
        }
        composable(route = Destinations.NearestBusStop.route) {
            NearestBusStopScreen(navController)
        }
    }
}

sealed class Destinations(val route: String) {
    data object Login : Destinations("login")
    data object Home : Destinations("home")
    data object BookTicket : Destinations("bookticket")
    data object SignUp : Destinations("signup")
    data object ForgotPassword : Destinations("forgotpassword")
    data object FavoriteRoutes : Destinations("favorite_routes")
    data object NearestBusStop : Destinations("nearest_bus_stop")
}