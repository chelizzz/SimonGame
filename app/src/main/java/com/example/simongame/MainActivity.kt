package com.example.simongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simongame.ui.theme.SimonGameTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The app takes the entire display size, from the top to the bottom edge of the display
        enableEdgeToEdge()

        // Set and display the UI content
        setContent {
            SimonGameTheme {
                // Reference: https://developer.android.com/develop/ui/compose/navigation
                val navController = rememberNavController()

                // The scaffold fills the whole display area
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "start",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Defines the destination "start" in the navigation graph
                        composable("start") {
                            StartScreen(
                                // When the button in StartScreen is clicked, onEndGameClicked is triggered with a parameter s (e.g. "R, G, B, M, Y, C")
                                onEndGameClicked = { s ->
                                    // The string route "history/${s}" indicates the app to navigate to the history screen, appending the value of the variable s (string interpolation)
                                    navController.navigate("history/${s}")
                                }
                            )
                        }

                        // Defines the destination "history" in the navigation graph, where {sequence} acts like a variable in the URL
                        composable("history/{sequence}") { backStackEntry ->
                            // backStackEntry.arguments: holds information about the current destination, including the argument (s) passed through the navigation route
                            // ?.getString("sequence"): ? (safe call operator) since arguments could be null, it ensures the app doesn't crash, and the expression returns null
                            // orEmpty(): Kotlin helper function for Strings, if getString returns null, it returns an empty string
                            HistoryScreen(
                                sequence = backStackEntry.arguments?.getString("sequence").orEmpty(),
                                onBackClicked = { navController.navigate("start") }
                            )
                        }
                    }
                }
            }
        }
    }
}