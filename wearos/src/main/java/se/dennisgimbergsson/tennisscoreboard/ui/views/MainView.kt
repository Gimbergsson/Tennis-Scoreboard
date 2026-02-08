package se.dennisgimbergsson.tennisscoreboard.ui.views

import android.content.res.Configuration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.tooling.preview.devices.WearDevices.LARGE_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.RECT
import androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.SQUARE
import kotlinx.coroutines.launch
import se.dennisgimbergsson.tennisscoreboard.R
import se.dennisgimbergsson.tennisscoreboard.ui.screens.IncrementAway
import se.dennisgimbergsson.tennisscoreboard.ui.screens.IncrementHome
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainUiEvent
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewState
import se.dennisgimbergsson.tennisscoreboard.ui.screens.ResetScoreboard
import se.dennisgimbergsson.tennisscoreboard.ui.screens.RevertCurrentScore
import se.dennisgimbergsson.tennisscoreboard.ui.screens.SaveAndSyncScoreboards
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    state: MainViewState,
    onEvent: (MainUiEvent) -> Unit = {},
) {

    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(0)
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = modifier
                .fillMaxSize()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            state = listState,
            verticalArrangement = Arrangement.SpaceBetween,
            scalingParams = ScalingLazyColumnDefaults.scalingParams(),
        ) {
            item {
                NewScoreboardView(
                    state = state,
                    incrementHomeScore = { onEvent(IncrementHome) },
                    incrementAwayScore = { onEvent(IncrementAway) },
                )
            }
            /*item {
                ScoreboardView(
                    state = state,
                    incrementHomeScore = { onEvent(IncrementHome) },
                    incrementAwayScore = { onEvent(IncrementAway) },
                )
            }
            if (state.scoreboard.isTieBreak) {
                item {
                    TiebreakView(
                        state = state,
                        incrementHomeScore = { onEvent(IncrementHome) },
                        incrementAwayScore = { onEvent(IncrementAway) },
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }*/
            item {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { onEvent(RevertCurrentScore) }
                ) {
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = stringResource(id = R.string.revert_current_score),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { onEvent(ResetScoreboard) }
                ) {
                    Text(
                        text = stringResource(id = R.string.reset_scoreboard),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { onEvent(SaveAndSyncScoreboards) }
                ) {
                    Text(
                        text = "save and sync",
                        textAlign = TextAlign.Center,
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Preview(
    apiLevel = 34,
    device = LARGE_ROUND,
    group = "round",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    apiLevel = 34,
    device = SMALL_ROUND,
    group = "round",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    fontScale = 1.00f,
)
@Preview(
    apiLevel = 34,
    device = RECT,
    group = "rect",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    apiLevel = 34,
    device = SQUARE,
    group = "square",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ScoreboardViewPreview() = TennisScoreboardTheme {
    NewScoreboardView(
        state = MainViewState(
            /*scoreboard = Scoreboard(
                homeScore = Score(
                    points = Points.FIFTEEN,
                    wonGames = 1,
                    wonSets = 1,
                ),
                awayScore = Score(
                    points = Points.FORTY,
                    wonGames = 1,
                    wonSets = 1,
                )
            ),*/
            scoreboardHistory = listOf(
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                        wonGames = 1,
                        wonSets = 1,
                    ),
                    awayScore = Score(
                        points = Points.ZERO,
                        wonGames = 1,
                        wonSets = 0,
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FIFTEEN,
                        wonGames = 1,
                        wonSets = 1,
                    ),
                    awayScore = Score(
                        points = Points.FIFTEEN,
                        wonGames = 1,
                        wonSets = 1,
                    )
                )
            )
        )
    )
}