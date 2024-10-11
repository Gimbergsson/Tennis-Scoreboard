package se.dennisgimbergsson.tennisscoreboard.ui.screens

import android.content.Context
import android.view.KeyEvent.KEYCODE_STEM_1
import android.view.KeyEvent.KEYCODE_STEM_2
import androidx.lifecycle.viewModelScope
import androidx.wear.input.WearableButtons
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import se.dennisgimbergsson.tennisscoreboard.data.GameScores.ADVANTAGE
import se.dennisgimbergsson.tennisscoreboard.data.GameScores.FIFTEEN
import se.dennisgimbergsson.tennisscoreboard.data.GameScores.FORTY
import se.dennisgimbergsson.tennisscoreboard.data.GameScores.THIRTY
import se.dennisgimbergsson.tennisscoreboard.data.GameScores.ZERO
import se.dennisgimbergsson.tennisscoreboard.data.Score
import se.dennisgimbergsson.tennisscoreboard.data.Teams
import se.dennisgimbergsson.tennisscoreboard.data.Teams.AWAY
import se.dennisgimbergsson.tennisscoreboard.data.Teams.HOME
import se.dennisgimbergsson.tennisscoreboard.utils.ReduxViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : ReduxViewModel<MainViewState>(MainViewState()) {

    init {
        val homeButton = WearableButtons.getButtonInfo(context, KEYCODE_STEM_1)
        val homeIcon = WearableButtons.getButtonIcon(context, homeButton?.keycode ?: -1)

        val awayButton = WearableButtons.getButtonInfo(context, KEYCODE_STEM_2)
        val awayIcon = WearableButtons.getButtonIcon(context, awayButton?.keycode ?: -1)

        viewModelScope.launchSetState {
            copy(
                scoreboardState = ScoreboardState(
                    homeIcon = homeIcon,
                    awayIcon = awayIcon
                )
            )
        }
    }

    private fun incrementScore(team: Teams) = viewModelScope.launchSetState {
        // Home
        val currentHomeWonGames = currentState().homeScore.wonGames
        var homeWonGames = currentHomeWonGames

        val currentHomeGameScore = currentState().homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        val currentAwayWonGames = currentState().awayScore.wonGames
        var awayWonGames = currentAwayWonGames

        val currentAwayGameScore = currentState().awayScore.gameScore
        var awayGameScore = currentAwayGameScore

        when (team) {
            HOME -> {
                homeGameScore = when (currentState().homeScore.gameScore) {
                    ZERO -> FIFTEEN
                    FIFTEEN -> THIRTY
                    THIRTY -> FORTY
                    FORTY -> {
                        when (currentAwayGameScore) {
                            ADVANTAGE -> {
                                awayGameScore = FORTY
                                FORTY
                            }

                            FORTY -> ADVANTAGE
                            else -> {
                                homeWonGames++
                                awayGameScore = ZERO
                                ZERO
                            }
                        }
                    }

                    ADVANTAGE -> {
                        homeWonGames++
                        awayGameScore = ZERO
                        ZERO
                    }
                }
            }

            AWAY -> {
                awayGameScore = when (currentAwayGameScore) {
                    ZERO -> FIFTEEN
                    FIFTEEN -> THIRTY
                    THIRTY -> FORTY
                    FORTY -> {
                        when (currentHomeGameScore) {
                            ADVANTAGE -> {
                                homeGameScore = FORTY
                                FORTY
                            }

                            FORTY -> ADVANTAGE
                            else -> {
                                awayWonGames++
                                homeGameScore = ZERO
                                ZERO
                            }
                        }
                    }

                    ADVANTAGE -> {
                        awayWonGames++
                        homeGameScore = ZERO
                        ZERO
                    }
                }
            }
        }
        val homeScore = Score(gameScore = homeGameScore, wonGames = homeWonGames)
        val awayScore = Score(gameScore = awayGameScore, awayWonGames)
        val scoreboardState = ScoreboardState(
            homeScore = homeScore,
            awayScore = awayScore,
            homeIcon = currentState().scoreboardState.homeIcon,
            awayIcon = currentState().scoreboardState.awayIcon,
        )
        copy(
            homeScore = homeScore,
            awayScore = awayScore,
            scoreboardState = scoreboardState
        )
    }

    private fun decrementScore(team: Teams) = viewModelScope.launchSetState {

        // Home
        var currentHomeWonGames = currentState().homeScore.wonGames
        val currentHomeGameScore = currentState().homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        var currentAwayWonGames = currentState().awayScore.wonGames
        val currentAwayGameScore = currentState().awayScore.gameScore
        var awayGameScore = currentAwayGameScore

        when (team) {
            HOME -> {
                homeGameScore = when (currentHomeGameScore) {
                    ZERO -> {
                        if (currentHomeWonGames > 0) {
                            currentHomeWonGames--
                        }
                        ZERO
                    }
                    FIFTEEN -> ZERO
                    THIRTY -> FIFTEEN
                    FORTY -> THIRTY
                    ADVANTAGE -> FORTY
                }
            }

            AWAY -> {
                awayGameScore = when (currentAwayGameScore) {
                    ZERO -> {
                        if (currentAwayWonGames > 0) {
                            currentAwayWonGames--
                        }
                        ZERO
                    }
                    FIFTEEN -> ZERO
                    THIRTY -> FIFTEEN
                    FORTY -> THIRTY
                    ADVANTAGE -> FORTY
                }
            }
        }
        val homeScore = Score(gameScore = homeGameScore, currentHomeWonGames)
        val awayScore = Score(gameScore = awayGameScore, currentAwayWonGames)

        val scoreboardState = ScoreboardState(
            homeScore = homeScore,
            awayScore = awayScore,
            homeIcon = currentState().scoreboardState.homeIcon,
            awayIcon = currentState().scoreboardState.awayIcon,
        )

        copy(
            homeScore = homeScore,
            awayScore = awayScore,
            scoreboardState = scoreboardState
        )
    }

    fun incrementHome() = incrementScore(HOME)

    fun incrementAway() = incrementScore(AWAY)

    fun clear() = viewModelScope.launchSetState {
        copy(
            homeScore = Score(),
            awayScore = Score(),
            scoreboardState = ScoreboardState(
                homeScore = Score(),
                awayScore = Score(),
                homeIcon = currentState().scoreboardState.homeIcon,
                awayIcon = currentState().scoreboardState.awayIcon,
            )
        )
    }

    fun decrementHome() = decrementScore(team = HOME)

    fun decrementAway() = decrementScore(team = AWAY)
}