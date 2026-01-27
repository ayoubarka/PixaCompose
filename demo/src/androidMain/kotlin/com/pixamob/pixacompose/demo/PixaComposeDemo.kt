package com.pixamob.pixacompose.demo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixamob.pixacompose.demo.screens.*

/**
 * Main demo application composable
 * Category-based navigation for elegant component showcase
 */
@Composable
fun PixaComposeDemo() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToComponent = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Category Screens
        composable("actions") { ActionsDemoScreen { navController.popBackStack() } }
        composable("inputs") { InputsDemoScreen { navController.popBackStack() } }
        composable("display") { DisplayDemoScreen { navController.popBackStack() } }
        composable("feedback") { FeedbackDemoScreen { navController.popBackStack() } }
        composable("navigation") { NavigationDemoScreen { navController.popBackStack() } }
        composable("overlay") { OverlayDemoScreen { navController.popBackStack() } }
    }
}


