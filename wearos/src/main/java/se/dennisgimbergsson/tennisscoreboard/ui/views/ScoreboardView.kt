package se.dennisgimbergsson.tennisscoreboard.ui.views

import android.content.res.Configuration
import android.view.KeyEvent.KEYCODE_STEM_1
import android.view.KeyEvent.KEYCODE_STEM_2
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.input.WearableButtons
import androidx.wear.tooling.preview.devices.WearDevices.LARGE_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.RECT
import androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.SQUARE
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
) = LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.SpaceBetween
) {
    item {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .clickable { incrementHomeScore() }
                        .weight(1f)
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val homeButton = WearableButtons.getButtonInfo(context, KEYCODE_STEM_1)
                        val homeIcon =
                            WearableButtons.getButtonIcon(context, homeButton?.keycode ?: -1)
                        homeIcon?.let {
                            Image(
                                modifier = Modifier.size(14.dp),
                                bitmap = it.toBitmap().asImageBitmap(),
                                contentDescription = stringResource(id = R.string.icon_increment_home_score),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            modifier = Modifier,
                            text = stringResource(id = R.string.home_score_label),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = state.scoreboard.homeScore.wonSets.toString(),
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = state.scoreboard.homeScore.wonGames.toString(),
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontSize = 20.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        text = stringResource(id = state.scoreboard.homeScore.gameScore.stringResource),
                        color = Color.White,
                        textAlign = TextAlign.End,
                        fontSize = 32.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = Modifier,
                        text = "      ",
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp),
                        text = "-",
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier,
                        text = " - ",
                        color = Color.White,
                        fontSize = 32.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .clickable { incrementAwayScore() }
                        .weight(1f)
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.clickable { incrementAwayScore() },
                            text = stringResource(id = R.string.away_score_label),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val awayButton = WearableButtons.getButtonInfo(context, KEYCODE_STEM_2)
                        val awayIcon =
                            WearableButtons.getButtonIcon(context, awayButton?.keycode ?: -1)
                        awayIcon?.let {
                            Image(
                                modifier = Modifier.size(14.dp),
                                bitmap = it.toBitmap().asImageBitmap(),
                                contentDescription = stringResource(id = R.string.icon_increment_away_score),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = state.scoreboard.awayScore.wonGames.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 20.sp,
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = state.scoreboard.awayScore.wonSets.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        text = stringResource(id = state.scoreboard.awayScore.gameScore.stringResource),
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        fontSize = 32.sp,
                    )
                }
            }
        }
    }
    item {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { decrementHome() }
            ) {
                Text(
                    text = stringResource(id = R.string.decrement_home_score),
                    textAlign = TextAlign.Center,
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { decrementAway() }
            ) {
                Text(
                    text = stringResource(id = R.string.decrement_away_score),
                    textAlign = TextAlign.Center,
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
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
        Spacer(modifier = Modifier.height(48.dp))
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
    uiMode = Configuration.UI_MODE_NIGHT_NO
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