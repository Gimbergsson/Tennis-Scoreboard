package se.dennisgimbergsson.tennisscoreboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.wearable.DataClient
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.enums.GameScores
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import se.dennisgimbergsson.tennisscoreboard.testutils.MainDispatcherRule
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewModel
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewModel.Companion.SCOREBOARD_HISTORY_MOCK

class MainViewModelTest {

    @get:Rule(order = 0)
    val testCoroutineRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val instantExecutorRule = InstantTaskExecutorRule()

    private val savedStateHandle: SavedStateHandle = mock()
    private val wearableDataClient: DataClient = mock()
    private val gson: Gson = mock()
    private val sharedPreferencesRepository: SharedPreferencesDataSource = mock()

    /**
     * Tests to increment/decrement home and away game score.
     */
    @Test
    fun `increment then decrement home game score`() = runTest {
        createViewModel().apply {
            incrementHome()
            // Check if game score has been incremented.
            assertEquals(GameScores.FIFTEEN, currentState().scoreboard.homeScore.gameScore)

            decrementHome()
            // Check if game score has been decremented.
            assertEquals(GameScores.ZERO, currentState().scoreboard.homeScore.gameScore)
        }
    }

    @Test
    fun `increment then decrement away game score`() = runTest {
        createViewModel().apply {
            incrementAway()
            // Check if game score has been incremented.
            assertEquals(GameScores.FIFTEEN, currentState().scoreboard.awayScore.gameScore)

            decrementAway()
            // Check if game score has been decremented.
            assertEquals(GameScores.ZERO, currentState().scoreboard.awayScore.gameScore)
        }
    }

    /**
     * Tests to increment/decrement home and away won games score.
     */
    @Test
    fun `increment up to a home won game then decrement it`() = runTest {
        createViewModel().apply {
            incrementHome()
            incrementHome()
            incrementHome()
            incrementHome()

            // Check if the won games has been incremented.
            assertEquals(ONE_WON_GAME, currentState().scoreboard.homeScore.wonGames)

            decrementHome()

            // Check if the won games has been decremented.
            assertEquals(ZERO_WON_GAME, currentState().scoreboard.homeScore.wonGames)

            // Check if game score is still at zero.
            assertEquals(GameScores.ZERO, currentState().scoreboard.homeScore.gameScore)
        }
    }

    @Test
    fun `increment up to a away won game then decrement it`() = runTest {
        createViewModel().apply {
            incrementAway()
            incrementAway()
            incrementAway()
            incrementAway()

            // Check if the won games has been incremented.
            assertEquals(ONE_WON_GAME, currentState().scoreboard.awayScore.wonGames)

            decrementAway()

            // Check if the won games has been decremented.
            assertEquals(ZERO_WON_GAME, currentState().scoreboard.awayScore.wonGames)

            // Check if game score is still at zero.
            assertEquals(GameScores.ZERO, currentState().scoreboard.awayScore.gameScore)
        }
    }

    /**
     * Test reverting the state of the scoreboard back to the previous state.
     */
    @Test
    fun `check that state is reverted`() = runTest {
        createViewModel().apply {
            incrementHome()
            incrementHome()
            incrementHome()
            incrementHome()

            incrementAway()
            incrementAway()
            incrementAway()

            // Check if the history size is correct
            assertEquals(8, currentState().scoreboardHistory.size)

            // Pop the last state
            revertLastScore()

            // Check if the history size is correct
            assertEquals(7, currentState().scoreboardHistory.size)

            // Check if the last game score is removed
            assertEquals(GameScores.THIRTY, currentState().scoreboard.awayScore.gameScore)
        }
    }


    @Test
    fun `check that state is not reverted if there is no history`() = runTest {
        createViewModel(
            mockEmptyHistory = true
        ).apply {
            assertEquals(0, currentState().scoreboardHistory.size)

            revertLastScore()

            assertEquals(0, currentState().scoreboardHistory.size)
        }
    }

    private suspend fun createViewModel(
        mockEmptyHistory: Boolean = false,
    ): MainViewModel {
        given(savedStateHandle.get<List<Scoreboard>>(SCOREBOARD_HISTORY_MOCK) ?: listOf(Scoreboard()))
            .willReturn(
                when {
                    mockEmptyHistory -> listOf()
                    else -> listOf(Scoreboard())
                }
            )

        given(sharedPreferencesRepository.getScoreboardHistoryData())
            .willReturn(
                when {
                    mockEmptyHistory -> listOf()
                    else -> listOf(Scoreboard())
                }
            )

        return MainViewModel(
            savedStateHandle = savedStateHandle,
            wearableDataClient = wearableDataClient,
            gson = gson,
            sharedPreferencesRepository = sharedPreferencesRepository,
        )
    }

    companion object {
        private const val ZERO_WON_GAME = 0
        private const val ONE_WON_GAME = 1
    }
}