package se.dennisgimbergsson.tennisscoreboard.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoreboard.dao.CurrentScoreDao
import se.dennisgimbergsson.tennisscoreboard.data.entities.CurrentScore
import se.dennisgimbergsson.tennisscoreboard.data.entities.TeamScore
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import javax.inject.Inject

interface ScoreDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard)
    fun getScoreboardData(): Flow<List<CurrentScore>>
}

class ScoreRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val currentScoreDao: CurrentScoreDao,
) : ScoreDataSource {

    override suspend fun putScoreboardData(
        scoreboard: Scoreboard,
    ) = withContext(dispatcherProvider.io()) {
        currentScoreDao.insertCurrentScore(
            currentScore = CurrentScore(
                awayTeamScore = mapToTeamScore(
                    score = scoreboard.awayScore,
                ),
                homeTeamScore = mapToTeamScore(
                    score = scoreboard.homeScore,
                )
            )
        )
    }

    private fun mapToTeamScore(
        score: Score,
    ) = TeamScore(
        points = score.points.name,
        games = score.wonGames,
        sets = score.wonSets
    )

    override fun getScoreboardData() = currentScoreDao.getAllScores()

}