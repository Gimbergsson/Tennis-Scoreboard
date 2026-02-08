package se.dennisgimbergsson.tennisscoreboard.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import se.dennisgimbergsson.tennisscoreboard.data.entities.SavedMatches

@Dao
interface SavedMatchesDao {

    @Query("SELECT * FROM saved_matches WHERE id = :matchId")
    fun findById(matchId: Int): Flow<SavedMatches>

    @Query("SELECT * FROM saved_matches")
    fun getAllSavedMatches(): Flow<List<SavedMatches>>

    @Insert
    fun insertAll(vararg savedMatches: SavedMatches)

}