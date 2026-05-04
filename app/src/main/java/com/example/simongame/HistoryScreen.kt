package com.example.simongame

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
fun HistoryScreen(onNextClicked: () -> Unit, onInputClicked: (String) -> Unit) {
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
            FloatingActionButton(onClick = onNextClicked) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }

        // Header: title and total games count
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

            // iterator of games returns a String (game) and passes it as parameter to DrawInput
            DrawInput(game, rowMod = Modifier
                    .clickable(onClick = { onInputClicked(game) }) // Modifier.clickable: adds click behavior to any composable
                    .background(
                        color = GameConst.panelColor,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(15.dp)
                    .fillMaxWidth()
            )
        }
    }
}


// Renders a single game row: if the sequence is too long to fit, only the beginning is shown followed by a "…" indicator
@Composable
fun DrawInput(input: String, rowMod: Modifier) {
    // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
    val items = if (input.isNotEmpty()) // without this check the app crashes with a bug
                    input.split(", ") // split the comma-separated sequence and return a list of individual color labels
                else
                    emptyList()

    // Comprehensive row of the game: count on the left, sequence on the right
    Row(
        modifier = rowMod,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Total number of buttons pressed in this game
        Text(
            text = "${items.size} ",
            color = Color.White,
            fontSize = 20.sp
        )

        // Reference: https://developer.android.com/develop/ui/compose/phases
        // Reference: https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/BoxWithConstraints.composable
        // A standard Row doesn't know exactly how many pixels or DPs wide the container is, until after it has already tried to draw everything.
        // BoxWithConstraints measures the available width at composition time, then does some math (maxAllowed and maxVisible) to decide what to actually draw.
        BoxWithConstraints(
            modifier = Modifier.weight(1f) // fills all the space left after the count Text
        ) {
            val tagSpacing = 5.dp // the width of the empty space between two tags
            val tagWidth = 25.dp // the width of a single tag: char (9.dp) + left/right padding (16.dp)

            // maxWidth: specific property in BoxWithConstraintsScope that describes the total horizontal space available in the Row
            // .toInt(): rounds down the result (e.g. 4.8 becomes 4, because you cannot display 80% of a tag)
            val maxAllowed = (maxWidth / (tagWidth + tagSpacing)).toInt() // number of color tags that fit without a truncation indicator
            val isTruncated = items.size > maxAllowed
            val maxVisible   = if (isTruncated)
                                    ((maxWidth - tagWidth) / (tagWidth + tagSpacing)).toInt() // removes tagWidth from maxWidth to reserve the space for indicator
                                else
                                    maxAllowed

            // Row of color tags representing the sequence of buttons pressed
            Row(
                horizontalArrangement = Arrangement.spacedBy(tagSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.take(maxVisible).forEach { item ->
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

                // Truncation indicator shown only when sequence is cut off
                if (isTruncated) {
                    Text(
                        text = "…",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(onNextClicked = {}, onInputClicked = {})
}