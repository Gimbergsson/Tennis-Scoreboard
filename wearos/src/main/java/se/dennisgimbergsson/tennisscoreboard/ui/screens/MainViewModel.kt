package se.dennisgimbergsson.tennisscoreboard.ui.screens

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.extensions.logWearMessage
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.shared.utils.debouncedActiveJob
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import se.dennisgimbergsson.tennisscoring.TennisScoreManager
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import se.dennisgimbergsson.tennisscoring.di.ScoreUpdateEvent
import se.dennisgimbergsson.tennisscoring.di.TennisScoringEvents
import javax.inject.Inject

sealed interface MainUiEvent
object IncrementHome : MainUiEvent
object IncrementAway : MainUiEvent
object ResetScoreboard : MainUiEvent
object RevertCurrentScore : MainUiEvent
object SaveAndSyncScoreboards : MainUiEvent

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tennisScoreManager: TennisScoreManager,
    private val tennisScoringEvents: TennisScoringEvents,
    private val wearableDataClient: DataClient,
    private val gson: Gson,
    private val sharedPreferencesRepository: SharedPreferencesDataSource,
    private val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) : ReduxViewModel<MainViewState>(
    initialState = MainViewState(
        scoreboardHistory = savedStateHandle.get<List<Scoreboard>>(SCOREBOARD_HISTORY_MOCK) ?: listOf(Scoreboard())
    )
), DefaultLifecycleObserver {

    private val incrementHomeScore = debouncedActiveJob(viewModelScope) {
        tennisScoreManager.incrementHomeTeamScore()
    }

    private val incrementAwayScore = debouncedActiveJob(viewModelScope) {
        tennisScoreManager.incrementAwayTeamScore()
    }

    /**
     * Saves the current scoreboard and waits until finished then syncs the saved scoreboards
     */
    private val saveAndSyncMatchesJob = debouncedActiveJob(viewModelScope) {
        syncSavedScoreboards()
    }

    init {
        subscribeToScoreEvents()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        saveScoreboardHistory()
        super.onDestroy(owner)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        //loadCurrentScoreboard()
        loadScoreboardHistory()
    }

    fun onEvent(event: MainUiEvent) =
        when (event) {
            IncrementHome -> incrementHomeScore()
            IncrementAway -> incrementAwayScore()
            ResetScoreboard -> resetScoreboardState()
            RevertCurrentScore -> revertToPreviousScore()
            SaveAndSyncScoreboards -> saveAndSyncMatchesJob()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeToScoreEvents() {
        tennisScoringEvents.asSharedFlow()
            .onEach { event ->
            when (event) {
                is ScoreUpdateEvent -> {
                    val scoreboard = event.scoreboard
                    val scoreboardHistory = when {
                        event.addToHistory -> {
                            currentState().scoreboardHistory.toMutableList().apply {
                                add(scoreboard)
                            }
                        }

                        else -> {
                            currentState().scoreboardHistory
                            //listOf(scoreboard)
                        }
                    }

                    setState {
                        copy(scoreboardHistory = scoreboardHistory)
                    }
                    updateAndroidAppScoreboard(scoreboard)
                }
            }
            //tennisScoringEvents.resetReplayCache()
        }.launchIn(viewModelScope)
    }

    private fun loadCurrentScoreboard() = viewModelScope.launch {
        val loadedScoreboard = sharedPreferencesRepository.getScoreboardData()
        tennisScoreManager.setScoreboard(
            scoreboard = loadedScoreboard,
            addToHistory = false,
        )
    }

    private fun saveScoreboardHistory() =
        viewModelScope.launch(dispatcherProvider.io() + NonCancellable) {
            sharedPreferencesRepository.putScoreboardHistoryData(
                scoreboardHistory = currentState().scoreboardHistory
            )
        }

    private fun loadScoreboardHistory() = viewModelScope.launch {
        val scoreboardHistory = sharedPreferencesRepository.getScoreboardHistoryData()
        tennisScoreManager.setScoreboard(
            scoreboard = scoreboardHistory.last(),
            addToHistory = false,
        )
        setState {
            copy(scoreboardHistory = scoreboardHistory)
        }
    }

    // TODO fix so you can store multiple scoreboard histories and then sync all of them.
    //  Right now it takes the current scoreboard history and syncs it with the android app
    private fun syncSavedScoreboards() = viewModelScope.launch {
        // Do check if a test is running, PutDataMapRequest will cause it to crash.
        if (isRunningInTest()) return@launch

        val scoreboardHistory = currentState().scoreboardHistory
        val json = gson.toJson(scoreboardHistory)
        val putDataReq: PutDataRequest? =
            PutDataMapRequest.create(Constants.Paths.SAVED_SCOREBOARDS_UPDATE).run {
                dataMap.putString(Constants.Keys.SAVED_SCOREBOARDS, json)
                asPutDataRequest()
            }.setUrgent()

        // Do null check, it can be null during test. This is a temporary workaround.
        putDataReq ?: return@launch

        val putDataTask = wearableDataClient.putDataItem(putDataReq)
        putDataTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Task completed successfully
                logWearMessage(message = "Updating saved scoreboards: ${task.result.uri}")
            } else {
                // Task failed, handle the exception
                logWearMessage(
                    message = "Error updating saved scoreboards: ${task.exception?.message}",
                    exception = task.exception,
                )
            }
        }
    }

    /**
     * Clears the scoreboard and scoreboard history.
     */
    private fun resetScoreboardState() {
        viewModelScope.launch {
            setState {
                copy(scoreboardHistory = listOf(Scoreboard()))
            }
        }
        tennisScoreManager.resetMatch()
    }

    /**
     * Takes the last scoreboard from the scoreboard history and sets it as the current scoreboard.
     */
    private fun revertToPreviousScore() {
        val currentHistory = currentState().scoreboardHistory
        when {
            currentHistory.size < 2 -> return
            else -> {
                val scoreboardHistory =  currentHistory.dropLast(1)
                viewModelScope.launch {
                    setState {
                        copy(scoreboardHistory = scoreboardHistory)
                    }
                }
                tennisScoreManager.setScoreboard(
                    scoreboard = scoreboardHistory.last(),
                    addToHistory = false,
                )
            }
        }
    }

    /**
     * Updates the Android app with the current scoreboard.
     * @param scoreboard The scoreboard to update the Android app with.
     */
    private fun updateAndroidAppScoreboard(scoreboard: Scoreboard) {
        // Do check if a test is running, PutDataMapRequest will cause it to crash.
        if (isRunningInTest()) return

        val json = gson.toJson(scoreboard)
        val putDataReq = PutDataMapRequest.create(Constants.Paths.SCOREBOARD_UPDATE).run {
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

    private fun isRunningInTest() =
        try {
            Class.forName("org.junit.Test") // Example, adjust based on your test framework
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Running in production environment", e)
            false
        }

    companion object {
        private const val TAG = "MainViewModel"
        const val SCOREBOARD_HISTORY_MOCK = "scoreboard_history_mock"
    }
}