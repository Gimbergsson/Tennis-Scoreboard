package se.dennisgimbergsson.tennisscoreboard.repositories

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.utils.Constants.Keys
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import javax.inject.Inject
import androidx.core.content.edit
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

interface SharedPreferencesDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard)
    suspend fun getScoreboardData(): Scoreboard

    suspend fun putScoreboardHistoryData(scoreboardHistory: List<Scoreboard>)
    suspend fun getScoreboardHistoryData(): List<Scoreboard>

    suspend fun putSavedScoreboards(savedScoreboards: List<Scoreboard>)
    suspend fun getSavedScoreboards(): List<Scoreboard>
}

class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcherProvider: DispatcherProvider,
) : SharedPreferencesDataSource {

    override suspend fun putScoreboardData(
        scoreboard: Scoreboard,
    ) = withContext(dispatcherProvider.io()) {
        sharedPreferences.edit() {
            val json = gson.toJson(scoreboard)
            Log.d("TAG", "putScoreboardData: $json")
            putString(Keys.SCOREBOARD, gson.toJson(scoreboard))
        }
    }

    override suspend fun getScoreboardData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Keys.SCOREBOARD, "") ?: ""
        Log.d("TAG", "getScoreboardData: $json")
        gson.fromJson(json, Scoreboard::class.java) ?: Scoreboard()
    }

    override suspend fun putScoreboardHistoryData(scoreboardHistory: List<Scoreboard>) =
        withContext(dispatcherProvider.io()) {
            sharedPreferences.edit() {
                putString(Keys.SCOREBOARD_HISTORY, gson.toJson(scoreboardHistory))
            }
        }

    override suspend fun getScoreboardHistoryData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Keys.SCOREBOARD_HISTORY, DEFAULT_SCOREBOARD_HISTORY)
            ?: DEFAULT_SCOREBOARD_HISTORY
        gson.fromJson(json, Array<Scoreboard>::class.java)?.toList() ?: emptyList()
    }

    override suspend fun putSavedScoreboards(savedScoreboards: List<Scoreboard>) =
        withContext(dispatcherProvider.io()) {
            sharedPreferences.edit() {
                putString(Keys.SAVED_SCOREBOARDS, gson.toJson(savedScoreboards))
            }
        }

    override suspend fun getSavedScoreboards() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Keys.SAVED_SCOREBOARDS, DEFAULT_SAVED_SCOREBOARDS)
            ?: DEFAULT_SAVED_SCOREBOARDS
        gson.fromJson(json, Array<Scoreboard>::class.java)?.toList() ?: emptyList()
    }

    companion object {
        private const val DEFAULT_SCOREBOARD_HISTORY =
            "[{\"awayScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0},\"homeScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0}}]"
        private const val DEFAULT_SAVED_SCOREBOARDS =
            "[{\"awayScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0},\"homeScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0}}]"
    }
}