package se.dennisgimbergsson.tennisscoreboard.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoreboard.dao.SavedMatchesDao
import se.dennisgimbergsson.tennisscoreboard.data.entities.SavedMatches
import javax.inject.Inject

interface SavedMatchesDataSource {
    suspend fun getSavedMatchesData(): Flow<List<SavedMatches>>
    suspend fun putSavedMatchesData(savedMatches: SavedMatches)
    suspend fun getSavedMatchById(matchId: Int): Flow<SavedMatches>
}

class SavedMatchesRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val savedMatchesDao: SavedMatchesDao,
) : SavedMatchesDataSource {

    override suspend fun getSavedMatchesData() = savedMatchesDao.getAllSavedMatches()

    override suspend fun putSavedMatchesData(savedMatches: SavedMatches) {
        withContext(dispatcherProvider.io()) {
            savedMatchesDao.insertAll(
                savedMatches = arrayOf(savedMatches)
            )
        }
    }

    override suspend fun getSavedMatchById(matchId: Int) =
        savedMatchesDao.findById(matchId = matchId)
}