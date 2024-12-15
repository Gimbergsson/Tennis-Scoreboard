package se.dennisgimbergsson.shared.extensions

import android.util.Log
import se.dennisgimbergsson.shared.utils.Constants
import java.lang.Exception

fun Any.logWearMessage(
    message: String,
    exception: Exception? = null,
) {
    Log.d(Constants.Debug.WEAR_TAG, message, exception)
}

fun Any.logAndroidMessage(
    message: String,
    exception: Exception? = null,
) {
    Log.d(Constants.Debug.ANDROID_TAG, message, exception)
}