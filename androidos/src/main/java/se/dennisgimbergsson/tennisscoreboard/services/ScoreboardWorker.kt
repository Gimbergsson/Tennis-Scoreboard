package se.dennisgimbergsson.tennisscoreboard.services

import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent.TYPE_CHANGED
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.gson.GsonBuilder
import se.dennisgimbergsson.shared.GameScoresDeserializer
import se.dennisgimbergsson.shared.data.models.Scoreboard
import se.dennisgimbergsson.shared.enums.GameScores
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.Constants

class ScoreboardWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams), DataClient.OnDataChangedListener {

    private val dataClient = Wearable.getDataClient(applicationContext)

    override fun doWork(): Result {
        // Listen for data changes here using Wearable.getDataClient()
        // Process data changes and return Result.success() or Result.failure()

        dataClient.addListener(this)

        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()

        // Remove listener to prevent leaks
        dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(Constants.Paths.SCOREBOARD_UPDATE) == 0) {
                        val scoreboard =
                            getScoreboardUpdate(DataMapItem.fromDataItem(item).dataMap)
                        val homeGameScore =
                            scoreboard.homeScore.gameScore.score
                        val awayGameScore =
                            scoreboard.awayScore.gameScore.score
                        Toast.makeText(
                            applicationContext,
                            "Score: $homeGameScore - $awayGameScore",
                            Toast.LENGTH_SHORT
                        ).show()
                        logAndroidMessage("Score: ${scoreboard.homeScore} - ${scoreboard.awayScore}")
                    }
                }
            }
        }
    }

    private fun getScoreboardUpdate(dataMap: DataMap): Scoreboard {
        val json = dataMap.getString(Constants.Keys.SCOREBOARD_UPDATE)
        logAndroidMessage(message = "json: $json")
        return GsonBuilder()
            .registerTypeAdapter(GameScores::class.java, GameScoresDeserializer())
            .create()
            .fromJson(json, Scoreboard::class.java) ?: Scoreboard()
    }
}