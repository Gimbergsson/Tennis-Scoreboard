package se.dennisgimbergsson.tennisscoring.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.tennisscoring.TennisScoreManager
import se.dennisgimbergsson.tennisscoring.TennisScoreManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    @Singleton
    abstract fun bindsTennisScoreManager(tennisScoreManager: TennisScoreManagerImpl): TennisScoreManager
}