package se.dennisgimbergsson.tennisscoreboard.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.utils.Constants.Keys
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import javax.inject.Inject

interface SharedPreferencesDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard): Boolean
    suspend fun getScoreboardData(): Scoreboard

    suspend fun putScoreboardHistoryData(scoreboardHistory: List<Scoreboard>): Boolean
    suspend fun getScoreboardHistoryData(): List<Scoreboard>
}

class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcherProvider: DispatcherProvider,
) : SharedPreferencesDataSource {

    override suspend fun putScoreboardData(
        scoreboard: Scoreboard,
    ) = withContext(dispatcherProvider.io()) {
        sharedPreferences.edit()
            .putString(Keys.SCOREBOARD, gson.toJson(scoreboard))
            .commit()
    }

    override suspend fun getScoreboardData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Keys.SCOREBOARD, "") ?: ""
        gson.fromJson(json, Scoreboard::class.java) ?: Scoreboard()
    }

    override suspend fun putScoreboardHistoryData(scoreboardHistory: List<Scoreboard>) =
        withContext(dispatcherProvider.io()) {
            sharedPreferences.edit()
                .putString(Keys.SCOREBOARD_HISTORY, gson.toJson(scoreboardHistory))
                .commit()
        }

    override suspend fun getScoreboardHistoryData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Keys.SCOREBOARD_HISTORY, DEFAULT_SCOREBOARD_HISTORY)
            ?: DEFAULT_SCOREBOARD_HISTORY
        gson.fromJson(json, Array<Scoreboard>::class.java)?.toList() ?: emptyList()
    }

    companion object {
        private const val DEFAULT_SCOREBOARD_HISTORY =
            "[{\"awayScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0},\"homeScore\":{\"gameScore\":\"ZERO\",\"wonGames\":0,\"wonSets\":0}]"
    }
}