package se.dennisgimbergsson.shared.utils

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "DebounceTag"
var lastClickTime: Long = 0

/**
 * Debouncing  an action if called within the debounceTime.
 * @param debounceTime The time in milliseconds to wait before executing the action.
 * @param action The action to be executed.
 */
fun debouncedAction(
    debounceTime: Long = 500L,
    action: () -> Unit,
) {
    when {
        SystemClock.elapsedRealtime() - lastClickTime < debounceTime -> {
            Log.i(TAG, "Action was debounced: $action")
        }

        else -> {
            lastClickTime = SystemClock.elapsedRealtime()
            action()
        }
    }
}

/**
 * Debouncing  an action if called within the debounceTime.
 * @param debounceTime The time in milliseconds to wait after an action before it can be executed again.
 * @param action The action to be executed with a boolean result indicating if the action was debounced .
 */
fun debouncedActionWithResult(
    debounceTime: Long = 500L,
    action: (Boolean) -> Unit,
) {
    when {
        SystemClock.elapsedRealtime() - lastClickTime < debounceTime -> {
            Log.i(TAG, "Action was debounced: $action")
            action(true)
        }

        else -> {
            lastClickTime = SystemClock.elapsedRealtime()
            action(false)
        }
    }
}

/**
 * Debouncing  an action if called within the debounceTime.
 * @param coroutineScope The coroutine scope to launch the action in.
 * @param action The action to be executed. Action also receives a parameter of type T.
 * @return A suspended function that launches a coroutine to execute the action with parameter if job is not active.
 */
fun <T> debouncedActiveJob(
    coroutineScope: CoroutineScope,
    action: suspend (T) -> Unit,
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (debounceJob?.isActive == true) {
            Log.i("TAG", "Job is active and was debounced: $action")
        } else {
            debounceJob = coroutineScope.launch {
                action(param)
            }
        }
    }
}

/**
 * Debouncing new job if exiting is active.
 * @param coroutineScope The coroutine scope to launch the action in.
 * @param action The action to be executed.
 * @return A suspended function that launches a coroutine to execute the action if job is not active.
 */
fun debouncedActiveJob(
    coroutineScope: CoroutineScope,
    action: suspend () -> Unit,
): () -> Unit {
    var debounceJob: Job? = null
    return {
        if (debounceJob?.isActive == true) {
            Log.i("TAG", "Job is active and was debounced: $action")
        } else {
            debounceJob = coroutineScope.launch {
                action()
            }
        }
    }
}