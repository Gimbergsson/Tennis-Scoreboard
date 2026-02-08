package se.dennisgimbergsson.tennisscoring

import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

object Constants {

    object DummyData {
        val SCOREBOARD_HISTORY = mapOf(
            1 to arrayOf(
                Scoreboard(
                    homeScore = Score(
                        points = Points.ZERO,
                    ),
                    awayScore = Score(
                        points = Points.ZERO,
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                    ),
                    awayScore = Score(
                        points = Points.ZERO,
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                    ),
                    awayScore = Score(
                        points = Points.FIFTEEN,
                    )
                )
            ),
            2 to arrayOf(
                Scoreboard(
                    homeScore = Score(
                        points = Points.ZERO,
                    ),
                    awayScore = Score(
                        points = Points.ZERO,
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                    ),
                    awayScore = Score(
                        points = Points.ZERO,
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                    ),
                    awayScore = Score(
                        points = Points.FIFTEEN,
                    )
                )
            )
        )
    }

}