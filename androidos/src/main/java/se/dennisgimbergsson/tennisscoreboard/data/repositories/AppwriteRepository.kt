package se.dennisgimbergsson.tennisscoreboard.data.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.RowList
import io.appwrite.services.TablesDB
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoreboard.BuildConfig
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import javax.inject.Inject
import androidx.core.content.edit

interface AppwriteDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard)
    suspend fun getScoreboardData(): RowList<Map<String, Any>>?
}

class AppwriteRepository @Inject constructor(
    private val appwriteClient: Client,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcher: DispatcherProvider,
) : AppwriteDataSource {

    override suspend fun putScoreboardData(
        scoreboard: Scoreboard,
    ) = withContext(dispatcher.io()) {
        sharedPreferences.edit(commit = true) {
            putString(Constants.Keys.SCOREBOARD, gson.toJson(scoreboard))
        }
    }

    override suspend fun getScoreboardData() = withContext(dispatcher.io()) {
        appwriteClient
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject(BuildConfig.APPWRITE_PROJECT_ID)
            .setKey(BuildConfig.APPWRITE_API_KEY)

        val tablesDB = TablesDB(appwriteClient)
        var rowList: RowList<Map<String, Any>>? = null
        try {
            rowList = tablesDB.listRows(
                databaseId = "6716cb9a002391ac0641",
                tableId = "67181f210033e0e79885",
                queries = listOf(
                    Query.equal("id", 0),
                    Query.isNotNull("data")
                )
            )
        } catch (e: AppwriteException) {
            logAndroidMessage(message = e.message ?: "", exception = e)
        }

        return@withContext rowList
    }
}