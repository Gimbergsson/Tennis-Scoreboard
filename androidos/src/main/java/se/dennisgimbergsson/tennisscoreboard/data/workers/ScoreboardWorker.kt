package se.dennisgimbergsson.tennisscoreboard.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.gson.GsonBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.utils.Constants.Keys
import se.dennisgimbergsson.shared.utils.Constants.Paths
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import se.dennisgimbergsson.tennisscoreboard.data.entities.SavedMatches
import se.dennisgimbergsson.tennisscoreboard.data.repositories.SavedMatchesDataSource
import se.dennisgimbergsson.tennisscoreboard.data.repositories.ScoreDataSource
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.codec.PointsDeserializer
import se.dennisgimbergsson.tennisscoring.data.codec.PointsSerializer
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard

@HiltWorker
class ScoreboardWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scoreRepository: ScoreDataSource,
    private val savedMatchesRepository: SavedMatchesDataSource,
    dispatcherProvider: DispatcherProvider,
) : Worker(context, workerParams), DataClient.OnDataChangedListener {

    private val coroutineScope = CoroutineScope(dispatcherProvider.io())
    private val dataClient = Wearable.getDataClient(applicationContext)
    private val gson = GsonBuilder()
        .registerTypeAdapter(Points::class.java, PointsDeserializer())
        .registerTypeAdapter(Points::class.java, PointsSerializer())
        .create()

    override fun doWork(): Result {
        dataClient.addListener(this)
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == TYPE_CHANGED) {
                event.dataItem.also { item ->
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    if (item.uri.path?.compareTo(Paths.SCOREBOARD_UPDATE) == 0) {
                        saveScoreboard(scoreboard = dataMap.toScoreboard())
                    } else if (item.uri.path?.compareTo(Paths.SAVED_SCOREBOARDS_UPDATE) == 0) {
                        saveScoreboardArray(scoreboardArray =  dataMap.toScoreboardArray())
                    }
                }
            }
        }
    }

    private fun saveScoreboard(scoreboard: Scoreboard) = coroutineScope.launch {
        scoreRepository.putScoreboardData(scoreboard = scoreboard)
    }

    private fun saveScoreboardArray(scoreboardArray: Array<Scoreboard>) = coroutineScope.launch {
        savedMatchesRepository.putSavedMatchesData(
            savedMatches = SavedMatches(
                jsonData = gson.toJson(scoreboardArray)
            )
        )
    }

    private fun DataMap.toScoreboard() =
        gson.fromJson(
            this.getString(Keys.SCOREBOARD_UPDATE),
            Scoreboard::class.java
        ) ?: Scoreboard()

    private fun DataMap.toScoreboardArray() =
        gson.fromJson(
            this.getString(Keys.SAVED_SCOREBOARDS),
            Array<Scoreboard>::class.java
        ) ?: emptyArray()
}