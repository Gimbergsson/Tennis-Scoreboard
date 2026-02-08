package se.dennisgimbergsson.tennisscoreboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.wearable.DataClient
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import se.dennisgimbergsson.shared.MainDispatcherRule
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import se.dennisgimbergsson.tennisscoreboard.ui.screens.IncrementAway
import se.dennisgimbergsson.tennisscoreboard.ui.screens.IncrementHome
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewModel
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewModel.Companion.SCOREBOARD_HISTORY_MOCK
import se.dennisgimbergsson.tennisscoreboard.ui.screens.RevertCurrentScore
import se.dennisgimbergsson.tennisscoring.TennisScoreManagerImpl
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import se.dennisgimbergsson.tennisscoring.di.ScoreUpdateEvent
import se.dennisgimbergsson.tennisscoring.di.TennisScoringEvents


class MainViewModelTest {

    @get:Rule(order = 0)
    val testCoroutineRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val instantExecutorRule = InstantTaskExecutorRule()

    private val tennisScoringEvents: TennisScoringEvents = MutableSharedFlow()

    private val tennisScoreManager = TennisScoreManagerImpl(
        dispatcherProvider = testCoroutineRule.testDispatcherProvider,
        tennisScoringEvents = tennisScoringEvents
    )

    private val savedStateHandle: SavedStateHandle = mockk()
    private val wearableDataClient: DataClient = mockk()
    private val gson: Gson = mockk()
    private val sharedPreferencesRepository: SharedPreferencesDataSource = mockk()
    private val testCoroutineScope = CoroutineScope(testCoroutineRule.testDispatcherProvider.io())

    /**
     * Tests to increment/decrement home and away game score.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `increment home points and verify event`() = runTest {
        createViewModel().apply {
            val emittedScoreboard = mutableStateOf(Scoreboard())
            tennisScoringEvents
                .asSharedFlow()
                .onEach { event ->
                    when (event) {
                        is ScoreUpdateEvent -> {
                            emittedScoreboard.value = event.scoreboard
                        }
                    }
                }.launchIn(testCoroutineScope)
            onEvent(IncrementHome)

            // Check if points were incremented in emitted event
            assertEquals(Points.FIFTEEN, emittedScoreboard.value.homeScore.points)
        }
    }

    @Test
    fun `increment home points`() = runTest {
        createViewModel().apply {
            onEvent(IncrementHome)
            assertEquals(Points.FIFTEEN, currentState().scoreboardHistory.last().homeScore.points)
        }
    }

    @Test
    fun `increment away points`() = runTest {
        createViewModel().apply {
            onEvent(IncrementAway)
            assertEquals(Points.FIFTEEN, currentState().scoreboardHistory.last().awayScore.points)
        }
    }

    /**
     * Tests to increment/decrement home and away won games score.
     */
    @Test
    fun `increment up to a home won game`() = runTest {
        createViewModel().apply {
            repeat(4) {
                onEvent(IncrementHome)
            }

            // Check if the won games has been incremented.
            assertEquals(ONE_WON_GAME, currentState().scoreboardHistory.last().homeScore.wonGames)
        }
    }

    @Test
    fun `increment up to a away won game`() = runTest {
        createViewModel().apply {
            repeat(4) {
                onEvent(IncrementAway)
            }

            // Check if the won games has been incremented.
            assertEquals(ONE_WON_GAME, currentState().scoreboardHistory.last().awayScore.wonGames)
        }
    }

    /**
     * Test reverting the state of the scoreboard back to the previous state.
     */
    @Test
    fun `check that state is reverted`() = runTest {
        createViewModel().apply {
            repeat(4) {
                tennisScoreManager.incrementHomeTeamScore()
            }

            repeat(3) {
                tennisScoreManager.incrementAwayTeamScore()
            }

            // Check if the history size is correct
            assertEquals(8, currentState().scoreboardHistory.size)

            // Pop the last state
            onEvent(RevertCurrentScore)

            // Check if the history size is correct
            assertEquals(7, currentState().scoreboardHistory.size)

            // Check if the last game score is removed
            assertEquals(Points.THIRTY, currentState().scoreboardHistory.last().awayScore.points)
        }
    }


    @Test
    fun `check that state is not reverted if there is no history`() = runTest {
        createViewModel(
            mockEmptyHistory = true
        ).apply {
            assertEquals(0, currentState().scoreboardHistory.size)

            onEvent(RevertCurrentScore)

            assertEquals(0, currentState().scoreboardHistory.size)
        }
    }

    private fun createViewModel(
        mockEmptyHistory: Boolean = false,
    ): MainViewModel {
        every {
            savedStateHandle.get<List<Scoreboard>>(SCOREBOARD_HISTORY_MOCK) ?: listOf(Scoreboard())
        } returns when {
            mockEmptyHistory -> listOf()
            else -> listOf(Scoreboard())
        }

        coEvery {
            sharedPreferencesRepository.getScoreboardHistoryData()
        } returns when {
            mockEmptyHistory -> listOf()
            else -> listOf(Scoreboard())
        }

        return MainViewModel(
            tennisScoreManager = tennisScoreManager,
            tennisScoringEvents = tennisScoringEvents,
            wearableDataClient = wearableDataClient,
            gson = gson,
            sharedPreferencesRepository = sharedPreferencesRepository,
            savedStateHandle = savedStateHandle,
            dispatcherProvider = testCoroutineRule.testDispatcherProvider
        )
    }

    companion object {
        private const val ONE_WON_GAME = 1
    }
}