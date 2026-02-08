package se.dennisgimbergsson.tennisscoreboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.dennisgimbergsson.tennisscoreboard.dao.CurrentScoreDao
import se.dennisgimbergsson.tennisscoreboard.dao.SavedMatchesDao
import se.dennisgimbergsson.tennisscoreboard.data.entities.CurrentScore
import se.dennisgimbergsson.tennisscoreboard.data.entities.SavedMatches

@Database(entities = [CurrentScore::class, SavedMatches::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun currentScoreDao(): CurrentScoreDao
    abstract fun savedMatchesDao(): SavedMatchesDao
}