package se.dennisgimbergsson.tennisscoreboard.ui.screens

import android.graphics.drawable.Drawable
import se.dennisgimbergsson.tennisscoreboard.data.Score

data class MainViewState(
    val homeScore: Score = Score(),
    val awayScore: Score = Score(),
    val scoreboardState: ScoreboardState = ScoreboardState(),
)

data class ScoreboardState(

    val homeGameScore: Int = 0,
    val homeGamesWon: Int = 0,
    val homeSetWon: Int = 0,

    val homeIcon: Drawable? = null,

    val awayGameScore: Int = 0,
    val awayGamesWon: Int = 0,
    val awaySetWon: Int = 0,

    val awayIcon: Drawable? = null,

    val homeScore: Score = Score(),
    val awayScore: Score = Score(),    val peekDrawer: Boolean = false,
)