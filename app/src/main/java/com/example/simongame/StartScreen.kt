package com.example.simongame

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun StartScreen(onEndGameClicked: (String) -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)

    // The State variable inputSeq triggers a Recomposition whenever the value changes
    // It represents the sequence of buttons pressed until a specific moment in time
    var inputSeq by rememberSaveable { mutableStateOf("") }

    val game = Game
    var attempts by rememberSaveable { mutableStateOf(game.getRound()) }
    var text by rememberSaveable { mutableStateOf(game.playComputer()) }

    // --- LANDSCAPE LAYOUT ---
    if (isLandscape) {
        // Main row of the StartScreen
        Row(
            // Reference: https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier
            modifier = Modifier
                .fillMaxSize()
                .background(GameConst.backgroundColorOne)
                .padding(32.dp), // inner padding, inside background, around elements
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ButtonGrid(
                colModifier = Modifier
                    .weight(1f) // the button grid occupies 1/2 of the screen
                    .background(
                        color = GameConst.panelColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp),

                onColorClicked = { clickedColor ->
                    // If it's the first button to be pressed, the sequence is empty
                    if (inputSeq.isEmpty()) {
                        inputSeq += clickedColor
                    } else {
                        // Template Expressions: pieces of code that are evaluated and whose results are concatenated into a string
                        inputSeq = "$inputSeq, $clickedColor"
                    }
                }
            )

            // Column container for input sequence and action buttons
            Column(
                modifier = Modifier
                    .weight(1f), // the column occupies the other 1/2 of the screen
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                SequenceDisplay(
                    inputSeq,
                    textModifier = Modifier
                        .weight(1f) // automatically fill the remaining space in the parent container (Column)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = GameConst.panelColor,
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(20.dp) // inner padding separating the text from the border
                )

                // FlowColumn automatically distributes the action buttons in different lines
                FlowColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    ActionButtons(
                        onPauseClicked = { inputSeq = "" },
                        onEndGameClicked = {
                            onEndGameClicked(inputSeq)
                            inputSeq = ""
                        },
                        buModifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- PORTRAIT LAYOUT ---
    } else {
        // Main column of the StartScreen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GameConst.backgroundColorOne)
                .padding(16.dp), // inner padding, inside background, around elements
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header containing the Simon Game Logo
            Column(
                modifier = Modifier.weight(0.15f), // occupies 15% of the screen height
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(0.025f)) // occupies 2.5% of the screen height

            // Panel containing the button grid and the input sequence
            Column(
                modifier = Modifier
                    .weight(0.65f) // occupies 65% of the screen height
                    .background(
                        color = GameConst.panelColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ButtonGrid(
                    colModifier = Modifier
                        .weight(5f),

                    onColorClicked = { clickedColor ->
                        if (attempts > 0) {
                            if (inputSeq.isEmpty())
                                inputSeq += clickedColor
                            else
                                inputSeq = "$inputSeq, $clickedColor"

                            // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
                            if (!(text.startsWith(inputSeq))) {
                                text = "Game Over!"
                                attempts = 0
                            }

                            attempts--

                        } else {
                            text = "Too many buttons pressed!"
                        }
                    }
                )

                SequenceDisplay(
                    // inputSeq,
                    text,
                    textModifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }

            Spacer(modifier = Modifier.weight(0.025f)) // occupies 2.5% of the screen height

            // Reference: https://developer.android.com/develop/ui/compose/layouts/flow
            // Rows of action buttons: Start game, Pause and End game
            FlowRow(
                modifier = Modifier
                    .weight(0.15f) // occupies 15% of the screen height
                    .fillMaxWidth(0.8f), // occupies 80% of the screen width
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                ActionButtons(
                    onPauseClicked = { inputSeq = "" },
                    onEndGameClicked = {
                        onEndGameClicked(inputSeq)
                        inputSeq = ""
                    },
                    buModifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}


// Grid 3x2: a column of 3 rows, each containing 2 buttons
@Composable
fun ButtonGrid(colModifier: Modifier, onColorClicked: (String) -> Unit) {
    Column(
        modifier = colModifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/chunked.html
        // For each couple of colors, create a row of 2 buttons (from 6 colors, it creates 3 rows)
        // The iterator rowPair selects a chunk of two colors from the map
        GameConst.buColors.chunked(2).forEach { rowPair ->
            Row(
                modifier = Modifier
                    .weight(1f), // each row occupies 1/3 of the column
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // The iterator color selects each entry-color from the pair
                rowPair.forEach { color ->
                    var isClicked by remember { mutableStateOf(false) }

                    // Since isClicked = false needs to happen after a time delay, a Coroutine is used so it doesn't block the UI thread
                    val coroutineScope = rememberCoroutineScope()

                    // When isClicked is true, the padding increases to 10.dp making the button to move inward
                    // animateDpAsState: instead of the value jumping instantly from 0 to 10, it calculates the intermediate values over time (1, 2, 3 ... 10)
                    val animatedPadding by animateDpAsState(
                        targetValue = if (isClicked) 10.dp else 0.dp,
                        animationSpec = tween(durationMillis = 200),
                        label = "padding"
                    )

                    // When isClicked is true, the color changes from transparent to a specific glow color
                    val glowColor by animateColorAsState(
                        targetValue = if (isClicked) GameConst.glowColors[color.key]!! else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "glow_color"
                    )

                    // When isClicked is true, the elevation increases to 20.dp making the button to emit "light"
                    val glowElevation by animateDpAsState(
                        targetValue = if (isClicked) 20.dp else 0.dp,
                        animationSpec = tween(durationMillis = 200),
                        label = "glow_elevation"
                    )

                    Button(
                        modifier = Modifier
                            .weight(1f) // each button occupies 1/2 of the row
                            .fillMaxSize()
                            .shadow( // the button looks like it is emitting light onto the background
                                elevation = glowElevation,
                                shape = RoundedCornerShape(25),
                                ambientColor = glowColor,
                                spotColor = glowColor,
                            )
                            .padding(animatedPadding),
                        // Reference: https://kotlinlang.org/docs/lambdas.html#function-types
                        // ButtonGrid calls the function passed as parameter to be executed after click event
                        // When invoked in StartScreen the function is then defined
                        onClick = {
                            // Check if the animation isn't launched already
                            if (!isClicked) {
                                // The button stays in the "glowing" state for 350ms and then automatically switches back to the normal state
                                coroutineScope.launch {
                                    isClicked = true
                                    delay(350)
                                    isClicked = false
                                }
                            }
                            onColorClicked(color.key)
                        },
                        shape = RoundedCornerShape(25),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isClicked) GameConst.glowColors[color.key]!! else color.value)
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
fun SequenceDisplay(inputSeq: String, textModifier: Modifier) {
    Text(
        // Text component observes the state inputSeq and updates automatically
        text = inputSeq,
        modifier = textModifier,
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 20.sp
    )
}


@Composable
fun ActionButtons(onPauseClicked: () -> Unit, onEndGameClicked: () -> Unit, buModifier: Modifier) {
    Button(
        onClick = {}, // TO DO: create new game every time start button is clicked
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    ) {
        Text(text = stringResource(R.string.start_game))
    }

    Button(
        onClick = onPauseClicked,
        modifier = buModifier
    ) {
        Text(text = stringResource(R.string.pause))
    }

    Button(
        onClick = onEndGameClicked, // TO DO: save game in database
        modifier = buModifier
    ) {
        Text(text = stringResource(R.string.end_game))
    }
}


class GameConst {
    companion object {
        val backgroundColorOne = Color(0xFF541D8B) // background color of StartScreen
        val backgroundColorTwo = Color(0xFF26368E) // background color of HistoryScreen

        val panelColor = Color(0xFF18275A) // background color of the panel containing UI elements

        // Map of buttons colors in Compose format (with alpha channel set to 100% opacity-0xFF)
        val buColors = mapOf(
            "R" to Color(0xffdc2626), "G" to Color(0xFF00A63E), // red, green
            "B" to Color(0xFF155DFC), "M" to Color(0xFFC800DE), // blue, magenta
            "Y" to Color(0xFFF0B100), "C" to Color(0xFF05A9E8) // yellow, cyan
        )

        val glowColors = mapOf(
            "R" to Color(0xFFFF0000), "G" to Color(0xFF01DA5A), // red, green
            "B" to Color(0xFF0000FF), "M" to Color(0xFFF700FF), // blue, magenta
            "Y" to Color(0xFFFFD500), "C" to Color(0xFF00E1FF) // yellow, cyan
        )
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