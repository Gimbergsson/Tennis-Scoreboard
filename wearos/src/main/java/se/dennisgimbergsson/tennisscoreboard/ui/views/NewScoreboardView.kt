package se.dennisgimbergsson.tennisscoreboard.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.shared.utils.WearPreview
import se.dennisgimbergsson.tennisscoreboard.R
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewState
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewScoreboardView(
    state: MainViewState,
    incrementHomeScore: () -> Unit = {},
    incrementAwayScore: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .indication(interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    coroutineScope.launch {
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)

                        // Split the composable into two parts, one for each action.
                        when {
                            offset.x < size.width / 2 -> incrementHomeScore()
                            else -> incrementAwayScore()
                        }

                        interactionSource.emit(PressInteraction.Release(press))
                    }
                }
            },
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val scoreboard = state.scoreboardHistory.last()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier,
                            text = stringResource(id = R.string.home_score_label),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(
                            modifier = Modifier.clickable { incrementAwayScore() },
                            text = stringResource(id = R.string.away_score_label),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f, fill = false),
                            //text = state.scoreboard.homeScore.wonSets.toString(),
                            text = scoreboard.homeScore.wonSets.toString(),
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        //if (state.scoreboard.isTieBreak) {
                        if (scoreboard.isTieBreak) {
                            Text(
                                modifier = Modifier
                                    .weight(2f),
                                text = "Tiebreak",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                maxLines = 1,
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    //text = state.scoreboard.homeScore.wonGames.toString(),
                                    text = scoreboard.homeScore.wonGames.toString(),
                                    color = Color.White,
                                    textAlign = TextAlign.End,
                                    fontSize = 20.sp,
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp),
                                    text = "-",
                                    color = Color.White,
                                )
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    //text = state.scoreboard.awayScore.wonGames.toString(),
                                    text = scoreboard.awayScore.wonGames.toString(),
                                    color = Color.White,
                                    textAlign = TextAlign.Start,
                                    fontSize = 20.sp,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            modifier = Modifier
                                .weight(1f, fill = false),
                            //text = state.scoreboard.awayScore.wonSets.toString(),
                            text = scoreboard.awayScore.wonSets.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            text = when {
                                /*state.scoreboard.isTieBreak -> {
                                    state.scoreboard.homeScore.tieBreakPoints.toString()
                                }*/
                                scoreboard.isTieBreak -> {
                                    scoreboard.homeScore.tieBreakPoints.toString()
                                }

                                //else -> stringResource(id = state.scoreboard.homeScore.points.stringResource)
                                else -> stringResource(id = scoreboard.homeScore.points.stringResource)
                            },
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontSize = 28.sp,
                            maxLines = 1,
                        )
                        Text(
                            modifier = Modifier,
                            text = " - ",
                            color = Color.White,
                            fontSize = 28.sp,
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            text = when {
                                /*state.scoreboard.isTieBreak -> {
                                    state.scoreboard.awayScore.tieBreakPoints.toString()
                                }*/
                                scoreboard.isTieBreak -> {
                                    scoreboard.awayScore.tieBreakPoints.toString()
                                }

                                //else -> stringResource(id = state.scoreboard.awayScore.points.stringResource)
                                else -> stringResource(id = scoreboard.awayScore.points.stringResource)
                            },
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 28.sp,
                            maxLines = 1,
                        )
                    }
                }
                /*Row(

            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable { incrementHomeScore() }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable { incrementAwayScore() }
            )
        }*/
            }
        }
    }
}

@WearPreview
@ThemedPreview
@Composable
private fun ScoreboardViewPreview() = TennisScoreboardTheme {
    NewScoreboardView(
        state = MainViewState(
            /*scoreboard = Scoreboard(
                homeScore = Score(
                    points = Points.FORTY
                )
            )*/
            scoreboardHistory = listOf(
                Scoreboard(
                    homeScore = Score(
                        points = Points.FORTY
                    )
                ),
                Scoreboard(
                    homeScore = Score(
                        points = Points.FORTY
                    )
                ),
            )
        ),
        incrementHomeScore = {},
        incrementAwayScore = {},
    )
}