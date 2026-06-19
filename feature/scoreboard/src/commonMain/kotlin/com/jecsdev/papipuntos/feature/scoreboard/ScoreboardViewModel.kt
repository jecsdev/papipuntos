package com.jecsdev.papipuntos.feature.scoreboard

import androidx.lifecycle.ViewModel

/**
 * Holds the scoreboard state. For this UI port it only serves the hardcoded
 * sample data; real point/validation logic is out of scope.
 */
class ScoreboardViewModel : ViewModel() {
    val uiState: ScoreboardUiState = ScoreboardSampleData.state
}
