package com.example.simongame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(onEndGameClicked: () -> Unit) {
    // List of buttons colors in Compose format (with alpha channel set to 100% opacity-0xFF)
    val buColors = listOf(
        Color(0xffdc2626), Color(0xFF00A63E), // red, green
        Color(0xFF155DFC), Color(0xFFC800DE), // blue, magenta
        Color(0xFFF0B100), Color(0xFF05A9E8) // yellow, cyan
    )

    // Main column of the StartScreen
    Column(
        // Reference: https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF541D8B)) // background color of screen
            .padding(16.dp), // inner padding, inside background, around elements
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f)) // occupies 30% of the screen height

        // GRID 2x3: a column of 3 rows, each containing 2 buttons
        Column(
            modifier = Modifier
                .weight(0.4f) // occupies 40% of the screen height
                .fillMaxWidth()
                .background(Color(0xFF18275A))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/chunked.html
            // For each couple of colors, create a row of 2 buttons
            // The iterator rowPair selects a chunk of two colors from the list
            buColors.chunked(2).forEach { rowPair ->
                Row(
                    modifier = Modifier
                        .weight(1f) // each row occupies 1/3 of the column
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // The iterator color selects each color from the pair
                    rowPair.forEach { color ->
                        // Reference: https://developer.android.com/reference/kotlin/androidx/compose/material3/Button.composable
                        Button(
                            modifier = Modifier
                                .weight(1f) // each button occupies 1/2 of the row
                                .fillMaxSize(),
                            onClick = {},
                            shape = RoundedCornerShape(25),
                            colors = ButtonDefaults.buttonColors(containerColor = color)
                        ) {}
                    }
                }
            }
        }

        Text(
            modifier = Modifier
                .weight(0.1f) // occupies 10% of the screen height
                .fillMaxWidth()
                .background(Color(0xFF18275A)),
            text = "Your Turn!",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(0.05f)) // occupies 5% of the screen height

        Row(modifier = Modifier.weight(0.05f)) { // occupies 5% of the screen height
            Button(onClick = {}) {
                Text(text = stringResource(R.string.delete))
            }
            Button(onClick = onEndGameClicked) {
                Text(text = stringResource(R.string.end_game))
            }
        }

        Spacer(modifier = Modifier.weight(0.1f)) // occupies 10% of the screen height
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