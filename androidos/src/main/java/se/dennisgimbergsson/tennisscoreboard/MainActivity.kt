package se.dennisgimbergsson.tennisscoreboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.tennisscoreboard.databinding.ActivityMainBinding
import se.dennisgimbergsson.tennisscoreboard.services.ScoreboardWorker

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val wearScoreboardWorker = OneTimeWorkRequestBuilder<ScoreboardWorker>()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        startWearWorkListener()
    }

    private fun startWearWorkListener() {
        val workManager = WorkManager.getInstance(this)

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