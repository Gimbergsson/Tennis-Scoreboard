package se.dennisgimbergsson.shared.data.models

import se.dennisgimbergsson.shared.enums.GameScores
import se.dennisgimbergsson.shared.enums.GameScores.ZERO

data class Score(
    var gameScore: GameScores = ZERO,
    var wonGames: Int = 0,
    var wonSets: Int = 0
) {
    fun isNotCleared() = !isCleared()

    fun isCleared() = gameScore == ZERO
            && wonGames == 0
            && wonSets == 0
}