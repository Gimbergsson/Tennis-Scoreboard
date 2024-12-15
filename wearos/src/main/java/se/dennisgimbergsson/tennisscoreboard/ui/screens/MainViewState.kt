package se.dennisgimbergsson.tennisscoreboard.ui.screens

import androidx.compose.runtime.Immutable
import se.dennisgimbergsson.shared.data.models.Scoreboard

@Immutable
data class MainViewState(
    val scoreboard: Scoreboard = Scoreboard(),
)