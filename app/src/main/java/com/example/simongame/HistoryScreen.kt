package com.example.simongame

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/
// List of all the games played
val games: MutableList<String> = mutableListOf()

@Composable
fun HistoryScreen(onBackClicked: () -> Unit) {

    val configuration = LocalConfiguration.current
    val isLandscape = (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    val screenPadding = if (isLandscape)
                            32.dp  // --- LANDSCAPE LAYOUT ---
                        else
                            16.dp // --- PORTRAIT LAYOUT ---

    // Reference: https://developer.android.com/develop/ui/compose/lists
    // Displays the full history as a scrollable list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GameConst.backgroundColorTwo)
            .padding(screenPadding)
    ) {
        item {
            Button(onClick = onBackClicked) {
                Text(text = stringResource(R.string.back))
            }
        }

        // Header: title and total game count
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineMedium)
            Text(
                text = stringResource(R.string.games) + " " + games.size,
                style = MaterialTheme.typography.bodyMedium)
        }

        // Adds a list of games played to the LazyColumn, reversed so the most recent game appears first
        items(games.asReversed()) { game ->
            Spacer(modifier = Modifier.height(16.dp))
            DrawInput(game) // iterator of games returns a String, passed as parameter to DrawInput
        }
    }
}


// Renders a single game row: if the sequence is too long to fit, only the beginning is shown followed by a "…" indicator
@Composable
fun DrawInput(input: String) {
    // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
    val items = if (input.isNotEmpty()) // without this check the app crashes with a bug
                    input.split(", ") // split the comma-separated sequence and return a list of individual color labels
                else
                    emptyList()

    val cont = items.size // total number of buttons pressed in this game

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
        // Count of buttons pressed
        Text(
            text = "$cont ",
            color = Color.White,
            fontSize = 20.sp
        )

        // Reference: https://developer.android.com/develop/ui/compose/phases
        // Reference: https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/BoxWithConstraints.composable
        // A plain Row draws all items without caring about overflow, instead BoxWithConstraints measures available width at composition time,
        // so we can calculate how many color tags fit before drawing them
        BoxWithConstraints(
            modifier = Modifier.weight(1f) // fills all the space left after the count Text
        ) {
            val tagPadding = 8.dp
            val tagSpacing = 5.dp
            val tagWidth = 9.dp + (tagPadding * 2) // single char (9.dp) + left/right padding = 25.dp
            val tagWithGap = tagWidth + tagSpacing // tag + spacing gap = 30.dp
            val indicatorWidth = 20.dp // space reserved for the "…" truncation indicator
            val maxAllowed = ((maxWidth + tagSpacing) / tagWithGap).toInt() // max tags that fit without a truncation indicator

            val isTruncated = items.size > maxAllowed
            val maxVisible   = if (isTruncated)
                ((maxWidth - indicatorWidth) / tagWithGap).toInt()
            else
                maxAllowed
            val visibleItems = items.take(maxVisible)


            // Row of color tags representing the sequence of buttons pressed
            Row(
                horizontalArrangement = Arrangement.spacedBy(tagSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (item in visibleItems) {
                    Text(
                        modifier = Modifier
                            .background(
                                // Returns the value corresponding to the given key [item], or Color.Gray if such a key is not present in the map (?: - elvis operator)
                                color = GameConst.buColors[item] ?: Color.Gray,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(tagPadding),
                        text = item,
                        color = Color.White,
                        fontSize = 15.sp,
                    )
                }

                // Truncation indicator shown only when sequence is cut off
                if (isTruncated) {
                    Text(
                        text = "…",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(onBackClicked = {})
}