package se.dennisgimbergsson.tennisscoring.data

import androidx.annotation.StringRes
import se.dennisgimbergsson.tennisscoring.R

enum class Points(
    @get:StringRes val stringResource: Int,
    val integer: Int,
) {
    ZERO(R.string.score_0, 0),
    FIFTEEN(R.string.score_15, 15),
    THIRTY(R.string.score_30, 30),
    FORTY(R.string.score_40, 40),
    ADVANTAGE(R.string.score_advantage, 41);

    companion object {
        fun getFromScore(score: Int) = entries.firstOrNull { it.integer == score } ?: ZERO

        fun getByName(name: String): Points = entries.firstOrNull {
            it.name.equals(name, ignoreCase = true)
        } ?: ZERO
    }
}