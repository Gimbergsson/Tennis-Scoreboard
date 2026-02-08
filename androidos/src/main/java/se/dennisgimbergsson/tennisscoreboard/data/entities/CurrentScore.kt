package se.dennisgimbergsson.tennisscoreboard.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_score")
data class CurrentScore(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "away_score") val awayTeamScore: TeamScore?,
    @ColumnInfo(name = "home_score") val homeTeamScore: TeamScore?,
)

