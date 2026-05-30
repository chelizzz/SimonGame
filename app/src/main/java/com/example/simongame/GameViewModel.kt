package com.example.simongame

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.emptyList


// Represents UI state for the Simon Game
data class GameUIState(
    val currentInput: String = "",
    var clicksLeft: Int = 0,
    val isGameActive: Boolean = false,
    val messageRes: Int? = R.string.press,
    val activeGlowKey: String? = null, // null = no buttons glowing; "R" = red button glowing, etc.
    val isUserTurn: Boolean = false, // true when the user is allowed to click on a SimonButton
    val isPaused: Boolean = false
)


// Reference: https://developer.android.com/topic/libraries/architecture/viewmodel
// The purpose of ViewModel is to encapsulate the data for the UI layer
class GameViewModel(private val repository: GameRepository) : ViewModel() {
    // Expose UI state
    private val _uiState = MutableStateFlow(GameUIState())
    val uiState = _uiState.asStateFlow()

    // Internal game state variables (not exposed to UI)
    private val availableColors = GameConst.buColors.keys
    private var computerSequence = ""

    // Tracks the ongoing glow animation coroutine to allow cancellation
    private var glowJob: Job? = null
    // Tracks the ongoing demo coroutine to allow cancellation
    private var demoJob: Job? = null


    // --- UI ACTIONS ---

    fun startGame() {
        // Launched in a coroutine to add a specific startup delay
        // This avoids putting the delay inside showDemo(), which would cause an unwanted double delay (startNextRound)
        viewModelScope.launch {
            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    clicksLeft = 1, // initialize the counter for remaining attempts
                    isGameActive = true,
                    messageRes = null
                )
            }

            // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/random.html
            // Directly assign the first random color for a new game
            computerSequence = availableColors.random()

            Log.d("SimonGame", "Computer - startGame: $computerSequence")

            // Before the demo begins, gives the user time to lift their finger from the Start button
            delay(1000)

            showDemo()
        }
    }


    // Reference: https://developer.android.com/topic/libraries/architecture/coroutines
    private fun showDemo() {
        // Lock user input before the demo begins
        _uiState.update { state ->
            state.copy(
                isUserTurn = false,
                isPaused = false
            )
        }

        // Cancel any ongoing demo to prevent overlapping animations
        demoJob?.cancel()

        demoJob = viewModelScope.launch {
            val sequenceList = computerSequence.split(", ")
            for (color in sequenceList) {
                // If the user pauses the game, the loop suspends execution here
                while (_uiState.value.isPaused) {
                    delay(100)
                }
                animateGlow(color) // the loop suspends for the 350ms animation duration
                delay(250) // then waits an additional 250ms before showing the next color
            }

            // After the demo, it is the user turn to play, unlock clicks (user input)
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

        // Play the corresponding sound effect
        sound.play(colorKey)

        // The duration the button remains physically illuminated
        delay(350)

        // Turn OFF the glow animation by clearing the active key
        _uiState.update { it.copy(activeGlowKey = null) }
    }


    fun onUserColorClicked(clickedColor: String) {
        // SAFETY LOCK: Ignore clicks during the computer's demo or game over
        // If the user spams buttons, execution stops here preventing useless calculations
        if (!_uiState.value.isUserTurn) return

        // Cancel any ongoing glow animation to prevent visual glitches with the user clicks
        glowJob?.cancel()

        // The button stays in the "glowing" state for 350ms and then automatically switches back to the normal state
        glowJob = viewModelScope.launch {
            animateGlow(clickedColor)
        }

        val currentState = _uiState.value

        // Build the user input sequence string
        val input = if (currentState.currentInput.isEmpty()) clickedColor
                    else "${currentState.currentInput}, $clickedColor"

        Log.d("SimonGame", "User: $input")

        // INPUT VERIFICATION
        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/
        val isFailed = !(computerSequence.startsWith(input))
        val isRoundWon = (input == computerSequence)

        if (isFailed) {
            sound.play("fail")

            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    currentInput = input,
                    clicksLeft = 0, // game over
                    isGameActive = false, // game over
                    messageRes = R.string.fail,
                    isUserTurn = false // the user can no longer play the current game
                )
            }

            // The maximum score is obtained by subtracting 1 for the error
            val score = computerSequence.split(",").size - 1
            saveCurrentGame(score) // save the score to the database

            // Launch a parallel coroutine that waits for the glow animation to finish before dimming the entire board
            viewModelScope.launch {
                delay(350)
                // After 350ms, the glow animation is complete: dim the grid and lock user input
                _uiState.update { it.copy(isUserTurn = false) }
            }

        } else if (isRoundWon) {
            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    currentInput = input,
                    clicksLeft = 0 // round cleared
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
            // UPDATE UI STATE
            _uiState.update { state ->
                state.copy(
                    currentInput = input,
                    clicksLeft = state.clicksLeft-- // correct input but the sequence is not yet complete
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

            // Calculate the new required attempts equal to the new sequence length
            val lengthSequence = computerSequence.split(", ").size

            _uiState.update { state ->
                state.copy(
                    currentInput = "", // clear the user's input on screen
                    clicksLeft = lengthSequence
                )
            }

            showDemo() // start the automated sequence demo
        }
    }


    fun pauseGame() {
        // Change the paused state from false to true, and vice versa
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }


    fun endGame() {
        // Safety check: the action is only enabled during an active game
        if (!_uiState.value.isGameActive) return

        // Extract state variables for better readability
        val isUserTurn = _uiState.value.isUserTurn
        val currentInput = _uiState.value.currentInput

        // Calculate the current round
        val round = computerSequence.split(",").size

        // CASE 1: The very first sequence demo is playing (round == 1 && !isUserTurn)
        // -> do not save anything to the database
        if (round > 1 || isUserTurn) {
            // CASE 2: A sequence demo is playing OR it's the user's turn but no buttons pressed yet
            // -> the user only completed the previous rounds
            val score = if ((round > 1 && !isUserTurn) || (isUserTurn && currentInput.isEmpty())) {
                            round - 1
                            // CASE 3: It's the user's turn and at least one correct button is pressed
                            // -> include all correctly pressed buttons from this round
                        } else {
                            round
                        }

            saveCurrentGame(score) // save the score to the database
        }

        demoJob?.cancel() // stop the demo
        glowJob?.cancel() // turn off all glow animations

        // Reset internal game variable in ViewModel
        computerSequence = ""

        _uiState.value = GameUIState()
    }


    // --- PERSISTENT STATE ---

    /**
     * .stateIn: Converts the Flow from the database into a StateFlow for the HistoryScreen UI.
     * It caches the latest list of games and keeps it active for 5 seconds after the UI disappears
     * to properly handle screen rotations without re-querying the database.
     */
    val gamesHistory = repository.getAllGames()
                          .stateIn(
                              viewModelScope,
                              SharingStarted.WhileSubscribed(5000),
                              emptyList()
                          )


    // Launches a new coroutine to insert the data without blocking the main UI thread
    private fun saveCurrentGame(score: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(
                GameEntity(
                    score = score,
                    sequence = computerSequence
                )
            )
        }
    }


    // --- SOUND EFFECT ---

    private val sound = SoundManager()

    // The SoundManager class and its AudioTrack are destroyed alongside the ViewModel
    override fun onCleared() {
        super.onCleared()
        sound.release()
    }
}