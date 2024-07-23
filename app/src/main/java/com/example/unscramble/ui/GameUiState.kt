package com.example.unscramble.ui

import com.example.unscramble.data.ROUND_DURATION

data class GameUiState(
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false,
    val score: Int = 0,
    val currentWordCount: Int = 1,
    val isGameOver: Boolean = false,
    val currentRoundTime: Long = ROUND_DURATION
)
