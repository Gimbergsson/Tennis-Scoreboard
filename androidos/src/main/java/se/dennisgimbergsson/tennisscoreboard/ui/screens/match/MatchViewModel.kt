package se.dennisgimbergsson.tennisscoreboard.ui.screens.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import se.dennisgimbergsson.shared.utils.ReduxViewModel
import se.dennisgimbergsson.tennisscoreboard.data.repositories.SavedMatchesDataSource
import se.dennisgimbergsson.tennisscoring.data.Points
import se.dennisgimbergsson.tennisscoring.data.models.Scoreboard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/*sealed interface MatchEvent
data class MatchClicked(val id: Int) : MatchEvent

sealed interface MatchSideEffects
data object MatchClickedSideEffect : MatchSideEffects*/

data class MatchState(
    val matchId: Int = -1,
    val match: Scoreboard = Scoreboard(),
    val matchHistory: List<Scoreboard> = listOf(),
    val matchCreatedAt: String = "",
    val deuceCount: Int = 0,
) {

}

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val savedMatchesRepository: SavedMatchesDataSource,
    private val gson: Gson,
    savedStateHandle: SavedStateHandle,
) : ReduxViewModel<MatchState>(
    initialState = MatchState(
        matchId = savedStateHandle.get<Int>(ARG_MATCH_ID) ?: -1,
    )
) {

    /*private val sideEffect = MutableSharedFlow<MatchSideEffects>()
    val sideEffectFlow = sideEffect.asSharedFlow()*/

    init {
        viewModelScope.launch {
            /*savedMatchesRepository.getSavedMatchesData()
                .mapNotNull {
                    val json = it.first().jsonData

                    gson.fromJson(json, Array<Scoreboard>::class.java)?.toList()
                        //?: emptyList()).find { it.matchStartedAt == currentState().matchId }

                }.collect { matchHistory ->
                    setState {
                        val match = matchHistory.find { it.matchStartedAt == currentState().matchId } ?: Scoreboard()
                        copy(
                            match = match,
                            matchHistory = matchHistory
                        )
                    }
                }*/
            savedMatchesRepository.getSavedMatchesData()
                .mapNotNull {
                    it.find { it.id == currentState().matchId }
                }.collect {
                    val json = it.jsonData
                    val matchHistory = gson.fromJson(json, Array<Scoreboard>::class.java)?.toList() ?: emptyList()
                    val matchCreatedAt = formatMillisToDateTime(matchHistory.last().matchStartedAt)
                    val deuceCount = matchHistory.count {
                        it.homeScore.points == Points.FORTY && it.awayScore.points == Points.FORTY
                    }
                    setState {
                        copy(
                            match = matchHistory.last(),
                            matchHistory = matchHistory,
                            matchCreatedAt = matchCreatedAt,
                            deuceCount = deuceCount,
                        )
                    }
                }
        }
    }

    /*fun onEvents(event: MatchEvent) = when (event) {
        is MatchClicked -> {
            viewModelScope.launch {
                sideEffect.emit(MatchClickedSideEffect)
            }
            logAndroidMessage("Match clicked")
        }

        else -> {
            logAndroidMessage("Unknown event")
        }
    }*/

    fun formatMillisToDateTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm yyyy-MM-dd", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    companion object {
        const val ARG_MATCH_ID = "arg_match_id"
    }
}
