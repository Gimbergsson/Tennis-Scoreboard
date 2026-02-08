package se.dennisgimbergsson.tennisscoreboard.ui.screens.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.Colors
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoreboard.ui.compose.views.MatchHistoryCard
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score

@AndroidEntryPoint
class MatchFragment : Fragment() {

    private val viewModel: MatchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setContent {
            //SideEffects()
            TennisScoreboardTheme {
                val state =
                    viewModel.stateFlow.collectAsStateWithLifecycle(initialValue = MatchState()).value

                LazyColumn(
                    modifier = Modifier
                        .background(color = MaterialTheme.Colors.background)
                        .fillMaxSize(),
                ) {
                    stickyHeader {
                        MatchHistoryCard(
                            scoreboard = state.match,
                            topContent = {
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    text = "Final score"
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = "Started at:"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = state.matchCreatedAt)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = "Deuce count:"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${state.deuceCount}")
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    itemsIndexed(
                        items = state.matchHistory,
                        key = { index, _ -> "$index" },
                    ) { index, item ->
                        val previousItem = state.matchHistory.getOrNull(index - 1)

                        val currentHomeScore = item.homeScore
                        val currentHomePoints = currentHomeScore.points
                        val currentHomeGames = currentHomeScore.wonGames
                        val prevHomeScore = previousItem?.homeScore ?: Score()
                        val prevHomePoints = prevHomeScore.points
                        val prevHomeGames = prevHomeScore.wonGames

                        val currentAwayScore = item.awayScore
                        val currentAwayPoints = currentAwayScore.points
                        val currentAwayGames = currentAwayScore.wonGames
                        val prevAwayScore = previousItem?.awayScore ?: Score()
                        val prevAwayPoints = prevAwayScore.points
                        val prevAwayGames = prevAwayScore.wonGames

                        val homeWonPoint = currentHomePoints == Points.ZERO
                                || currentHomePoints.integer > prevHomePoints.integer
                        val homeWonGame =
                            (prevHomePoints == Points.ADVANTAGE || prevHomePoints == Points.FORTY)
                                    && currentHomePoints == Points.ZERO
                                    && currentHomeGames > prevHomeGames

                        val awayWonPoint = currentAwayPoints == Points.ZERO
                                || currentAwayPoints.integer > prevAwayPoints.integer
                        val awayWonGame =
                            (prevAwayPoints == Points.ADVANTAGE || prevAwayPoints == Points.FORTY)
                                    && currentAwayPoints == Points.ZERO
                                    && currentAwayGames > prevAwayGames

                        val isDeuce =
                            currentHomePoints == Points.FORTY && currentAwayPoints == Points.FORTY

                        MatchHistoryCard(
                            pointWon = homeWonPoint || awayWonPoint || isDeuce,
                            gameWon = homeWonGame || awayWonGame,
                            setWon = false,
                            scoreboard = item,
                            topContent = {
                                when {
                                    isDeuce -> {
                                        Text(
                                            fontWeight = FontWeight.Bold,
                                            text = "DEUCE!",
                                        )
                                    }

                                    homeWonGame -> {
                                        Text(
                                            fontWeight = FontWeight.Bold,
                                            text = "HOME WON THE GAME!",
                                        )
                                    }

                                    awayWonGame -> {
                                        Text(
                                            fontWeight = FontWeight.Bold,
                                            text = "AWAY WON THE GAME!",
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        // Replace with your BottomNavigationView ID

        // Assuming the menu item ID for "Matches" is R.id.navigation_home
        /*bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Navigate back to MatchesFragment and reselect the tab
                    //findNavController().popBackStack(R.id.navigation_home, false)

                    // Ensure the item is selected.  This might be redundant
                    // depending on your setup, but it's good practice to ensure
                    // the visual state is correct.
                    bottomNavigationView.selectedItemId = R.id.navigation_home
                    true // Indicate that the item selection was handled
                }
                R.id.navigation_match -> {
                    false
                }
                R.id.navigation_dashboard -> {
                    // Navigate to DashboardFragment
                    findNavController().navigate(R.id.navigation_dashboard)
                    true // Indicate that the item selection was handled
                }
                R.id.navigation_notifications -> {
                    // Navigate to NotificationsFragment
                    findNavController().navigate(R.id.navigation_notifications)
                    true // Indicate that the item selection was handled
                }
                // Handle other bottom navigation items if needed...
                else -> false // Indicate that the item selection was not handled
            }
        }*/
    }

    /*@Composable
    private fun SideEffects() = SingleEventEffect(viewModel.sideEffectFlow) { sideEffect ->
        when (sideEffect) {
            is MatchClickedSideEffect -> {
                Toast.makeText(requireContext(), "Match clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

}