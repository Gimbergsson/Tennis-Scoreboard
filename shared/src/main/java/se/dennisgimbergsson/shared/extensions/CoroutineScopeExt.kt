package se.dennisgimbergsson.shared.extensions

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun CoroutineScope.launchWithDelay(
    delayMillis: Long = 300L,
    action: () -> Unit
): Job = this.launch {
    delay(delayMillis)
    action()
}

/**
 * Debounce action if coroutine job is active
 */
fun CoroutineScope.debouncedCoroutineAction(
    action: () -> Unit
) {
    when {
        this.isActive -> {
            Log.i("TAG", "Job is active and was debounced: $action")
        }

        else -> action()
    }
}