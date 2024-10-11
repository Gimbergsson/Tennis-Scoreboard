package se.dennisgimbergsson.tennisscoreboard.data.models

import android.graphics.drawable.Drawable

data class Scoreboard(
    val homeScore: Score = Score(),
    val awayScore: Score = Score(),

    val homeIcon: Drawable? = null,
    val awayIcon: Drawable? = null,

    val peekDrawer: Boolean = false,
)