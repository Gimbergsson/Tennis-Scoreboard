package se.dennisgimbergsson.tennisscoreboard

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.tennisscoreboard.data.workers.ScoreboardWorker
import se.dennisgimbergsson.tennisscoreboard.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var workManager: WorkManager

    private lateinit var binding: ActivityMainBinding

    private val wearScoreboardWorker = OneTimeWorkRequestBuilder<ScoreboardWorker>()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        // Enable edge-to-edge which means app can draw under system bars.
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // Inflate the layout using the generated binding class.
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view to the root view of the binding.
        setContentView(binding.root)

        // Add margin to the top of the toolbar to account for the status bar.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val displayCutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                topMargin = maxOf(systemBarInsets.top, displayCutoutInsets.top)
                bottomMargin = maxOf(systemBarInsets.bottom, displayCutoutInsets.bottom)
                leftMargin = maxOf(systemBarInsets.left, displayCutoutInsets.left)
                rightMargin = maxOf(systemBarInsets.right, displayCutoutInsets.right)
            }
            WindowInsetsCompat.CONSUMED
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_matches,
                R.id.navigation_scoreboard,
                R.id.navigation_notifications
            ),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_match,
                R.id.navigation_matches,
                    -> {
                    binding.toolbar.isVisible = true

                    val menu = binding.navView.menu
                    menu.findItem(R.id.navigation_matches).isChecked = true
                }
                R.id.navigation_scoreboard -> {
                    binding.toolbar.isVisible = false
                }

                else -> {
                    binding.toolbar.isVisible = true
                }
            }
        }

        binding.navView.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_matches -> {
                    navController.popBackStack(R.id.navigation_matches, inclusive = false)
                }
                else -> {}
            }
        }

        /*binding.navView.setOnItemSelectedListener { item ->
            false
        }*/

        startWearWorkListener()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.cancelUniqueWork(WORK_NAME)
    }

    private fun startWearWorkListener() {
        workManager.enqueue(wearScoreboardWorker)

        workManager.getWorkInfosForUniqueWorkLiveData(WORK_NAME)
            .observe(this) { workInfos ->
                workInfos.forEach { workInfo ->
                    logAndroidMessage("Work status: ${workInfo.state}")
                }
            }
    }

    companion object {
        const val WORK_NAME = "dataChangeWork"
    }
}