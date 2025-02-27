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
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.tennisscoreboard.ui.theme.TennisScoreboardTheme
import se.dennisgimbergsson.tennisscoreboard.ui.views.MainView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        lifecycle.addObserver(viewModel)

        setContent {
            TennisScoreboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(
                        state = viewModel.stateFlow.collectAsStateWithLifecycle(
                            initialValue = MainViewState()
                        ).value,
                        incrementHomeScore = {
                            viewModel.incrementHome()
                            viewModel.updateScoreboard()
                        },
                        incrementAwayScore = {
                            viewModel.incrementAway()
                            viewModel.updateScoreboard()
                        },
                        revertLastScore = viewModel::popScoreboardHistory,
                        clearAll = viewModel::clearScoreboard,
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
                        viewModel.updateScoreboard()
                    }

                    KEYCODE_STEM_2 -> {
                        viewModel.incrementAway()
                        viewModel.updateScoreboard()
                    }
                }
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}