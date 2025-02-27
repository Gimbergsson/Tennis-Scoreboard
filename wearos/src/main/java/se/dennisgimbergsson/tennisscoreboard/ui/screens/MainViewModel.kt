package se.dennisgimbergsson.tennisscoreboard.ui.screens

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest.create
import com.google.android.gms.wearable.PutDataRequest
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.data.models.Score
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.enums.GameScores.ADVANTAGE
import se.dennisgimbergsson.shared.enums.GameScores.FIFTEEN
import se.dennisgimbergsson.shared.enums.GameScores.FORTY
import se.dennisgimbergsson.shared.enums.GameScores.THIRTY
import se.dennisgimbergsson.shared.enums.GameScores.ZERO
import se.dennisgimbergsson.shared.enums.Teams
import se.dennisgimbergsson.shared.enums.Teams.AWAY
import se.dennisgimbergsson.shared.enums.Teams.HOME
import se.dennisgimbergsson.shared.extensions.logWearMessage
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val wearableDataClient: DataClient,
    private val gson: Gson,
    private val sharedPreferencesRepository: SharedPreferencesDataSource
) : ReduxViewModel<MainViewState>(
    initialState = MainViewState()
), DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        saveScoreboard()
        saveScoreboardHistory()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadScoreboard()
        loadScoreboardHistory()
    }

    private fun saveScoreboard() = viewModelScope.launch {
        sharedPreferencesRepository.putScoreboardData(
            scoreboard = currentState().scoreboard
        )
    }

    private fun loadScoreboard() = viewModelScope.launch {
        val scoreboard = sharedPreferencesRepository.getScoreboardData()
        setState {
            copy(scoreboard = scoreboard)
        }
    }

    private fun saveScoreboardHistory() =
        viewModelScope.launch {
            sharedPreferencesRepository.putScoreboardHistoryData(
                scoreboardHistory = currentState().scoreboardHistory
            )
        }

    private fun loadScoreboardHistory() = viewModelScope.launch {
        val scoreboardHistory = sharedPreferencesRepository.getScoreboardHistoryData()
        setState {
            copy(scoreboardHistory = scoreboardHistory)
        }
    }

    fun updateScoreboard() = viewModelScope.launch {
        val json = gson.toJson(currentState().scoreboard)
        val putDataReq: PutDataRequest = create(Constants.Paths.SCOREBOARD_UPDATE).run {
            dataMap.putString(Constants.Keys.SCOREBOARD_UPDATE, json)
            asPutDataRequest()
        }.setUrgent()
        val putDataTask = wearableDataClient.putDataItem(putDataReq)
        putDataTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Task completed successfully
                logWearMessage(message = "Updating scoreboard: ${task.result.uri}")
            } else {
                // Task failed, handle the exception
                logWearMessage(
                    message = "Error updating scoreboard: ${task.exception?.message}",
                    exception = task.exception,
                )
            }
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

    fun incrementHome() = incrementScore(HOME)

    fun incrementAway() = incrementScore(AWAY)

    private fun incrementScore(team: Teams) =
        calculateIncrementedScore(team = team) { homeScore, awayScore ->
            viewModelScope.launchSetState {
                val scoreboard = scoreboard.copy(
                    homeScore = homeScore,
                    awayScore = awayScore,
                )
                val scoreboardHistory = scoreboardHistory.toMutableList()
                scoreboardHistory.add(scoreboard)
                copy(
                    scoreboard = scoreboard,
                    scoreboardHistory = scoreboardHistory,
                )
            }
        }

    fun decrementHome() = decrementScore(team = HOME)

    fun decrementAway() = decrementScore(team = AWAY)

    private fun decrementScore(team: Teams) =
        calculateDecrementedScore(team = team) { homeScore, awayScore ->
            viewModelScope.launchSetState {
                val scoreboard = scoreboard.copy(
                    homeScore = homeScore,
                    awayScore = awayScore,
                )
                val scoreboardHistory = scoreboardHistory.toMutableList()
                scoreboardHistory.add(scoreboard)
                copy(
                    scoreboard = scoreboard,
                    scoreboardHistory = scoreboardHistory,
                )
            }
        }

    private fun calculateDecrementedScore(
        team: Teams,
        result: (Score, Score) -> Unit
    ): Unit = with(currentState().scoreboard) {
        // Home
        var currentHomeWonSets = homeScore.wonSets
        var currentHomeWonGames = homeScore.wonGames
        val currentHomeGameScore = homeScore.gameScore
        var homeGameScore = currentHomeGameScore

        // Away
        var currentAwayWonSets = awayScore.wonSets
        var currentAwayWonGames = awayScore.wonGames
        val currentAwayGameScore = awayScore.gameScore
        var awayGameScore = currentAwayGameScore

        when (team) {
            HOME -> {
                homeGameScore = when (currentHomeGameScore) {
                    ZERO -> {
                        when {
                            currentHomeWonGames > 0 -> currentHomeWonGames--
                            currentHomeWonSets > 0 -> currentHomeWonSets--
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
                        when {
                            currentAwayWonGames > 0 -> currentAwayWonGames--
                            currentAwayWonSets > 0 -> currentAwayWonSets--
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
                wonGames = currentHomeWonGames,
                wonSets = currentHomeWonSets,
            ),
            Score(
                gameScore = awayGameScore,
                wonGames = currentAwayWonGames,
                wonSets = currentAwayWonSets,
            )
        )
    }

    fun clearScoreboard() = viewModelScope.launchSetState {
        copy(
            scoreboard = scoreboard.copy(
                homeScore = Score(),
                awayScore = Score(),
            ),
            scoreboardHistory = listOf(Scoreboard()),
        )
    }

    fun popScoreboardHistory() = viewModelScope.launch {
        val scoreboardHistory = currentState().scoreboardHistory
        if (scoreboardHistory.isEmpty() || scoreboardHistory.size == 1) return@launch

        val newScoreboardHistory = scoreboardHistory.dropLast(1)

        setState {
            copy(
                scoreboard = newScoreboardHistory.last(),
                scoreboardHistory = newScoreboardHistory,
            )
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
        const val MOCK_ARG = "mock_arg"
    }
}