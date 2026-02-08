package se.dennisgimbergsson.shared.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.core.text.parseAsHtml
import androidx.core.text.toHtml
import androidx.core.text.toSpanned
import se.dennisgimbergsson.shared.utils.LocaleUtils

@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.vibrateOnce() {
    val vibrationEffect = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE)
    when {
        SDK_INT >= Build.VERSION_CODES.S -> {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(vibrationEffect)
        }

        else -> {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(vibrationEffect)
        }
    }
}

fun Context.isValidGlideContext() =
    this !is Activity || (!isDestroyed && !isFinishing) // https://stackoverflow.com/a/61682571

fun Context.getFormattedText(@StringRes id: Int, vararg formatArgs: Any?): CharSequence =
    resources.getText(id).toSpanned().toHtml().format(*formatArgs).parseAsHtml()

fun Context.openLink(url: String?) {
    if (url.isNullOrEmpty()) return

    when {
        url.isNotEmpty() -> {
            val uri = url.toUri()
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            this.startActivity(browserIntent)
        }
    }
}

fun Context.getLocalizedResources(): Resources = createConfigurationContext(
    Configuration(resources.configuration).apply {
        setLocale(LocaleUtils.getApplicationLocale())
    }
).resources