package se.dennisgimbergsson.tennisscoreboard.ui.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.Colors
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Score
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@Composable
fun MatchCard(
    scoreboard: Scoreboard,
    onClick: () -> Unit = {},
) = ElevatedCard(
    modifier = Modifier
        .fillMaxWidth(),
    shape = RectangleShape,
    colors = CardDefaults.elevatedCardColors().copy(
        containerColor = MaterialTheme.Colors.containerColor
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    /*colors = CardDefaults.outlinedCardColors().copy(
        containerColor = MaterialTheme.Colors.containerColor
    ),
    border = CardDefaults.outlinedCardBorder(enabled = true).copy(
        width = 2.dp,
        //brush = SolidColor(colorResource(id = R.color.black))
    ),*/
    onClick = onClick,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier,
                text = "Home",
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = scoreboard.homeScore.points.integer.toString(),
            )
            Text(
                modifier = Modifier.width(IntrinsicSize.Max),
                text = scoreboard.homeScore.wonGames.toString(),
            )
            Text(
                text = scoreboard.homeScore.wonSets.toString(),
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "-")
            Text(text = "-")
            Text(text = "-")
            Text(text = "-")
        }
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Away",
                fontWeight = FontWeight.Bold,
            )
            Text(text = scoreboard.awayScore.points.integer.toString())
            Text(text = scoreboard.awayScore.wonGames.toString())
            Text(text = scoreboard.awayScore.wonSets.toString())
        }
    }
}

@ThemedPreview
@Composable
private fun MatchRowPreview() = TennisScoreboardTheme {
    MatchCard(
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