package se.dennisgimbergsson.tennisscoreboard.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.tennisscoreboard.data.database.ScoreDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun scoreDatabase(@ApplicationContext context: Context): ScoreDatabase = Room.databaseBuilder(
        context,
        ScoreDatabase::class.java, "score_database"
    ).build()

    @Singleton
    @Provides
    fun provideCurrentScoreDao(scoreDatabase: ScoreDatabase) = scoreDatabase.currentScoreDao()


    @Singleton
    @Provides
    fun provideSavedMatchesDao(scoreDatabase: ScoreDatabase) = scoreDatabase.savedMatchesDao()
}