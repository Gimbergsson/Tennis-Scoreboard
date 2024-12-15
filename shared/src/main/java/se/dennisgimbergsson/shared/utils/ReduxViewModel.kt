package se.dennisgimbergsson.shared.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KProperty1

abstract class ReduxViewModel<S>(
    initialState: S
) : ViewModel() {
    private val state = MutableStateFlow(initialState)
    private val stateMutex = Mutex()

    fun currentState(): S = state.value

    val liveData: LiveData<S>
        get() = state.asLiveData()

    val stateFlow: StateFlow<S>
        get() = state

    protected suspend fun <T> Flow<T>.collectAndSetState(reducer: S.(T) -> S) {
        return collect { item -> setState { reducer(item) } }
    }

    fun <A> selectObserve(prop1: KProperty1<S, A>): LiveData<A> {
        return selectSubscribe(prop1).asLiveData()
    }

    protected fun subscribe(block: (S) -> Unit) {
        viewModelScope.launch {
            state.collect { block(it) }
        }
    }

    protected fun <A> selectSubscribe(prop1: KProperty1<S, A>, block: (A) -> Unit) {
        viewModelScope.launch {
            selectSubscribe(prop1).collect { block(it) }
        }
    }

    private fun <A> selectSubscribe(prop1: KProperty1<S, A>): Flow<A> = try {
        state.map { prop1.get(it) }.distinctUntilChanged()
    } catch (e: Exception) {
        Log.e(TAG, "selectSubscribe", e)
        emptyFlow()
    }

    protected suspend fun setState(reducer: S.() -> S) = try {
        stateMutex.withLock {
            state.value = reducer(state.value)
        }
    } catch (e: Exception) {
        Log.e(TAG, "setState", e)
    }

    protected fun CoroutineScope.launchSetState(reducer: S.() -> S) {
        launch { this@ReduxViewModel.setState(reducer) }
    }

    protected suspend fun withState(block: (S) -> Unit) {
        stateMutex.withLock {
            block(state.value)
        }
    }

    protected fun CoroutineScope.withState(block: (S) -> Unit) {
        launch { this@ReduxViewModel.withState(block) }
    }

    companion object {
        const val TAG = "tag_redux_view_model"
    }
}
