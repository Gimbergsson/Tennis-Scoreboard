package se.dennisgimbergsson.tennisscoreboard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.appwrite.Client
import javax.inject.Inject

@HiltAndroidApp
class TennisScoreApp : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var appwriteClient: Client


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        appwriteClient.setProject("6716c8cf001419f170c3")
    }
}


/*
@HiltAndroidApp
class TennisScoreApp : Application(), Configuration.Provider{

    @Inject
    lateinit var workerFactory : HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}*/
