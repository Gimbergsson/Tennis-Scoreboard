package se.dennisgimbergsson.shared.data.models

data class Scoreboard(
    val homeScore: Score = Score(),
    val awayScore: Score = Score(),
    val peekDrawer: Boolean = false,
)