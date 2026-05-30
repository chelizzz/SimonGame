package com.example.simongame

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetailScreen(score: Int, sequence: String, clicks: Int) {
    val isLandscape = isScreenLandscape()
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
        Text(
            text = stringResource(R.string.detail),
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        DrawDetail(score, sequence, clicks) // receives the parameters from HistoryScreen via MainActivity
    }
}


@Composable
fun DrawDetail(score: Int, sequence: String, clicks: Int) {
    val colorKeys = if (sequence.isNotBlank())
        sequence.split(", ")
    else
        emptyList()

    // Comprehensive row of the game: score on the left, sequence on the right
    Row(
        modifier = Modifier
            .background(
                color = GameConst.panelColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Left column
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            // Title: score
            Text(
                text = stringResource(R.string.score),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            // Maximum score obtained during this game
            Text(
                text = "$score",
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Right column
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Title: input
            Text(
                text = stringResource(R.string.input),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            // Reference: https://developer.android.com/develop/ui/compose/layouts/flow
            // The items automatically flow into the next line when the container runs out of space
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                colorKeys.forEachIndexed { index, key ->
                    // If the color tag index is less than the user's correct clicks, apply its color.
                    // Otherwise (it is the error or it was never pressed), make it gray.
                    val tagColor = if (index < clicks) {
                        GameConst.buColors[key] ?: Color.Gray
                    } else {
                        Color.Gray
                    }

                    Text(
                        modifier = Modifier
                            .background(
                                color = tagColor,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(10.dp), // inner color-tag padding
                        text = key,
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(score = 5, sequence = "R, G, B, M, Y, C", clicks = 3)
}

