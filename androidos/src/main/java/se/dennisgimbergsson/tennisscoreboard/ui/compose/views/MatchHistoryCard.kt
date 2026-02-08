package se.dennisgimbergsson.tennisscoreboard.ui.compose.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.Colors
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@Composable
fun MatchHistoryCard(
    pointWon: Boolean,
    gameWon: Boolean,
    setWon: Boolean,
    scoreboard: Scoreboard,
    topContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) = ElevatedCard(
    modifier = Modifier
        .fillMaxWidth(),
    shape = RectangleShape,
    colors = CardDefaults.elevatedCardColors().copy(
        containerColor = MaterialTheme.Colors.containerColor
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    onClick = onClick,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        topContent()
        Spacer(modifier = Modifier.height(8.dp))
        val homeScore = scoreboard.homeScore
        val awayScore = scoreboard.awayScore
        val homePoints = stringResource(homeScore.points.stringResource)
        val awayPoints = stringResource(awayScore.points.stringResource)
        if (pointWon && !gameWon) {
            Text(text = "$homePoints - $awayPoints")
        }
        if (gameWon) {
            Text(text = "${homeScore.wonGames} - ${awayScore.wonGames}")
        }
        if (setWon) {
            Text(text = "${homeScore.wonSets} - ${awayScore.wonSets}")
        }
    }
}

@Composable
fun MatchHistoryCard(
    scoreboard: Scoreboard,
    topContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) = ElevatedCard(
    modifier = Modifier
        .fillMaxWidth(),
    shape = RectangleShape,
    colors = CardDefaults.elevatedCardColors().copy(
        containerColor = MaterialTheme.Colors.containerColor
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    onClick = onClick,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        topContent()
        Spacer(modifier = Modifier.height(8.dp))
        val homeScore = scoreboard.homeScore
        val awayScore = scoreboard.awayScore
        val homePoints = stringResource(homeScore.points.stringResource)
        val awayPoints = stringResource(awayScore.points.stringResource)
        Text(text = "$homePoints - $awayPoints")
        Text(text = "${homeScore.wonGames} - ${awayScore.wonGames}")
        Text(text = "${homeScore.wonSets} - ${awayScore.wonSets}")
    }
}

@ThemedPreview
@Composable
private fun MatchRowPreview() = TennisScoreboardTheme {
    MatchHistoryCard(
        scoreboard = Scoreboard(
            homeScore = Score(
                points = Points.FIFTEEN,
                wonGames = 3,
                wonSets = 1
            ),
            awayScore = Score(
                points = Points.FORTY,
                wonGames = 4,
                wonSets = 2
            )
        )
    )
}