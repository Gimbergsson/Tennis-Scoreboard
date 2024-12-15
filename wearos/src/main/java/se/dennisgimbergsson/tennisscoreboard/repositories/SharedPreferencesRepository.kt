package se.dennisgimbergsson.tennisscoreboard.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.shared.data.models.Scoreboard
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
            .putString(Constants.Keys.SCOREBOARD, gson.toJson(scoreboard))
            .commit()
    }

    override suspend fun getScoreboardData() = withContext(dispatcherProvider.io()) {
        val json = sharedPreferences.getString(Constants.Keys.SCOREBOARD, "") ?: ""
        gson.fromJson(json, Scoreboard::class.java) ?: Scoreboard()
    }
}