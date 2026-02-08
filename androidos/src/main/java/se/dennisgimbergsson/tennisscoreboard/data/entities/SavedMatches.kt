package se.dennisgimbergsson.tennisscoreboard.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_matches")
data class SavedMatches(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "json_data") val jsonData: String?,
)