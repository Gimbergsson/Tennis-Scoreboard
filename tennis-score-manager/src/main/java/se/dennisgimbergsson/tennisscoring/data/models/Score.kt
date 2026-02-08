package se.dennisgimbergsson.tennisscoring.data.models

import se.dennisgimbergsson.tennisscoring.data.Points

data class Score(
    val points: Points = Points.ZERO,
    val wonGames: Int = 0,
    val wonSets: Int = 0,
    val tieBreakPoints: Int = 0,
) {
    fun isNotCleared() = !isCleared()

    fun isCleared() = points == Points.ZERO
            && wonGames == 0
            && wonSets == 0
}