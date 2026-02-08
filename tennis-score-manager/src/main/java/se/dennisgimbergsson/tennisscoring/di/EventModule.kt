package se.dennisgimbergsson.tennisscoring.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import javax.inject.Singleton

sealed class TennisScoringEvent
data class ScoreUpdateEvent(
    val scoreboard: Scoreboard,
    val addToHistory: Boolean = true
) : TennisScoringEvent()
typealias TennisScoringEvents = MutableSharedFlow<TennisScoringEvent>

@Module
@InstallIn(SingletonComponent::class)
class EventsModule {

    @Provides
    @Singleton
    fun tennisScoringEvents(): TennisScoringEvents = MutableSharedFlow()
}