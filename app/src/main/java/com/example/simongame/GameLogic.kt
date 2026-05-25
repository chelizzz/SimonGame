package com.example.simongame

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Represents UI state for the Simon Game
data class Game(
    val sequence: String = "",
    val clicksLeft: Int = 0,
    val isGameActive: Boolean = false,
    val messageRes: Int? = R.string.press,
    val activeGlowKey: String? = null, // null = no buttons glowing; "R" = red button glowing, etc.
    // The safety lock: true when the user is allowed to tap, false during the computer's demo or at game over
    val isUserTurn: Boolean = false
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

    // Tracks the ongoing glow animation coroutine to allow cancellation
    private var glowJob: Job? = null
    // Tracks the ongoing demo coroutine to allow cancellation
    private var demoJob: Job? = null


    // --- UI Actions ---

    fun startGame() {
        // Launched in a coroutine to add a specific startup delay
        // This avoids putting the delay inside showDemo(), which would cause an unwanted double delay (startNextRound)
        viewModelScope.launch {
            // Restart the round counter
            attempts = 1

            // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/random.html
            // Directly assign the first random color for a new game
            computerSequence = availableColors.random()

            Log.d("SimonGame", "Computer - startGame: $computerSequence")

            _uiState.update { state ->
                state.copy(
                    sequence = "",
                    clicksLeft = attempts,
                    isGameActive = true,
                    messageRes = R.string.demo,
                )
            }

            // Before the demo begins, gives the user time to lift their finger from the Start button
            delay(1000)

            showDemo()
        }
    }


    // Reference: https://developer.android.com/topic/libraries/architecture/coroutines
    private fun showDemo() {
        // Lock user input before the demo begins
        _uiState.update { it.copy(isUserTurn = false) }

        // Cancel any ongoing demo to prevent overlapping animations
        demoJob?.cancel()

        demoJob = viewModelScope.launch {
            val sequenceList = computerSequence.split(", ")
            for (color in sequenceList) {
                animateGlow(color) // the loop suspends for the 350ms animation duration
                delay(250) // then waits an additional 250ms before showing the next color
            }

            // After the demo, it is the user turn to play, unlock taps (user input)
            _uiState.update { state ->
                state.copy(
                    messageRes = null,
                    isUserTurn = true
                )
            }
        }
    }


    // Suspends execution until the glow animation completes
    private suspend fun animateGlow(colorKey: String) {
        // Turn ON the glow animation by setting the activeGlowKey to the targeted color
        _uiState.update { it.copy(activeGlowKey = colorKey) }

        // The duration the button remains physically illuminated
        delay(350)

        // Turn OFF the glow animation by clearing the active key
        _uiState.update { it.copy(activeGlowKey = null) }
    }


    fun onUserColorClicked(clickedColor: String) {
        // SAFETY LOCK: Ignore taps during the computer's demo or game over
        // If the user spams buttons, execution stops here preventing useless calculations
        if (!_uiState.value.isUserTurn) return

        // Cancel any ongoing glow animation to prevent visual glitches with the user taps
        glowJob?.cancel()

        // The button stays in the "glowing" state for 350ms and then automatically switches back to the normal state
        glowJob = viewModelScope.launch {
            animateGlow(clickedColor)
        }

        val currentState = _uiState.value // current Game state

        // Build the user input sequence string
        val input = if (currentState.sequence.isEmpty()) clickedColor
                    else "${currentState.sequence}, $clickedColor"

        Log.d("SimonGame", "User: $input")

        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
        // INPUT VERIFICATION
        val isFailed = !(computerSequence.startsWith(input))
        val isRoundWon = (input == computerSequence)

        if (isFailed) {
            attempts = 0 // game over

            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    sequence = input,
                    clicksLeft = attempts,
                    messageRes = R.string.fail
                )
            }

            // Launch a parallel coroutine that waits for the glow animation to finish before dimming the entire board
            viewModelScope.launch {
                delay(350)
                // After 350ms, the glow animation is complete: dim the grid and lock user input
                _uiState.update { it.copy(isUserTurn = false) }
            }

        } else if (isRoundWon) {
            attempts = 0 // round cleared

            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    sequence = input,
                    clicksLeft = attempts
                )
            }

            // Lock user input while preparing the next round
            viewModelScope.launch {
                delay(350)
                _uiState.update { it.copy(isUserTurn = false) }
            }

            // The user completed the sequence, start the next round
            startNextRound()

        // CORRECT INPUT: User turn in progress
        } else {
            attempts-- // correct input but the sequence is not yet complete

            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    sequence = input,
                    clicksLeft = attempts
                )
            }
        }
    }


    private fun startNextRound() {
        viewModelScope.launch {
            delay(1100) // little break before the next round begins

            val randomColor = availableColors.random()
            computerSequence = "$computerSequence, $randomColor"

            Log.d("SimonGame", "Computer - startNextRound: $computerSequence")

            // Calculate the new required attempts (equal to the new sequence length)
            attempts = computerSequence.split(", ").size

            _uiState.update { state ->
                state.copy(
                    sequence = "", // clear the user's input on screen
                    clicksLeft = attempts,
                    messageRes = R.string.demo
                )
            }

            showDemo() // start the automated sequence demo
        }
    }


    fun pauseGame() {

    }


    fun resetGame() {
        demoJob?.cancel() // stop the demo
        glowJob?.cancel() // turn off all glow animations

        // Internal game variables in viewmodel
        computerSequence = ""
        attempts = 0

        _uiState.value = Game()
    }
}