package com.example.simongame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
// Receives the sequence from StartScreen through MainActivity
fun HistoryScreen(sequence: String, onBackClicked: () -> Unit) {
    //Reference: https://developer.android.com/develop/ui/compose/lists
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF26368E))
            .padding(16.dp)
    ) {
        item {
            Button(onClick = onBackClicked) {
                Text(text = stringResource(R.string.back))
            }
        }

        // Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineMedium)
            Text(
                text = stringResource(R.string.games) + "0",
                style = MaterialTheme.typography.bodyMedium)
        }

        // Add a single item
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = sequence)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(sequence = "R, G, B, M, Y, C", onBackClicked = {})
}