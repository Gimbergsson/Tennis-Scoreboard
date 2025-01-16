package se.dennisgimbergsson.tennisscoreboard.ui.screens

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.data.models.Score
import se.dennisgimbergsson.shared.enums.GameScores.ADVANTAGE
import se.dennisgimbergsson.shared.enums.GameScores.FIFTEEN
import se.dennisgimbergsson.shared.enums.GameScores.FORTY
import se.dennisgimbergsson.shared.enums.GameScores.THIRTY
import se.dennisgimbergsson.shared.enums.GameScores.ZERO
import se.dennisgimbergsson.shared.enums.Teams
import se.dennisgimbergsson.shared.enums.Teams.AWAY
import se.dennisgimbergsson.shared.enums.Teams.HOME
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesDataSource
) : ReduxViewModel<MainViewState>(
    initialState = MainViewState()
), DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        saveScoreboard()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadScoreboard()
    }

    private fun loadScoreboard() = viewModelScope.launch {
        val scoreboard = sharedPreferencesRepository.getScoreboardData()
        setState {
            copy(scoreboard = scoreboard)
        }
    }

    private fun saveScoreboard() = viewModelScope.launch {
        sharedPreferencesRepository.putScoreboardData(
            scoreboard = currentState().scoreboard
        )
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
    ): Unit = with(currentState().scoreboard) {
        // Home
        val currentHomeWonSets = homeScore.wonSets
        var homeWonSets = currentHomeWonSets

        val currentHomeWonGames = homeScore.wonGames
        var homeWonGames = currentHomeWonGames

        val currentHomeGameScore = homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        var currentAwayWonSets = awayScore.wonSets
        var currentAwayWonGames = awayScore.wonGames
        var currentAwayGameScore = awayScore.gameScore

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
                                        currentAwayGameScore = FORTY
                                        FORTY
                                    }

                                    FORTY -> ADVANTAGE
                                    else -> {
                                        homeWonSets++
                                        homeWonGames = 0
                                        currentAwayWonGames = 0
                                        currentAwayGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                homeWonSets++
                                homeWonGames = 0
                                currentAwayWonGames = 0
                                currentAwayGameScore = ZERO
                                ZERO
                            }
                            else -> ZERO
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
                                        currentAwayGameScore = FORTY
                                        FORTY
                                    }

                                    FORTY -> ADVANTAGE
                                    else -> {
                                        homeWonGames++
                                        currentAwayGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                homeWonGames++
                                currentAwayGameScore = ZERO
                                ZERO
                            }
                            else -> ZERO
                        }
                    }
                }
            }

            AWAY -> {
                currentAwayGameScore = when {
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
                                        currentAwayWonSets++
                                        currentAwayWonGames = 0
                                        homeWonGames = 0
                                        homeGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                currentAwayWonSets++
                                currentAwayWonGames = 0
                                homeWonGames = 0
                                homeGameScore = ZERO
                                ZERO
                            }
                            else -> ZERO
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
                                        currentAwayWonGames++
                                        homeGameScore = ZERO
                                        ZERO
                                    }
                                }
                            }

                            ADVANTAGE -> {
                                currentAwayWonGames++
                                homeGameScore = ZERO
                                ZERO
                            }

                            else -> ZERO
                        }
                    }
                }
            }
        }

        result(
            Score(
                gameScore = homeGameScore,
                wonGames = homeWonGames,
                wonSets = homeWonSets,
            ),
            Score(
                gameScore = currentAwayGameScore,
                wonGames = currentAwayWonGames,
                wonSets = currentAwayWonSets,
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
    ): Unit = with(currentState().scoreboard) {
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
                    else -> ZERO
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
                    else -> ZERO
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

    companion object {
        private const val TAG = "MainViewModel"
        const val MOCK_ARG = "mock_arg"
    }
}