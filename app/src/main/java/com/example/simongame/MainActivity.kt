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
import androidx.lifecycle.viewmodel.compose.viewModel


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

                // --- PERSISTENT STATE ---
                // 1. Initialize the database using applicationContext (lives for the entire lifecycle of the app
                // and it is not destroyed and recreated on screen rotations)
                val database = GameRoomDatabase.getDatabase(applicationContext)

                // 2. Initialize the repository using the DAO
                val repository = GameRepository(database.gameDao())

                // 3. Initialize the gameViewModel using the repository
                val gameViewModel: GameViewModel = viewModel{
                    GameViewModel(repository)
                }

                // The scaffold fills the whole display area
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "history",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Defines the destination "history" in the navigation graph
                        composable("history") {
                            HistoryScreen(
                                viewModel = gameViewModel,
                                onNextClicked = {
                                    navController.navigate("start") {
                                        // Reference: https://developer.android.com/guide/navigation/backstack#pop
                                        // Tells the navigation controller to remove destinations from the back stack until it reaches the screen with the route "start"
                                        // inclusive = true: also removes the "start" destination itself from the back stack
                                        popUpTo("start") { inclusive = true }
                                    }
                                },
                                // When a single game row in HistoryScreen is clicked, onInputClicked is triggered with the parameters:
                                // score (e.g. 5), sequence (e.g. "R, G, B, M, Y, C"), clicks (e.g. 3)
                                onInputClicked = { score, sequence, clicks ->
                                    // The string route "detail/$s/$s/$c" indicates the app to navigate to the detail screen, appending the value of the variables (string interpolation)
                                    navController.navigate("detail/$score/$sequence/$clicks") {
                                        popUpTo("detail/$score/$sequence/$clicks") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Defines the destination "detail" in the navigation graph, where {score}, {sequence}, {clicks} act like variables in the URL
                        composable("detail/{score}/{sequence}/{clicks}") { backStackEntry ->
                            DetailScreen(
                                // backStackEntry.arguments: holds information about the current destination, including the argument passed through the navigation route
                                // ?.getString(): ? (safe call operator) since arguments could be null, it ensures the app doesn't crash and the expression returns null
                                // orEmpty(): Kotlin helper function for Strings, if getString returns null, it returns an empty string
                                // ?.toIntOrNull() ?: 0: converts the string into an integer, if it returns null, it use 0 instead
                                score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0,
                                sequence = backStackEntry.arguments?.getString("sequence").orEmpty(),
                                clicks = backStackEntry.arguments?.getString("clicks")?.toIntOrNull() ?: 0
                            )
                        }

                        // Defines the destination "start" in the navigation graph
                        composable("start") {
                            StartScreen(
                                viewModel = gameViewModel,
                                // When the button in StartScreen is clicked, onEndGameClicked is triggered and navigates to "history"
                                onEndGameClicked = {
                                    navController.navigate("history") {
                                        popUpTo("history") { inclusive = true }
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