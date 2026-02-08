package se.dennisgimbergsson.tennisscoreboard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.tennisscoreboard.data.repositories.AppwriteDataSource
import se.dennisgimbergsson.tennisscoreboard.data.repositories.AppwriteRepository
import se.dennisgimbergsson.tennisscoreboard.data.repositories.SavedMatchesDataSource
import se.dennisgimbergsson.tennisscoreboard.data.repositories.SavedMatchesRepository
import se.dennisgimbergsson.tennisscoreboard.data.repositories.ScoreDataSource
import se.dennisgimbergsson.tennisscoreboard.data.repositories.ScoreRepository
import se.dennisgimbergsson.shared.utils.ResourceProvider
import se.dennisgimbergsson.shared.utils.ResourceProviderSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(resourceProvider: ResourceProvider): ResourceProviderSource

    @Binds
    @Singleton
    abstract fun bindScoreRepository(scoreRepository: ScoreRepository): ScoreDataSource

    @Binds
    @Singleton
    abstract fun bindSavedMatchesRepository(savedMatchesRepository: SavedMatchesRepository): SavedMatchesDataSource

    @Binds
    @Singleton
    abstract fun bindAppwriteDataSource(appwriteRepository: AppwriteRepository): AppwriteDataSource
}