package se.dennisgimbergsson.tennisscoreboard.ui.screens.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.compose.SingleEventEffect
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoreboard.ui.compose.views.MatchCard
import se.dennisgimbergsson.tennisscoring.Constants.DummyData

@AndroidEntryPoint
class MatchesFragment : Fragment() {

    private val viewModel: MatchesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setContent {
            SideEffects()
            TennisScoreboardTheme {
                val state =
                    viewModel.stateFlow.collectAsStateWithLifecycle(initialValue = MatchesState()).value
                HomeScreen(
                    state = state,
                    events = viewModel::onEvents
                )
            }
        }
    }

    @Composable
    private fun HomeScreen(
        state: MatchesState,
        events: (MatchesUiEvent) -> Unit = {},
    ) = LazyColumn(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        val savedMatchesSize = state.tennisMatches.size
        when {
            savedMatchesSize > 0 -> {
                items(savedMatchesSize) { index ->
                    val scoreboardKey = state.tennisMatches.keys.elementAt(index)
                    val scoreboard = state.tennisMatches.values.elementAt(index).last()
                    Spacer(modifier = Modifier.height(16.dp))
                    MatchCard(
                        scoreboard = scoreboard,
                        onClick = {
                            events(MatchesUiClicked(matchId = scoreboardKey))
                        }
                    )
                    if (index == savedMatchesSize - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            else -> item { EmptyMatchesState() }
        }
    }

    @Composable
    private fun EmptyMatchesState(
        modifier: Modifier = Modifier,
    ) = Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No matches found"
        )
    }

    @ThemedPreview
    @Composable
    private fun HomeScreenPreview() = TennisScoreboardTheme {
        HomeScreen(
            state = MatchesState(
                //savedMatches = DUMMY_SCOREBOARDS
                tennisMatches = DummyData.SCOREBOARD_HISTORY
            )
        )
    }

    @Composable
    private fun SideEffects() = SingleEventEffect(viewModel.sideEffectFlow) { sideEffect ->
        when (sideEffect) {
            is MatchesClickedSideEffect -> {
                val action = MatchesFragmentDirections.actionNavigationHomeToNavigationMatch(
                    argMatchId = sideEffect.matchId
                )
                findNavController().navigate(action)
            }
        }
    }

}