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
import se.dennisgimbergsson.shared.utils.DefaultDispatcherProvider
import se.dennisgimbergsson.shared.utils.DispatcherProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(GameScores::class.java, GameScoresDeserializer())
        .create()
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun provideWearableDataClient(@ApplicationContext context: Context): DataClient =
        Wearable.getDataClient(context)

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}