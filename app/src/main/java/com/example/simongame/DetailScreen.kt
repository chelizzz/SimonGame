package com.example.simongame

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetailScreen(sequence: String) {
    val configuration = LocalConfiguration.current
    val isLandscape = (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    val screenPadding = if (isLandscape)
                            32.dp  // --- LANDSCAPE LAYOUT ---
                        else
                            16.dp // --- PORTRAIT LAYOUT ---

    // Displays details of the clicked game sequence
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameConst.backgroundColorOne)
            .padding(screenPadding)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        DrawDetail(sequence) // receives the color sequence from HistoryScreen via MainActivity
    }
}


@Composable
fun DrawDetail(input: String) {
    // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
    val items = if (input.isNotEmpty()) // without this check the app crashes with a bug
        input.split(", ") // split the comma-separated sequence and return a list of individual color labels
    else
        emptyList()

    // Comprehensive row of the game: count on the left, sequence on the right
    Row(
        modifier = Modifier
            .background(
                color = GameConst.panelColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Total number of buttons pressed in this game
        Text(
            text = "${items.size} ",
            color = Color.White,
            fontSize = 20.sp
        )

        // Reference: https://developer.android.com/develop/ui/compose/layouts/flow
        // The items automatically flow into the next line when the container runs out of space
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items.forEach { item ->
                Text(
                    modifier = Modifier
                        .background(
                            // Returns the value corresponding to the given key [item], or Color.Gray if such a key is not present in the map (?: - elvis operator)
                            color = GameConst.buColors[item] ?: Color.Gray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(8.dp), // inner color-tag padding
                    text = item,
                    color = Color.White,
                    fontSize = 15.sp,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(sequence = "R, G, B, M, Y, C")
}

