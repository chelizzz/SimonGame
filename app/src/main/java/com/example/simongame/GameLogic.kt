package com.example.simongame

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// Represents UI state for the Simon Game
data class Game(
    val sequence: String = "",
    val round: Int = 0,
    val isGameActive: Boolean = false,
    val messageRes: Int? = R.string.press
)


// Reference: https://developer.android.com/topic/libraries/architecture/viewmodel
// The purpose of ViewModel is to encapsulate the data for the UI layer
class GameViewModel : ViewModel() {
    // Expose UI state
    private val _uiState = MutableStateFlow(Game())
    val uiState = _uiState.asStateFlow()

    // Internal game variables (not exposed to UI)
    private val availableColors = GameConst.buColors.keys
    private var computerSequence = ""
    private var attempts = 0


    // --- UI Actions ---
    fun startGame() {
        // Increment round and build the target sequence
        attempts++

        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/random.html
        val randomColor = availableColors.random()
        computerSequence = if (computerSequence.isEmpty()) randomColor
                           else "$computerSequence, $randomColor"

        Log.d("SimonGame", "Computer: $computerSequence")

        _uiState.update { currentState ->
            currentState.copy(
                sequence = "",
                round = attempts,
                isGameActive = true,
                messageRes = R.string.demo,
            )
        }

        showDemo() // TO DO: isGameActive should be false while demo is playing
    }

    private fun showDemo() {

    }


    fun onUserColorClicked(clickedColor: String) {
        val currentState = _uiState.value // current Game

        if (attempts > 0) {
            // Build the user input sequence string
            val input = if (currentState.sequence.isEmpty()) clickedColor
                        else "${currentState.sequence}, $clickedColor"

            Log.d("SimonGame", "User: $input")

            // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
            val isWrong = !(computerSequence.startsWith(input))
            if (isWrong) attempts = 0

            attempts--

            _uiState.update { state ->
                state.copy(
                    sequence = input,
                    round = attempts,
                    messageRes = if (isWrong) R.string.wrong else null
                )
            }

        } else {
            _uiState.update { state ->
                state.copy(
                    messageRes = R.string.exceed
                )
            }
        }
    }


    fun pauseGame() {

    }


    fun resetGame() {
        computerSequence = ""
        attempts = 0
        _uiState.value = Game()
    }
}