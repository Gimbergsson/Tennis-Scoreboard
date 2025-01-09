package se.dennisgimbergsson.tennisscoreboard.ui.screens

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_STEM_1
import android.view.KeyEvent.KEYCODE_STEM_2
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest.create
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.shared.extensions.logWearMessage
import se.dennisgimbergsson.shared.utils.Constants
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoreboard.ui.views.ScoreboardView
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var gson: Gson

    private val viewModel: MainViewModel by viewModels()

    private lateinit var dataClient: DataClient

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)

        lifecycle.addObserver(viewModel)

        setContent {
            TennisScoreboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScoreboardView(
                        state = viewModel.stateFlow.collectAsStateWithLifecycle(
                            initialValue = MainViewState()
                        ).value,
                        incrementHomeScore = {
                            viewModel.incrementHome()
                            increaseCounter()
                        },
                        incrementAwayScore = {
                            viewModel.incrementAway()
                            increaseCounter()
                        },
                        clearAll = viewModel::clear,
                        decrementHome = viewModel::decrementHome,
                        decrementAway = viewModel::decrementAway,
                    )
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (event.repeatCount) {
            0 -> {
                when (keyCode) {
                    KEYCODE_STEM_1 -> {
                        viewModel.incrementHome()
                        increaseCounter()
                    }

                    KEYCODE_STEM_2 -> {
                        viewModel.incrementAway()
                        increaseCounter()
                    }
                }
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun increaseCounter() {
        val json = gson.toJson(viewModel.currentState().scoreboard)
        val putDataReq: PutDataRequest = create(Constants.Paths.SCOREBOARD_UPDATE).run {
            dataMap.putString(Constants.Keys.SCOREBOARD_UPDATE, json)
            asPutDataRequest()
        }.setUrgent()

        /*val putDataTask2 = dataClient.putDataItem(putDataReq)
        lifecycleScope.launch {
            try {
                putDataTask2.await()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to p", e)
            }
        }*/


        val putDataTask = dataClient.putDataItem(putDataReq)
        putDataTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Task completed successfully
                logWearMessage(message = "DataItem saved: ${task.result.uri}")
            } else {
                // Task failed, handle the exception
                logWearMessage(
                    message = "Error saving DataItem: ${task.exception?.message}",
                    exception = task.exception,
                )
            }
        }
    }
}