package se.dennisgimbergsson.tennisscoreboard.ui.screens

import androidx.compose.runtime.Immutable
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@Immutable
data class MainViewState(
    val scoreboardHistory: List<Scoreboard> = listOf(Scoreboard()),
    val savedScoreboards: List<Scoreboard> = listOf(Scoreboard()),
)