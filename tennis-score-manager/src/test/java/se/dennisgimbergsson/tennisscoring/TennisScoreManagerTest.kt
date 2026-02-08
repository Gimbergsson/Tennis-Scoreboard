package se.dennisgimbergsson.tennisscoring

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
import se.dennisgimbergsson.tennisscoring.data.Points.ADVANTAGE
import se.dennisgimbergsson.tennisscoring.data.Points.FIFTEEN
import se.dennisgimbergsson.tennisscoring.data.Points.FORTY
import se.dennisgimbergsson.tennisscoring.data.Points.THIRTY
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import se.dennisgimbergsson.tennisscoring.di.ScoreUpdateEvent
import se.dennisgimbergsson.tennisscoring.di.TennisScoringEvent

class TennisScoreManagerTest {

    @get:Rule(order = 0)
    val testCoroutineRule = MainDispatcherRule()

    private val testCoroutineScope = CoroutineScope(testCoroutineRule.testDispatcher)

    private val _tennisScoringEvents = MutableSharedFlow<TennisScoringEvent>()
    private val tennisScoringEvents = _tennisScoringEvents.asSharedFlow()

    private val tennisScoring: TennisScoreManager = TennisScoreManagerImpl(
        dispatcherProvider = testCoroutineRule.testDispatcherProvider,
        tennisScoringEvents = _tennisScoringEvents
    )

    /**
     * Increment home points.
     * Home: 15 -> 30 -> 40
     */
    @Test
    fun `increment home points`() = runTest(testCoroutineRule.testDispatcher) {
        val points = mutableListOf(FIFTEEN, THIRTY, FORTY)

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(points[0], event.scoreboard.homeScore.points)
            points.removeAt(0)
        }

        repeat(3) {
            tennisScoring.incrementHomeTeamScore()
        }
    }

    /**
     * Increment away points.
     * Away: 15 -> 30 -> 40
     */
    @Test
    fun `increment away points`() = runTest(testCoroutineRule.testDispatcher) {
        val points = mutableListOf(FIFTEEN, THIRTY, FORTY)

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(points[0], event.scoreboard.awayScore.points)
            points.removeAt(0)
        }

        repeat(3) {
            tennisScoring.incrementAwayTeamScore()
        }
    }

    /**
     * Increment home points to a game.
     * Home: 15 -> 30 -> 40 -> Game
     */
    @Test
    fun `increment home points to won game`() = runTest(testCoroutineRule.testDispatcher) {
        // Mock scoreboard
        tennisScoring.setScoreboard(
            scoreboard = Scoreboard(homeScore = Score(points = FORTY))
        )

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(1, event.scoreboard.homeScore.wonGames)
        }

        tennisScoring.incrementHomeTeamScore()
    }

    /**
     * Increment away points to a game.
     * Away: 15 -> 30 -> 40 -> Game
     */
    @Test
    fun `increment away points to won game`() = runTest(testCoroutineRule.testDispatcher) {
        // Mock scoreboard
        tennisScoring.setScoreboard(
            scoreboard = Scoreboard(awayScore = Score(points = FORTY))
        )

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(1, event.scoreboard.awayScore.wonGames)
        }

        tennisScoring.incrementAwayTeamScore()
    }

    /**
     * Increment home points during a deuce.
     * Home: 15 -> 30 -> 40 -> Adv.
     * Away: 15 -> 30 -> 40
     */
    @Test
    fun `increment home points during deuce`() = runTest(testCoroutineRule.testDispatcher) {
        // Mock scoreboard
        tennisScoring.setScoreboard(
            scoreboard = Scoreboard(
                homeScore = Score(points = FORTY),
                awayScore = Score(points = FORTY)
            )
        )

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(ADVANTAGE, event.scoreboard.homeScore.points)
        }

        tennisScoring.incrementHomeTeamScore()
    }

    /**
     * Increment home points during advantage.
     * Home: 15 -> 30 -> 40 -> Adv. -> Game
     * Away: 15 -> 30 -> 40
     */
    @Test
    fun `increment home points during advantage`() = runTest(testCoroutineRule.testDispatcher) {
        // Mock scoreboard
        tennisScoring.setScoreboard(
            scoreboard = Scoreboard(
                homeScore = Score(points = ADVANTAGE),
                awayScore = Score(points = FORTY)
            )
        )

        subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
            assertEquals(1, event.scoreboard.homeScore.wonGames)
        }

        tennisScoring.incrementHomeTeamScore()
    }

    /**
     * Increment away points during home advantage.
     * Home: 15 -> 30 -> 40 <- Adv.
     * Away: 15 -> 30 -> 40
     */
    @Test
    fun `increment away points during home advantage`() =
        runTest(testCoroutineRule.testDispatcher) {
            // Mock scoreboard
            tennisScoring.setScoreboard(
                scoreboard = Scoreboard(
                    homeScore = Score(points = ADVANTAGE),
                    awayScore = Score(points = FORTY)
                )
            )

            subscribeToTennisScoringEvents<ScoreUpdateEvent> { event ->
                assertEquals(FORTY, event.scoreboard.homeScore.points)
            }

            tennisScoring.incrementAwayTeamScore()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private inline fun <reified T> subscribeToTennisScoringEvents(noinline result: (T) -> Unit) {
        tennisScoringEvents.onEach { event ->
            if (event !is T) return@onEach
            when (event) {
                is ScoreUpdateEvent -> result(event)
            }
            _tennisScoringEvents.resetReplayCache()
        }.launchIn(testCoroutineScope)
    }
}