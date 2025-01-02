package se.dennisgimbergsson.tennisscoreboard.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.DocumentList
import io.appwrite.services.Databases
import kotlinx.coroutines.withContext
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoreboard.BuildConfig
import javax.inject.Inject

interface AppwriteDataSource {
    suspend fun putScoreboardData(scoreboard: Scoreboard): Boolean
    suspend fun getScoreboardData(): DocumentList<Map<String, Any>>?
}

class AppwriteRepository @Inject constructor(
    private val appwriteClient: Client,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcherProvider: DispatcherProvider,
) : AppwriteDataSource {

    override suspend fun putScoreboardData(
        scoreboard: Scoreboard,
    ) = withContext(dispatcherProvider.io()) {
        sharedPreferences.edit()
            .putString(Constants.Keys.SCOREBOARD, gson.toJson(scoreboard))
            .commit()
    }

    /*override suspend fun getScoreboardData() = flow {

        if (currentCoroutineContext().isActive) {
            emit()

        }
    }*/
    override suspend fun getScoreboardData() = withContext(dispatcherProvider.io()) {
        /*val account = Account(appwriteClient)

        account.createSession("6716dd67003e7a5432e2", "qwer1234")

        val jwt = account.createJWT()*/

        /*appwriteClient
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("6716c8cf001419f170c3")
            .setKey("standard_831da97cd0697377e8b7327848446fe9ce2adac26eca0131a774d88083a90fcaa39cf12cc57f4f29e009714286b3505d83b38c9dcf6b23ccbdbd90bae8a9860bb6344f637437e305b84dad53762fbd9ffb6a4b063626971fb19a2db69991fb567f7b32e7c7a13712665cfe3866161d6b964850bb15bbb76bd75d6089d74dd333")*/

        appwriteClient
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject(BuildConfig.APPWRITE_PROJECT_ID)
            .setKey(BuildConfig.APPWRITE_API_KEY)

        //.setKey("standard_831da97cd0697377e8b7327848446fe9ce2adac26eca0131a774d88083a90fcaa39cf12cc57f4f29e009714286b3505d83b38c9dcf6b23ccbdbd90bae8a9860bb6344f637437e305b84dad53762fbd9ffb6a4b063626971fb19a2db69991fb567f7b32e7c7a13712665cfe3866161d6b964850bb15bbb76bd75d6089d74dd333")
        //.setSession("standard_831da97cd0697377e8b7327848446fe9ce2adac26eca0131a774d88083a90fcaa39cf12cc57f4f29e009714286b3505d83b38c9dcf6b23ccbdbd90bae8a9860bb6344f637437e305b84dad53762fbd9ffb6a4b063626971fb19a2db69991fb567f7b32e7c7a13712665cfe3866161d6b964850bb15bbb76bd75d6089d74dd333")

        val databases = Databases(appwriteClient)

        var documentList: DocumentList<Map<String, Any>>? = null

        try {
            documentList = databases.listDocuments(
                databaseId = "6716cb9a002391ac0641",
                collectionId = "6716cbbb0037477cada5",
                queries = listOf(
                    Query.equal("id", 0),
                    Query.isNotNull("data")
                )
            )
        } catch (e: AppwriteException) {
            logAndroidMessage(message = e.message ?: "", exception = e)
        }

        return@withContext documentList
    }
}