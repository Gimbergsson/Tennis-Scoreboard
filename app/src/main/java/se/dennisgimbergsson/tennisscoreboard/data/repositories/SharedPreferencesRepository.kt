package se.dennisgimbergsson.tennisscoreboard.data.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.tennisscoreboard.data.models.Scoreboard
import se.dennisgimbergsson.tennisscoreboard.utils.Constants.SharedPreferences.SCOREBOARD_KEY
import se.dennisgimbergsson.tennisscoreboard.utils.DispatcherProvider
import javax.inject.Inject


interface SharedPreferencesDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard): Boolean
    suspend fun getScoreboardData(): Scoreboard
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
            .putString(SCOREBOARD_KEY, gson.toJson(scoreboard))
            .commit()
    }

    override suspend fun getScoreboardData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(SCOREBOARD_KEY, "") ?: ""
        gson.fromJson(json, Scoreboard::class.java) ?: Scoreboard()
    }
}