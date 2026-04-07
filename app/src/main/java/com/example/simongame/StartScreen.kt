package com.example.simongame

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(onEndGameClicked: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // The State variable inputSeq triggers a Recomposition whenever the value changes
    // It represents the sequence of buttons pressed until a specific moment in time
    var inputSeq by rememberSaveable { mutableStateOf("") }

    val backgroundColor = Color(0xFF541D8B) // background color of the screen
    val panelColor = Color(0xFF18275A) // background color of the panel containing UI elements

    // --- LANDSCAPE LAYOUT ---
    if (isLandscape) {
        // Main row of the StartScreen
        Row(
            // Reference: https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(32.dp), // inner padding, inside background, around elements
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ButtonGrid(
                colMod = Modifier
                    .weight(1f) // the button grid occupies 1/2 of the screen
                    .background(
                        color = panelColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp),

                onColorClicked = { clickedColor ->
                    inputSeq += clickedColor
                }
            )

            // Column container for input sequence and action buttons
            Column(
                modifier = Modifier
                    .weight(1f), // the column occupies the other 1/2 of the screen
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SequenceDisplay(
                    inputSeq,

                    textMod = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = panelColor,
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(20.dp) // inner padding separating the text from the border
                )

                ActionButtons(
                    onClearClicked = { inputSeq = "" },
                    onEndGameClicked = onEndGameClicked,
                    buMod = Modifier.fillMaxWidth()
                )
            }
        }

        // --- PORTRAIT LAYOUT ---
    } else {
        // Main column of the StartScreen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp), // inner padding, inside background, around elements
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.25f)) // occupies 25% of the screen height

            // "Panel" containing the button grid and the input sequence
            Column(
                modifier = Modifier
                    .weight(0.6f) // occupies 60% of the screen height
                    .background(
                        color = panelColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ButtonGrid(
                    colMod = Modifier
                        .weight(7f),

                    onColorClicked = { clickedColor ->
                        inputSeq += clickedColor
                    }
                )

                SequenceDisplay(
                    inputSeq,

                    textMod = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }

            Spacer(modifier = Modifier.weight(0.025f)) // occupies 2.5% of the screen height

            // Row of two action buttons: Delete and End game
            Row(
                modifier = Modifier
                    .weight(0.05f) // occupies 5% of the screen height
                    .fillMaxWidth(0.8f), // occupies 80% of the screen width
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButtons(
                    onClearClicked = { inputSeq = "" },
                    onEndGameClicked = onEndGameClicked,
                    buMod = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(0.075f)) // occupies 7.5% of the screen height
        }
    }
}


// Grid 3x2: a column of 3 rows, each containing 2 buttons
@Composable
fun ButtonGrid(colMod: Modifier, onColorClicked: (String) -> Unit) {
    Column(
        modifier = colMod,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Map of buttons colors in Compose format (with alpha channel set to 100% opacity-0xFF)
        val buColors = mapOf(
            "R" to Color(0xffdc2626), "G" to Color(0xFF00A63E), // red, green
            "B" to Color(0xFF155DFC), "M" to Color(0xFFC800DE), // blue, magenta
            "Y" to Color(0xFFF0B100), "C" to Color(0xFF05A9E8) // yellow, cyan
        )

        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/chunked.html
        // For each couple of colors, create a row of 2 buttons
        // The iterator rowPair selects a chunk of two colors from the map
        buColors.chunked(2).forEach { rowPair ->
            Row(
                modifier = Modifier
                    .weight(1f), // each row occupies 1/3 of the column
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // The iterator color selects each entry-color from the pair
                rowPair.forEach { color ->
                    Button(
                        modifier = Modifier
                            .weight(1f) // each button occupies 1/2 of the row
                            .fillMaxSize(),
                        onClick = { onColorClicked(color.key) },
                        shape = RoundedCornerShape(25),
                        colors = ButtonDefaults.buttonColors(containerColor = color.value)
                    ) {}
                }
            }
        }
    }
}

// Definition of an extension function for a map -> Map<K, V>.chunked(size: Int)
// With two generic types parameters representing the Key and the Value of the map -> <K, V>
// The return type is a list of smaller lists of entry objects -> List<List<Map.Entry<K, V>>>
fun <K, V> Map<K, V>.chunked(size: Int): List<List<Map.Entry<K, V>>> {
    return this.entries // returns an Iterable, a set of entries
        .chunked(size) // the std method splits the entries into a list of lists
}


@Composable
fun SequenceDisplay(inputSeq: String, textMod: Modifier) {
    Text(
        // Text component observes the State inputSeq and updates automatically
        text = inputSeq,
        modifier = textMod,
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 20.sp
    )
}


@Composable
fun ActionButtons(onClearClicked: () -> Unit, onEndGameClicked: () -> Unit, buMod: Modifier) {
    Button(
        onClick = onClearClicked,
        modifier = buMod
    ) {
        Text(text = stringResource(R.string.delete))
    }
    Button(
        onClick = onEndGameClicked,
        modifier = buMod
    ) {
        Text(text = stringResource(R.string.end_game))
    }
}


// Reference: https://developer.android.com/develop/ui/compose/tooling/previews
// @Preview avoids reliance on the emulator in Android Studio
// as this @Composable is shown in the design view of this file (with live updates)
@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    StartScreen(onEndGameClicked = {})
}