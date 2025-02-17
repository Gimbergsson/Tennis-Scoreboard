package se.dennisgimbergsson.shared.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.shared.GameScoresDeserializer
import se.dennisgimbergsson.shared.enums.GameScores

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(GameScores::class.java, GameScoresDeserializer())
        .create()
}