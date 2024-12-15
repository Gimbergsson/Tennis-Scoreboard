package se.dennisgimbergsson.tennisscoreboard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesDataSource
import se.dennisgimbergsson.tennisscoreboard.repositories.SharedPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMainDataSource(sharedPreferencesRepository: SharedPreferencesRepository): SharedPreferencesDataSource
}
