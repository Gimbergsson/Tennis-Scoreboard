package se.dennisgimbergsson.tennisscoreboard.ui.screens.scoreboard

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoreboard.ui.compose.views.FlipNumberText
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score

@AndroidEntryPoint
class ScoreboardFragment : Fragment() {

    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setContent {
            TennisScoreboardTheme {
                val state =
                    viewModel.stateFlow.collectAsStateWithLifecycle(
                        //initialValue = ScoreboardState()
                    ).value
                ScoreboardScreen(state)
            }
        }
    }

    @Composable
    private fun ScoreboardScreen(state: ScoreboardState) = TennisScoreboardTheme {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlipNumberText(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(state.homeTeamScore.points.stringResource),
                        /*textStyle = LocalTextStyle.current.copy(
                            fontSize = 72.sp
                        ),*/
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    FlipNumberText(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(state.awayTeamScore.points.stringResource),
                        /*textStyle = LocalTextStyle.current.copy(
                            fontSize = 72.sp
                        ),*/
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FlipNumberText(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(state.homeTeamScore.points.stringResource),
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    FlipNumberText(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(state.awayTeamScore.points.stringResource),
                    )
                }
            }
        }
    }

    /*@Preview(
        showBackground = true,
        device = "specc:width=1920dp,height=1080dp,orientation=landsape"
    )*/
    @ThemedPreview
    @Composable
    private fun ScoreboardScreenPreview() = TennisScoreboardTheme {
        ScoreboardScreen(
            state = ScoreboardState(
                homeTeamScore = Score(
                    points = Points.FIFTEEN,
                    wonGames = 0,
                    wonSets = 0
                ),
                awayTeamScore = Score(
                    points = Points.THIRTY,
                    wonGames = 0,
                    wonSets = 0
                ),
            )
        )
    }
}