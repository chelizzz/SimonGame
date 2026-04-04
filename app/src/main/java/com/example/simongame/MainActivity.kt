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
        enableEdgeToEdge()
        // Set and display the UI content
        setContent {
            SimonGameTheme {
                val navController = rememberNavController()

                // Reference: https://developer.android.com/develop/ui/compose/components/scaffold
                // The scaffold fills the whole display area
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, startDestination = "start",
                        modifier = Modifier.padding(innerPadding)
                        //,.fillMaxSize()
                    ) {
                        composable("start") {
                            StartScreen(
                                onEndGameClicked = { navController.navigate("history") }
                            )
                        }
                        composable("history") {
                            HistoryScreen()
                        }
                    }
                }
            }
        }
    }
}