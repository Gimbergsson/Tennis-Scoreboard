package se.dennisgimbergsson.tennisscoreboard.ui.screens

import androidx.compose.runtime.Immutable
import se.dennisgimbergsson.tennisscoreboard.data.models.Scoreboard

@Immutable
data class MainViewState(
    val scoreboard: Scoreboard = Scoreboard(),
)