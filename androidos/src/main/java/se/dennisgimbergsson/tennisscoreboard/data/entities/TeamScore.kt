package se.dennisgimbergsson.tennisscoreboard.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "game_score")
data class TeamScore(
    @ColumnInfo(name = "points") val points: String,
    @ColumnInfo(name = "games") val games: Int,
    @ColumnInfo(name = "sets") val sets: Int,
)