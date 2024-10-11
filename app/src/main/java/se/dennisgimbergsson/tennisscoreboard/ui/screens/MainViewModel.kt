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
import se.dennisgimbergsson.tennisscoreboard.data.Scoreboard
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
                scoreboard = Scoreboard(
                    homeIcon = homeIcon,
                    awayIcon = awayIcon
                )
            )
        }
    }

    private fun incrementScore(team: Teams) =
        calculateIncrementedScore(team = team) { homeScore, awayScore ->
            viewModelScope.launchSetState {
                copy(
                    scoreboard = scoreboard.copy(
                        homeScore = homeScore,
                        awayScore = awayScore,
                    )
                )
            }
        }

    private fun calculateIncrementedScore(
        team: Teams,
        result: (Score, Score) -> Unit
    ) = with(currentState().scoreboard) {
        // Home
        val currentHomeWonSets = homeScore.wonSets
        var homeWonSets = currentHomeWonSets

        val currentHomeWonGames = homeScore.wonGames
        var homeWonGames = currentHomeWonGames

        val currentHomeGameScore = homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        val currentAwayWonSets = awayScore.wonSets
        var awayWonSets = currentAwayWonSets

        val currentAwayWonGames = awayScore.wonGames
        var awayWonGames = currentAwayWonGames

        val currentAwayGameScore = awayScore.gameScore
        var awayGameScore = currentAwayGameScore

        when (team) {
            HOME -> {
                homeGameScore = when {
                    currentHomeWonGames == 2 -> {
                        when (currentHomeGameScore) {
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
                                        homeWonSets++
                                        homeWonGames = 0
                                        awayWonGames = 0
                                        awayGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                homeWonSets++
                                homeWonGames = 0
                                awayWonGames = 0
                                awayGameScore = ZERO
                                ZERO
                            }
                        }
                    }

                    else -> {
                        when (currentHomeGameScore) {
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
                }
            }

            AWAY -> {
                awayGameScore = when {
                    currentAwayWonGames == 2 -> {
                        when (currentAwayGameScore) {
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
                                        awayWonSets++
                                        awayWonGames = 0
                                        homeWonGames = 0
                                        homeGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                awayWonSets++
                                awayWonGames = 0
                                homeWonGames = 0
                                homeGameScore = ZERO
                                ZERO
                            }
                        }
                    }

                    else -> {
                        when (currentAwayGameScore) {
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

                /*awayGameScore = when (currentAwayGameScore) {
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
                }*/
            }
        }

        result(
            Score(
                gameScore = homeGameScore,
                wonGames = homeWonGames,
                wonSets = homeWonSets,
            ),
            Score(
                gameScore = awayGameScore,
                wonGames = awayWonGames,
                wonSets = awayWonSets,
            )
        )
    }

    private fun decrementScore(team: Teams) =
        calculateDecrementedScore(team = team) { homeScore, awayScore ->
            viewModelScope.launchSetState {
                copy(
                    scoreboard = scoreboard.copy(
                        homeScore = homeScore,
                        awayScore = awayScore,
                    )
                )
            }
        }

    private fun calculateDecrementedScore(
        team: Teams,
        result: (Score, Score) -> Unit
    ) = with(currentState().scoreboard) {
        // Home
        var currentHomeWonGames = homeScore.wonGames
        val currentHomeGameScore = homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        var currentAwayWonGames = awayScore.wonGames
        val currentAwayGameScore = awayScore.gameScore
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

        result(
            Score(
                gameScore = homeGameScore,
                wonGames = currentHomeWonGames
            ),
            Score(
                gameScore = awayGameScore,
                wonGames = currentAwayWonGames
            )
        )
    }

    fun incrementHome() = incrementScore(HOME)

    fun incrementAway() = incrementScore(AWAY)

    fun clear() = viewModelScope.launchSetState {
        copy(
            scoreboard = scoreboard.copy(
                homeScore = Score(),
                awayScore = Score(),
            )
        )
    }

    fun decrementHome() = decrementScore(team = HOME)

    fun decrementAway() = decrementScore(team = AWAY)
}