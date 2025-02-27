package se.dennisgimbergsson.shared.di

import android.content.Context
import androidx.work.WorkManager
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.shared.GameScoresDeserializer
import se.dennisgimbergsson.shared.enums.GameScores
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(GameScores::class.java, GameScoresDeserializer())
        .create()

    @Singleton
    @Provides
    fun workManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun wearableDataClient(@ApplicationContext context: Context): DataClient =
        Wearable.getDataClient(context)
}