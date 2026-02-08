package se.dennisgimbergsson.tennisscoreboard.ui.screens.scoreboard

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.data.repositories.ScoreRepository
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import javax.inject.Inject

data class ScoreboardState(
    val homeTeamScore: Score = Score(),
    val awayTeamScore: Score = Score(),
)

@HiltViewModel
class ScoreboardViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
) : ReduxViewModel<ScoreboardState>(
    initialState = ScoreboardState()
) {
    init {
        viewModelScope.launch {
            scoreRepository.getScoreboardData()
                .collect {
                    val scoreboard = it.firstOrNull() ?: return@collect

                    setState {
                        ScoreboardState(
                            homeTeamScore = Score(
                                points = Points.getByName(
                                    name = scoreboard.homeTeamScore?.points ?: Points.ZERO.name
                                ),
                                wonGames = scoreboard.homeTeamScore?.games ?: 0,
                                wonSets = scoreboard.homeTeamScore?.sets ?: 0
                            ),
                            awayTeamScore = Score(
                                points = Points.getByName(
                                    name = scoreboard.awayTeamScore?.points ?: Points.ZERO.name
                                ),
                                wonGames = scoreboard.awayTeamScore?.games ?: 0,
                                wonSets = scoreboard.awayTeamScore?.sets ?: 0
                            )
                        )
                    }

                    logAndroidMessage("room score updated: $scoreboard")
                }
        }
    }
}