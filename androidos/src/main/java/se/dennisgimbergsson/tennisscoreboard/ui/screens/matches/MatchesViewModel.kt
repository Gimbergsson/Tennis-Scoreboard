package se.dennisgimbergsson.tennisscoreboard.ui.screens.matches

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.extensions.logAndroidMessage
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.data.repositories.AppwriteDataSource
import se.dennisgimbergsson.tennisscoreboard.data.repositories.SavedMatchesDataSource
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import javax.inject.Inject

sealed interface MatchesUiEvent
data class MatchesUiClicked(val matchId: Int) : MatchesUiEvent

sealed interface MatchesSideEffects
data class MatchesClickedSideEffect(val matchId: Int) : MatchesSideEffects

data class MatchesState(
    val tennisMatches: Map<Int, Array<Scoreboard>> = emptyMap(),
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val appwriteRepository: AppwriteDataSource,
    private val savedMatchesRepository: SavedMatchesDataSource,
    private val gson: Gson,
) : ReduxViewModel<MatchesState>(
    initialState = MatchesState()
) {

    private val sideEffect = MutableSharedFlow<MatchesSideEffects>()
    val sideEffectFlow = sideEffect.asSharedFlow()

    init {
        startMatchesFlow()

        // TODO do something with the evaluation or AppWrite.
        viewModelScope.launch {
            val rowList = appwriteRepository.getScoreboardData()
            rowList?.rows?.forEach {
                it.data.values.forEach { value ->
                    if (value is String) {
                        logAndroidMessage(message = value)
                    }
                }
            }
        }
    }

    fun onEvents(event: MatchesUiEvent) = when (event) {
        is MatchesUiClicked -> {
            viewModelScope.launch {
                sideEffect.emit(MatchesClickedSideEffect(matchId = event.matchId))
            }
        }
    }

    private fun startMatchesFlow() = viewModelScope.launch {
        savedMatchesRepository.getSavedMatchesData().collect {
            setState {
                copy(
                    tennisMatches = it.associate { savedMatches ->
                        savedMatches.id to gson.fromJson(
                            savedMatches.jsonData,
                            Array<Scoreboard>::class.java
                        )
                    },
                )
            }
        }
    }
}