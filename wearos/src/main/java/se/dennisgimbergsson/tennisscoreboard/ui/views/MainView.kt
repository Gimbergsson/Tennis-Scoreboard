package se.dennisgimbergsson.tennisscoreboard.ui.views

import android.content.res.Configuration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import se.dennisgimbergsson.shared.data.models.Score
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.enums.GameScores
import se.dennisgimbergsson.tennisscoreboard.R
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewState
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme

@Composable
fun ScoreboardView(
    modifier: Modifier = Modifier,
    state: MainViewState,
    incrementHomeScore: () -> Unit = {},
    incrementAwayScore: () -> Unit = {},
    clearAll: () -> Unit = {},
    decrementHome: () -> Unit = {},
    decrementAway: () -> Unit = {},
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
                ScoreboardView(
                    state = state,
                    incrementHomeScore = incrementHomeScore,
                    incrementAwayScore = incrementAwayScore,
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { decrementHome() }
                    ) {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = stringResource(id = R.string.decrement_home_score),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { decrementAway() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.decrement_away_score),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { clearAll() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.clear_all_score),
                            textAlign = TextAlign.Center,
                        )
                    }
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
    ScoreboardView(
        state = MainViewState(
            scoreboard = Scoreboard(
                homeScore = Score(
                    gameScore = GameScores.FIFTEEN,
                    wonGames = 1,
                    wonSets = 1,
                ),
                awayScore = Score(
                    gameScore = GameScores.FORTY,
                    wonGames = 1,
                    wonSets = 1,
                )
            )
        )
    )
}