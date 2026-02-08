package se.dennisgimbergsson.tennisscoreboard.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.screens.MainViewState
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@Composable
fun TiebreakView(
    state: MainViewState,
    incrementHomeScore: () -> Unit = {},
    incrementAwayScore: () -> Unit = {},
) = Box(
    modifier = Modifier
        .fillMaxWidth(),
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Tiebreak",
                color = Color.White,
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
        ) {
            val scoreboard = state.scoreboardHistory.last()
            Column(
                modifier = Modifier
                    .clickable { incrementHomeScore() }
                    .weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                /*Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.home_score_label),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                )
                Spacer(modifier = Modifier.height(16.dp))*/
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    //text = state.scoreboard.homeScore.tieBreakPoints.toString(),
                    text = scoreboard.homeScore.tieBreakPoints.toString(),
                    color = Color.White,
                    textAlign = TextAlign.End,
                    fontSize = 32.sp,
                )
            }
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                //Spacer(modifier = Modifier.height(32.dp))
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
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                /*Text(
                    modifier = Modifier.clickable { incrementAwayScore() },
                    text = stringResource(id = R.string.away_score_label),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                )
                Spacer(modifier = Modifier.height(16.dp))*/
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    //text = state.scoreboard.awayScore.tieBreakPoints.toString(),
                    text = scoreboard.awayScore.tieBreakPoints.toString(),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp,
                )
            }
        }
    }
}

//@WearPreview
@ThemedPreview
@Composable
private fun TiebreakViewPreview() = TennisScoreboardTheme {
    TiebreakView(
        state = MainViewState(
           scoreboardHistory = listOf(
                Scoreboard(
                   homeScore = Score(
                       points = Points.FIFTEEN,
                       wonGames = 1,
                       wonSets = 2,
                       tieBreakPoints = 3,
                   ),
                   awayScore = Score(
                       points = Points.ZERO,
                       wonGames = 1,
                       wonSets = 2,
                       tieBreakPoints = 3,
                   ),
               ),
               Scoreboard(
                   homeScore = Score(
                       points = Points.FIFTEEN,
                       wonGames = 1,
                       wonSets = 2,
                       tieBreakPoints = 3,
                   ),
                   awayScore = Score(
                       points = Points.FIFTEEN,
                       wonGames = 1,
                       wonSets = 2,
                       tieBreakPoints = 3,
                   ),
               ),
           )
        ),
        incrementHomeScore = {},
        incrementAwayScore = {},
    )
}