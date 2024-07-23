package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import com.example.unscramble.data.ROUND_DURATION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private lateinit var currentWord: String
    private val _gameTimer: GameTimer = GameTimer(durationInSeconds = ROUND_DURATION)

    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }

    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
        startTimer()
    }
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    fun checkUserGuess() {

        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // User's guess is wrong, show an error
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = false)
            }
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // User's guess is wrong, show an error
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // Reset user guess
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
            startTimer()
        }
    }

    fun skipRound() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
        restartTimer()
    }

    private fun runTimer(): Pair<(Long) -> Unit, () -> Unit> {
        val onTick: (Long) -> Unit = { timeLeft ->
            println("Time left: $timeLeft seconds")
            _uiState.update { currentState ->
                currentState.copy(currentRoundTime = timeLeft)
            }
        }

        // Define what happens when the timer finishes
        val onFinish: () -> Unit = {
            println("Timer finished!")
        }

        return Pair(onTick, onFinish)
    }

    private fun startTimer() {
        val (onTick, onFinish) = runTimer()
        _gameTimer.start(onTick = onTick, onFinish)
    }
    private fun restartTimer()
    {
        val (onTick, onFinish) = runTimer()
        _gameTimer.restart(onTick, onFinish)
    }


}