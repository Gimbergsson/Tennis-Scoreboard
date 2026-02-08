package se.dennisgimbergsson.tennisscoring.data.models

data class Scoreboard(
    val matchStartedAt: Long = 0,
    val homeScore: Score = Score(),
    val awayScore: Score = Score(),
    val isTieBreak: Boolean = false,
)