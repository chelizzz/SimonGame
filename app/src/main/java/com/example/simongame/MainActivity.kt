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
                                // When the button in StartScreen is clicked, onEndGameClicked is triggered and adds the latest game sequence to the history list
                                onEndGameClicked = { sequence ->
                                    games.add(sequence) // the call is here to avoid duplicates on recomposition
                                    navController.navigate("history")
                                }
                            )
                        }

                        // Defines the destination "history" in the navigation graph
                        composable("history") {
                            HistoryScreen(
                                onBackClicked = {
                                    navController.navigate("start") {
                                        // Reference: https://developer.android.com/guide/navigation/backstack#pop
                                        // Tells the navigation controller to remove destinations from the back stack until it reaches the screen with the route "start"
                                        // inclusive = true: also removes the "start" destination itself from the back stack
                                        popUpTo("start") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}