package com.pixamob.pixacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pixamob.pixacompose.demo.ComponentDetailScreen
import com.pixamob.pixacompose.demo.ComponentEntry
import com.pixamob.pixacompose.demo.LocalThemeToggle
import com.pixamob.pixacompose.demo.MainScreen
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.PixaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            PixaTheme(useDarkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalThemeToggle provides { isDarkTheme = !isDarkTheme }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppTheme.colors.baseSurfaceDefault)
                    ) {
                        PixaComposeNavHost()
                    }
                }
            }
        }
    }
}

@Composable
private fun PixaComposeNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onComponentClick = { entry: ComponentEntry ->
                    navController.navigate("component/${entry.name}")
                }
            )
        }
        composable(
            route = "component/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ComponentDetailScreen(
                componentName = name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
