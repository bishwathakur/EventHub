package com.example.eventhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventhub.ui.screens.auth.SignInScreen
import com.example.eventhub.ui.screens.auth.SignUpScreen
import com.example.eventhub.ui.screens.auth.AddProfileScreen
import com.example.eventhub.ui.screens.main.MainScreen
import com.example.eventhub.ui.screens.event.CreateEventScreen
import com.example.eventhub.ui.screens.event.EventDetailsScreen

@Composable
fun EventHubNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SignIn.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToAddProfile = {
                    navController.navigate(Screen.AddProfile.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onNavigateToAddProfile = {
                    navController.navigate(Screen.AddProfile.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.AddProfile.route) {
            AddProfileScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.AddProfile.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateToEventDetails = { eventKey ->
                    navController.navigate("${Screen.EventDetails.route}/$eventKey")
                }
            )
        }
        
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("${Screen.EventDetails.route}/{eventKey}") { backStackEntry ->
            val eventKey = backStackEntry.arguments?.getString("eventKey") ?: ""
            EventDetailsScreen(
                eventKey = eventKey,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object AddProfile : Screen("add_profile")
    object Main : Screen("main")
    object CreateEvent : Screen("create_event")
    object EventDetails : Screen("event_details")
}