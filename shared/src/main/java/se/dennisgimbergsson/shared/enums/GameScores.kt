package se.dennisgimbergsson.shared.enums

import androidx.annotation.StringRes
import se.dennisgimbergsson.shared.R

enum class GameScores(
    @StringRes val stringResource: Int,
    val score: Int
) {
    ZERO(R.string.score_0, 0),
    FIFTEEN(R.string.score_15, 15),
    THIRTY(R.string.score_30, 30),
    FORTY(R.string.score_40, 40),
    ADVANTAGE(R.string.score_advantage, 41);

    companion object {
        fun getFromScore(score: Int) = entries.firstOrNull { it.score == score } ?: ZERO
    }
}