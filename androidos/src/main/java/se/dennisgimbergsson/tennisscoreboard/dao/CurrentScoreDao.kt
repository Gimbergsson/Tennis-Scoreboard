package se.dennisgimbergsson.tennisscoreboard.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import se.dennisgimbergsson.tennisscoreboard.data.entities.CurrentScore

@Dao
interface CurrentScoreDao {

    @Query("SELECT * FROM current_score")
    fun getAllScores(): Flow<List<CurrentScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentScore(currentScore: CurrentScore)

}