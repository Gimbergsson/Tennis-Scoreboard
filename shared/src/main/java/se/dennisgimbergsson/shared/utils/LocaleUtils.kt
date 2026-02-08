package se.dennisgimbergsson.shared.utils

import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import se.dennisgimbergsson.shared.utils.Constants.Locales.DefaultAppLocale

object LocaleUtils {
    // Use device locale as fallback.
    fun getApplicationLocale() =
        AppCompatDelegate.getApplicationLocales().get(0) ?: getDeviceLocale()

    fun getDeviceLocale() = Resources.getSystem()?.configuration?.locales?.get(0) ?: DefaultAppLocale

    fun getDeviceCountryCode(): String = getDeviceLocale().country

    fun getDeviceLanguageCode(): String = getDeviceLocale().language
}