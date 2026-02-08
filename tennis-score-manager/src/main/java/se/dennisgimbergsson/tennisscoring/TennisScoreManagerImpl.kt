package se.dennisgimbergsson.tennisscoring

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import se.dennisgimbergsson.tennisscoring.di.ScoreUpdateEvent
import se.dennisgimbergsson.tennisscoring.di.TennisScoringEvents
import javax.inject.Inject

interface TennisScoreManager {
    fun incrementHomeTeamScore()
    fun incrementAwayTeamScore()
    fun setScoreboard(
        scoreboard: Scoreboard,
        addToHistory: Boolean = true,
    )

    fun resetPoints()
    fun resetGames()
    fun resetTiebreakPoints()
    fun resetMatch()
}

class TennisScoreManagerImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val tennisScoringEvents: TennisScoringEvents,
) : TennisScoreManager {

    private var state = TennisMatchState()

    override fun setScoreboard(
        scoreboard: Scoreboard,
        addToHistory: Boolean,
    ) {
        state = TennisMatchState.fromScoreboard(scoreboard)
        emitScoreboardUpdate(addToHistory = addToHistory)
    }

    override fun incrementHomeTeamScore() {
        updateScore { it.homeTeamScores() }
    }

    override fun incrementAwayTeamScore() {
        updateScore { it.awayTeamScores() }
    }

    override fun resetPoints() {
        state = state.copy(homeTeamPointsIndex = 0, awayTeamPointsIndex = 0)
    }

    override fun resetGames() {
        state = state.copy(homeTeamGames = 0, awayTeamGames = 0)
    }

    override fun resetTiebreakPoints() {
        state = state.copy(homeTeamTiebreakPoints = 0, awayTeamTiebreakPoints = 0)
    }

    override fun resetMatch() {
        state = TennisMatchState()
        emitScoreboardUpdate(addToHistory = false)
    }

    private fun updateScore(scoreUpdater: (TennisMatchState) -> TennisMatchState) {
        state = scoreUpdater(state)
        emitScoreboardUpdate()
    }

    private fun emitScoreboardUpdate(addToHistory: Boolean = true) =
        CoroutineScope(dispatcherProvider.io()).launch {
            tennisScoringEvents.emit(
                ScoreUpdateEvent(
                    scoreboard = state.toScoreboard(),
                    addToHistory = addToHistory
                )
            )
        }
}

private data class TennisMatchState(
    val matchStartedAt: Long = 0L,
    val homeTeamPointsIndex: Int = 0,
    val awayTeamPointsIndex: Int = 0,
    val homeTeamGames: Int = 0,
    val awayTeamGames: Int = 0,
    val homeTeamSets: Int = 0,
    val awayTeamSets: Int = 0,
    val homeTeamTiebreakPoints: Int = 0,
    val awayTeamTiebreakPoints: Int = 0,
    val inTiebreak: Boolean = false,
) {
    private val points = Points.entries

    private val homeTeamScore
        get() = Score(
            points = points[homeTeamPointsIndex],
            wonGames = homeTeamGames,
            wonSets = homeTeamSets,
            tieBreakPoints = homeTeamTiebreakPoints
        )

    private val awayTeamScore
        get() = Score(
            points = points[awayTeamPointsIndex],
            wonGames = awayTeamGames,
            wonSets = awayTeamSets,
            tieBreakPoints = awayTeamTiebreakPoints
        )

    fun toScoreboard(): Scoreboard =
        Scoreboard(
            matchStartedAt = matchStartedAt,
            homeScore = homeTeamScore,
            awayScore = awayTeamScore,
            isTieBreak = inTiebreak,
        )

    fun homeTeamScores(): TennisMatchState = updateScore(true)

    fun awayTeamScores(): TennisMatchState = updateScore(false)

    private fun updateScore(isHomeTeamScoring: Boolean): TennisMatchState {
        var newState = this.copy(
            matchStartedAt = when (matchStartedAt) {
                0L -> System.currentTimeMillis()
                else -> matchStartedAt
            }
        )

        newState = when {
            newState.inTiebreak -> {
                newState.checkTiebreakWon(isHomeTeamScoring)
            }

            else -> {
                newState.updateRegularScore(isHomeTeamScoring)
            }
        }

        return newState.checkGameWon().checkSetWon()
    }

    private fun updateRegularScore(isHomeTeamScoring: Boolean): TennisMatchState {
        var newHomePointsIndex = homeTeamPointsIndex
        var newAwayPointsIndex = awayTeamPointsIndex

        if (isHomeTeamScoring) {
            when {
                newHomePointsIndex == 3 && newAwayPointsIndex == 3 -> newHomePointsIndex++ // Deuce
                newHomePointsIndex == 3 && newAwayPointsIndex == 4 -> newAwayPointsIndex-- // Advantage Away
                else -> newHomePointsIndex++
            }
        } else {
            when {
                newAwayPointsIndex == 3 && newHomePointsIndex == 3 -> newAwayPointsIndex++ // Deuce
                newAwayPointsIndex == 3 && newHomePointsIndex == 4 -> newHomePointsIndex-- // Advantage Home
                else -> newAwayPointsIndex++
            }
        }

        return this.copy(
            homeTeamPointsIndex = newHomePointsIndex,
            awayTeamPointsIndex = newAwayPointsIndex
        )
    }

    private fun checkGameWon(): TennisMatchState {
        return when {
            homeTeamPointsIndex >= 4 && homeTeamPointsIndex >= awayTeamPointsIndex + 2 -> homeTeamWinsGame()
            awayTeamPointsIndex >= 4 && awayTeamPointsIndex >= homeTeamPointsIndex + 2 -> awayTeamWinsGame()
            else -> this
        }
    }

    private fun homeTeamWinsGame(): TennisMatchState = this.copy(
        homeTeamGames = homeTeamGames + 1,
        homeTeamPointsIndex = 0,
        awayTeamPointsIndex = 0
    )

    private fun awayTeamWinsGame(): TennisMatchState = this.copy(
        awayTeamGames = awayTeamGames + 1,
        homeTeamPointsIndex = 0,
        awayTeamPointsIndex = 0
    )

    private fun checkSetWon(): TennisMatchState {
        return when {
            homeTeamGames >= 6 && homeTeamGames - awayTeamGames >= 2 -> homeTeamWinsSet()
            awayTeamGames >= 6 && awayTeamGames - homeTeamGames >= 2 -> awayTeamWinsSet()
            homeTeamGames == 6 && awayTeamGames == 6 -> startTiebreak()
            else -> this
        }
    }

    private fun homeTeamWinsSet(): TennisMatchState = this.copy(
        homeTeamSets = homeTeamSets + 1,
        homeTeamGames = 0,
        awayTeamGames = 0,
        inTiebreak = false,
        homeTeamTiebreakPoints = 0,
        awayTeamTiebreakPoints = 0
    )

    private fun awayTeamWinsSet(): TennisMatchState = this.copy(
        awayTeamSets = awayTeamSets + 1,
        homeTeamGames = 0,
        awayTeamGames = 0,
        inTiebreak = false,
        homeTeamTiebreakPoints = 0,
        awayTeamTiebreakPoints = 0
    )

    private fun startTiebreak() = this.copy(inTiebreak = true)

    private fun checkTiebreakWon(isHomeTeamScoring: Boolean): TennisMatchState {
        val newHomeTiebreakPoints = when {
            isHomeTeamScoring -> homeTeamTiebreakPoints.inc()
            else -> homeTeamTiebreakPoints
        }

        val newAwayTiebreakPoints = when {
            !isHomeTeamScoring -> awayTeamTiebreakPoints.inc()
            else -> awayTeamTiebreakPoints
        }

        return when {
            newHomeTiebreakPoints >= 7
                    && newHomeTiebreakPoints - newAwayTiebreakPoints >= 2 -> {
                homeTeamWinsSet()
            }

            newAwayTiebreakPoints >= 7
                    && newAwayTiebreakPoints - newHomeTiebreakPoints >= 2 -> {
                awayTeamWinsSet()
            }

            else -> this.copy(
                homeTeamTiebreakPoints = newHomeTiebreakPoints,
                awayTeamTiebreakPoints = newAwayTiebreakPoints
            )
        }
    }

    companion object {
        fun fromScoreboard(scoreboard: Scoreboard): TennisMatchState {
            val home = scoreboard.homeScore
            val away = scoreboard.awayScore

            return TennisMatchState(
                matchStartedAt = scoreboard.matchStartedAt,
                homeTeamPointsIndex = home.points.ordinal,
                awayTeamPointsIndex = away.points.ordinal,
                homeTeamGames = home.wonGames,
                awayTeamGames = away.wonGames,
                homeTeamSets = home.wonSets,
                awayTeamSets = away.wonSets,
                homeTeamTiebreakPoints = home.tieBreakPoints,
                awayTeamTiebreakPoints = away.tieBreakPoints,
                inTiebreak = scoreboard.isTieBreak || (home.wonGames == 6 && away.wonGames == 6)
            )
        }
    }
}